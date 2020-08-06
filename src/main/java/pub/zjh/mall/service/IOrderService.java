package pub.zjh.mall.service;

import com.github.pagehelper.PageInfo;
import pub.zjh.mall.vo.OrderProductVo;
import pub.zjh.mall.vo.OrderVo;
import pub.zjh.mall.vo.ResponseVo;

public interface IOrderService {

    public ResponseVo<Boolean> selectByOrderNoAndUserId(Long orderNo, Integer userId);

    public ResponseVo<OrderVo> create(Integer userId, Integer shippingId);

    public ResponseVo cancel(Long orderNo, Integer userId);

    public ResponseVo<OrderProductVo> getOrderCartProduct(Integer userId);

    public ResponseVo<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    public ResponseVo<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    //backend
    public ResponseVo<PageInfo> managelist(Integer pageNum, Integer pageSize);

    public ResponseVo<OrderVo> manageGetOrderDetail(Long orderNo);

    public ResponseVo<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize);

    public ResponseVo<String> manageSendGoods(Long orderNo);

}
