package pub.zjh.mall.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class QuestionAnswerForm {

    @NotBlank
    private String username;

    @NotBlank
    @Length(max = 20)
    private String question;

    @NotBlank
    @Length(max = 20)
    private String answer;

}
