package pub.zjh.mall.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class RestPasswordForm {

    @NotBlank
    private String username;

    @NotBlank
    @Length(min = 6, message = "密码长度必须大于6位")
    private String passwordNew;

    @NotBlank
    private String forgetToken;

}
