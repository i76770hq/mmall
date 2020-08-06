package pub.zjh.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.dao.ShippingMapper;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.form.ShippingForm;
import pub.zjh.mall.pojo.Shipping;
import pub.zjh.mall.service.IShippingService;
import pub.zjh.mall.vo.ResponseVo;

import java.util.List;
import java.util.Map;

@Service
public class IShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo add(Integer userId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(userId);
        int row = shippingMapper.insert(shipping);
        if (row <= 0) {
            throw new MallException(ResponseEnum.ADD_SHIPPING_ERROR.getDesc());
        }
        Map<String, Object> returnMap = Maps.newHashMap();
        returnMap.put("shippingId", shipping.getId());
        return ResponseVo.success(returnMap);
    }

    @Override
    public ResponseVo del(Integer userId, Integer shippingId) {
        int row = shippingMapper.deleteByShippingIdAndUserId(shippingId, userId);
        if (row <= 0) {
            throw new MallException(ResponseEnum.DELETE_SHIPPING_FAIL.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer userId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm, shipping);
        shipping.setUserId(userId);
        int row = shippingMapper.updateByShippingIdAndUserId(shipping);
        if (row <= 0) {
            throw new MallException(ResponseEnum.UPDATE_SHIPPING_FAIL.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
        if (shipping == null) {
            throw new MallException(ResponseEnum.SHIPPING_NOT_EXIST.getDesc());
        }
        return ResponseVo.success(shipping);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ResponseVo.success(pageInfo);
    }


}
