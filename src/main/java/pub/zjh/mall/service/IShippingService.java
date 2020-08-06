package pub.zjh.mall.service;

import com.github.pagehelper.PageInfo;
import pub.zjh.mall.form.ShippingForm;
import pub.zjh.mall.pojo.Shipping;
import pub.zjh.mall.vo.ResponseVo;

public interface IShippingService {

    public ResponseVo add(Integer userId, ShippingForm shippingForm);

    public ResponseVo del(Integer userId, Integer shippingId);

    public ResponseVo update(Integer userId, ShippingForm shippingForm);

    public ResponseVo<Shipping> select(Integer userId, Integer shippingId);

    public ResponseVo<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

}
