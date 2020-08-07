package pub.zjh.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.*;
import pub.zjh.mall.enums.OrderStatusEnum;
import pub.zjh.mall.enums.PaymentTypeEnum;
import pub.zjh.mall.enums.ProductStatusEnum;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.pojo.*;
import pub.zjh.mall.service.IOrderService;
import pub.zjh.mall.util.BigDecimalUtil;
import pub.zjh.mall.util.DateTimeUtil;
import pub.zjh.mall.vo.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BatchOperationService batchOperationService;

    @Autowired
    private FtpConfig ftpConfig;

    @Override
    public ResponseVo<Boolean> selectByOrderNoAndUserId(Long orderNo, Integer userId) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }

        if (order.getStatus() < OrderStatusEnum.PAID.getCode()) {
            return ResponseVo.success(false);
        }

        return ResponseVo.success(true);
    }

    @Override
    public ResponseVo<OrderVo> create(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
        if (shipping == null) {
            throw new MallException(ResponseEnum.SHIPPING_NOT_EXIST.getDesc());
        }
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        List<Cart> checkedCartList = cartList.stream().
                filter(cart -> cart.getChecked().equals(MallConst.CHECKED)).
                collect(Collectors.toList());
        if (CollectionUtils.isEmpty(checkedCartList)) {
            throw new MallException(ResponseEnum.CART_SELECTED_IS_EMPTY.getDesc());
        }
        Set<Integer> productIdSet = checkedCartList.stream().
                map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByIds(productIdSet);
        Map<Integer, Product> productMap = productList.stream().
                filter(product -> product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())).
                collect(Collectors.toMap(Product::getId, Function.identity()));

        Long orderNo = generateId();
        List<OrderItem> orderItemList = Lists.newArrayList();
        List<Product> updateProductList = Lists.newArrayList();
        for (Cart cart : checkedCartList) {
            Product product = productMap.get(cart.getProductId());
            if (product == null) {
                throw new MallException(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc());
            }

            if (cart.getQuantity() > product.getStock()) {
                throw new MallException(ResponseEnum.PRODUCT_STOCK_ERROR.getDesc());
            }
            //减库存
            product.setStock(product.getStock() - cart.getQuantity());

            OrderItem orderItem = assembleOrderItem(userId, orderNo, cart, product);
            orderItemList.add(orderItem);
            updateProductList.add(product);
        }

        //批量更新商品
        batchOperationService.batchUpdate(updateProductList, ProductMapper.class);

        //插入Order
        Order order = assembleOrder(userId, orderNo, shippingId, orderItemList);
        orderMapper.insert(order);
        //批量插入OrderItem
        orderItemMapper.batchInsert(orderItemList);

        //清空购物车
        cartMapper.deleteByUserIdAndProductIds(userId, productIdSet);

        //组装OrderVo对象
        OrderVo orderVo = assembleOrderVo(shipping, order, orderItemList);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Long orderNo, Integer userId) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())) {
            throw new MallException(ResponseEnum.ORDER_PAYED_NOT_CANCEL_ERROR.getDesc());
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row <= 0) {
            throw new MallException(ResponseEnum.ERROR.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<OrderProductVo> getOrderCartProduct(Integer userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(cartList)) {
            throw new MallException(ResponseEnum.CART_SELECTED_IS_EMPTY.getDesc());
        }
        List<Cart> checkedCartList = cartList.stream().
                filter(cart -> cart.getChecked().equals(MallConst.CHECKED)).
                collect(Collectors.toList());

        Set<Integer> productIdSet = checkedCartList.stream().
                map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByIds(productIdSet);
        Map<Integer, Product> productMap = productList.stream().
                filter(product -> product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())).
                collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItemList = Lists.newArrayList();
        BigDecimal productTotalPrice = BigDecimal.ZERO;
        for (Cart cart : checkedCartList) {
            Product product = productMap.get(cart.getProductId());
            if (product == null) {
                throw new MallException(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc());
            }

            if (cart.getQuantity() > product.getStock()) {
                throw new MallException(ResponseEnum.PRODUCT_STOCK_ERROR.getDesc());
            }

            OrderItem orderItem = assembleOrderItem(userId, null, cart, product);
            orderItemList.add(orderItem);

            productTotalPrice = BigDecimalUtil.add(orderItem.getTotalPrice().doubleValue(), productTotalPrice.doubleValue());
        }

        List<OrderItemVo> orderItemVoList = orderItemList.stream()
                .map(orderItem -> assembleOrderItemVo(orderItem))
                .collect(Collectors.toList());

        OrderProductVo orderProductVo = new OrderProductVo();
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(productTotalPrice);
        orderProductVo.setImageHost(ftpConfig.getPrefix() + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/");
        return ResponseVo.success(orderProductVo);
    }

    @Override
    public ResponseVo<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = assembleOrderVo(shipping, order, orderItemList);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        //组装OrderVoList
        List<OrderVo> orderVoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderVoList = assembleOrderVoList(orderList);
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> managelist(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAll();
        //组装OrderVoList
        List<OrderVo> orderVoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderVoList = assembleOrderVoList(orderList);
        }
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> manageGetOrderDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(Lists.newArrayList(orderNo));
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = assembleOrderVo(shipping, order, orderItemList);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(Lists.newArrayList(orderNo));
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = assembleOrderVo(shipping, order, orderItemList);

        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        if (!order.getStatus().equals(OrderStatusEnum.PAID.getCode())) {
            throw new MallException(ResponseEnum.ORDER_STATUS_ERROR.getDesc());
        }
        order.setStatus(OrderStatusEnum.SHIPPED.getCode());
        order.setSendTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
        return ResponseVo.success();
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList) {
        List<Long> orderNoList = orderList.stream().
                map(Order::getOrderNo)
                .collect(Collectors.toList());
        List<OrderItem> orderItemAllList = orderItemMapper.selectByOrderNo(orderNoList);
        Map<Long, List<OrderItem>> orderItemByOrderNoMap = orderItemAllList.stream().
                collect(Collectors.groupingBy(OrderItem::getOrderNo));

        List<Integer> shippingIdList = orderList.stream().
                map(Order::getShippingId).
                collect(Collectors.toList());
        List<Shipping> shippingList = shippingMapper.selectByShippingIds(shippingIdList);
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, Function.identity()));
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            Shipping shipping = shippingMap.get(order.getShippingId());
            List<OrderItem> orderItemList = orderItemByOrderNoMap.get(order.getOrderNo());
            OrderVo orderVo = assembleOrderVo(shipping, order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    private Long generateId() {
        return System.currentTimeMillis() + new Random().nextInt(100);
    }

    private OrderVo assembleOrderVo(Shipping shipping, Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        orderVo.setPaymentTypeDesc(PaymentTypeEnum.value(orderVo.getPaymentType()).getDesc());
        orderVo.setStatusDesc(OrderStatusEnum.value(order.getStatus()).getDesc());
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        List<OrderItemVo> orderItemVoList = orderItemList.stream().
                map(orderItem -> assembleOrderItemVo(orderItem))
                .collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);

        orderVo.setImageHost(ftpConfig.getPrefix() + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/");
        if (shipping != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setReceiverName(shipping.getReceiverName());
            ShippingVo shippingVo = new ShippingVo();
            BeanUtils.copyProperties(shipping, shippingVo);
            orderVo.setShippingVo(shippingVo);
        }
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        BeanUtils.copyProperties(orderItem, orderItemVo);
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private Order assembleOrder(Integer userId, Long orderNo, Integer shippingId, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        BigDecimal payment = orderItemList.stream().
                map(OrderItem::getTotalPrice).
                reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setPayment(payment);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }

    private OrderItem assembleOrderItem(Integer userId, Long orderNo, Cart cart, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(cart.getQuantity());
        orderItem.setTotalPrice(BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuantity().doubleValue()));
        return orderItem;
    }


}
