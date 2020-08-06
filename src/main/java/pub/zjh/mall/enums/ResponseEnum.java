package pub.zjh.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum {


    SUCCESS(0, "成功"),

    ERROR(1, "服务端错误"),

    PARAM_ERROR(2, "参数错误"),

    USERNAME_EXIST(3, "用户名已存在"),

    PASSWORD_ERROR(4, "密码错误"),

    EMAIL_EXIST(5, "邮箱已存在"),

    QUESTION_ANSWER_ERROR(6, "问题的答案错误"),

    MODIFY_PASSWORD_ERROR(7, "修改密码失败"),

    UPDATE_USER_INFO_ERROR(8, "更新个人信息失败"),

    NOT_ADMIN_LOGIN_ERROR(9, "您无权登陆"),

    NEED_LOGIN(10, "用户未登录, 请先登录"),

    USERNAME_OR_PASSWORD_ERROR(11, "用户名或密码错误"),

    OLD_PASSWORD_ERROR(12, "旧密码错误"),

    NOT_ADMIN_NOT_PERSSION_ERROR(13, "不是管理员,无权限操作"),

    ADD_CATEGORY_ERROR(14, "添加品类失败"),

    UPDATE_CATEGORY_NAME_ERROR(15, "更新品类名字失败"),

    ADD_PRODUCT_ERROR(16, "添加商品失败"),

    UPDATE_PRODUCT_ERROR(17, "更新商品失败"),

    UPDATE_PRODUCT_STATUS_ERROR(18, "更新商品销售状态失败"),

    PRODUCT_OFF_SALE_OR_DELETE(32, "商品下架或删除"),

    PRODUCT_NOT_EXIST(33, "商品不存在"),

    PRODUCT_STOCK_ERROR(34, "库存不正确"),

    CART_PRODUCT_NOT_EXIST(35, "购物车里无此商品"),

    DELETE_SHIPPING_FAIL(36, "删除收货地址失败"),

    SHIPPING_NOT_EXIST(37, "收货地址不存在"),

    CART_SELECTED_IS_EMPTY(38, "请选择商品后下单"),

    ORDER_NOT_EXIST(39, "订单不存在"),

    ORDER_STATUS_ERROR(40, "订单状态有误"),

    ADD_SHIPPING_ERROR(41, "新建地址失败"),

    UPDATE_SHIPPING_FAIL(42, "更新收货地址失败"),

    REPEAT_INVO_ERROR(43, "支付宝重复调用"),

    ORDER_PAYED_NOT_CANCEL_ERROR(43, "订单已付款，无法取消"),

    ;

    Integer code;

    String desc;

}
