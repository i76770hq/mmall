package pub.zjh.mall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.zjh.mall.exception.MallException;

/**
 * 角色信息
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(0, "admin"),
    CUSTOMER(1, "customer"),
    ;

    Integer code;
    String desc;


    public static RoleEnum value(Integer code) {
        for (RoleEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new MallException("没有该枚举");
    }

}
