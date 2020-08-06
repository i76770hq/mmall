package pub.zjh.mall.service;

import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.http.HttpServletRequest;

public interface IPayService {

    public ResponseVo pay(Long orderNo, Integer userId);

    public String alipayCallback(HttpServletRequest request) throws Exception;

}
