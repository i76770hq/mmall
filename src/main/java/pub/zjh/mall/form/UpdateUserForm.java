package pub.zjh.mall.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserForm {

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
