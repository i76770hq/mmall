package pub.zjh.mall.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRegisterForm {

    @NotBlank
    private String username;

    @NotBlank
    @Length(min = 6, message = "密码长度必须大于6位")
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    @Length(max = 20)
    private String question;

    @NotBlank
    @Length(max = 20)
    private String answer;

}
