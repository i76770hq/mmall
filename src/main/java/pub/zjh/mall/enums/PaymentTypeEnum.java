package pub.zjh.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.zjh.mall.exception.MallException;

/**
 * 支付状态
 */
@Getter
@AllArgsConstructor
public enum PaymentTypeEnum {

    PAY_ONLINE(1, "在线支付"),
    ;

    Integer code;

    String desc;

    public static PaymentTypeEnum value(Integer code) {
        for (PaymentTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new MallException("没有该枚举");
    }

}
