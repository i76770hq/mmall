package pub.zjh.mall.form;

import lombok.Data;

@Data
public class ShippingForm {

    private Integer id;

    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverMobile;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String receiverZip;

}
