package pub.zjh.mall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.config.PayConfig;
import pub.zjh.mall.consts.AlipayCallback;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.OrderItemMapper;
import pub.zjh.mall.dao.OrderMapper;
import pub.zjh.mall.dao.PayInfoMapper;
import pub.zjh.mall.enums.OrderStatusEnum;
import pub.zjh.mall.enums.PayPlatformEnum;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.pojo.Order;
import pub.zjh.mall.pojo.OrderItem;
import pub.zjh.mall.pojo.PayInfo;
import pub.zjh.mall.service.IFtpService;
import pub.zjh.mall.service.IPayService;
import pub.zjh.mall.util.BigDecimalUtil;
import pub.zjh.mall.util.DateTimeUtil;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PayServiceImpl implements IPayService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayConfig payConfig;

    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private IFtpService ftpService;

    @Autowired
    private AlipayTradeService alipayTradeService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public ResponseVo pay(Long orderNo, Integer userId) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }
        Map<String, Object> returnMap = Maps.newHashMap();
        returnMap.put("orderNo", order.getOrderNo().toString());


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "我的商场当面付扫码支付，订单号:" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单:" + outTradeNo + "购买商品共:" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail good = GoodsDetail.newInstance(orderItem.getProductId().toString(),
                    orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100.0)).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(good);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(payConfig.getCallbackUrl())//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);
        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);

        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                String path = servletContext.getRealPath(MallConst.QRCODE_UPLOAD_LOCAL_PATH);
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.setWritable(true);
                    dir.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path + File.separator + "qr-%s.png", response.getOutTradeNo());
                String fileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                log.info("生成二维码成功 filePath:" + filePath);

                File targetFile = new File(path, fileName);
                try {
                    ftpService.uploadFile(MallConst.QRCODE_UPLOAD_LOCAL_PATH.replace(File.separator, "/"), Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    throw new MallException(e);
                }
                //删除resources/upload/qrcode目录下文件
                targetFile.delete();

                String prefix = ftpConfig.getPrefix();
                String qrUrl = prefix + MallConst.QRCODE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/" + fileName;
                returnMap.put("qrUrl", qrUrl);
                return ResponseVo.success(returnMap);
            case FAILED:
                log.error("支付宝预下单失败!!! 订单号:" + orderNo);
                throw new MallException("支付宝预下单失败!!! 订单号:" + orderNo);
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!! 订单号:" + orderNo);
                throw new MallException("系统异常，预下单状态未知!!! 订单号:" + orderNo);
            default:
                log.error("不支持的交易状态，交易返回异常!!! 订单号:" + orderNo);
                throw new MallException("不支持的交易状态，交易返回异常!!! 订单号:" + orderNo);
        }
    }

    @Override
    public String alipayCallback(HttpServletRequest request) throws Exception {
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        Map<String, String> parameterMap = Maps.newHashMap();
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            String[] values = entry.getValue();
            String valueStr = Arrays.stream(values).collect(Collectors.joining(","));
            String name = entry.getKey();
            parameterMap.put(name, valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",
                parameterMap.get("sign"),
                parameterMap.get("trade_status"),
                parameterMap);

        //验证回调的正确性，是不是支付宝发的，并且还要避免重复通知
        parameterMap.remove("sign_type");

        boolean checkAlipay = AlipaySignature.rsaCheckV2(parameterMap, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
        if (!checkAlipay) {
            throw new MallException("非法请求,验证不通过,再恶意请求我就报警找网警了");
        }

        boolean isSuccess = orderStatusHandler(parameterMap);
        if (isSuccess) {
            return AlipayCallback.RESPONSE_SUCCESS;
        }
        return AlipayCallback.RESPONSE_FAILED;
    }

    private boolean orderStatusHandler(Map<String, String> parameterMap) {
        Long orderNo = Long.parseLong(parameterMap.get("out_trade_no"));
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(ResponseEnum.ORDER_NOT_EXIST.getDesc());
        }

        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()) {
            return true;
        }
        String tradeStatus = parameterMap.get("trade_status");
        if (AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(parameterMap.get("gmt_payment")));
            order.setStatus(OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(PayPlatformEnum.ALIPAY.getCode());
        String tradeNo = parameterMap.get("trade_no");
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return true;
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

}
