package pub.zjh.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayPlatformEnum {
    ALIPAY(1, "支付宝");

    private int code;
    private String msg;

}
