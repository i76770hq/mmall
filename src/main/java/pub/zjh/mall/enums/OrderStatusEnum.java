package pub.zjh.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.zjh.mall.exception.MallException;

@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    CANCELED(0, "已取消"),
    NO_PAY(10, "未付款"),
    PAID(20, "已付款"),
    SHIPPED(40, "已发货"),
    ORDER_SUCCESS(50, "订单完成"),
    ORDER_CLOSE(60, "订单关闭"),
    ;

    private Integer code;
    private String desc;

    public static OrderStatusEnum value(Integer code) {
        for (OrderStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new MallException("没有该枚举");
    }

}
