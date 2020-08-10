package pub.zjh.mall.consts;

import com.google.common.collect.Sets;

import java.io.File;
import java.util.Set;

public class MallConst {

    public static final String CURRENT_USER = "currentUser";

    public static Integer ROOT_PARENT_ID = 0;

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String IMAGE_UPLOAD_LOCAL_PATH = "mall" + File.separator + "upload" + File.separator + "img";
    public static final String QRCODE_UPLOAD_LOCAL_PATH = "mall" + File.separator + "upload" + File.separator + "qrcode";

    public static final Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");

    public static final int CHECKED = 1;//即购物车选中状态
    public static final int UN_CHECKED = 0;//购物车中未选中状态

    public static final String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
    public static final String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";


    //Redis Session过期时间
    public static final Integer REDIS_SESSION_EXPIRE = 30 * 60;

}
