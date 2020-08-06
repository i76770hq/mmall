package pub.zjh.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductForm {

    private Integer id;

    @NotNull
    private Integer categoryId;

    @NotBlank
    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private String detail;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    private Integer status;

}