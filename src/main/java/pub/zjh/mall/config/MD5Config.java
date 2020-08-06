package pub.zjh.mall.config;

import lombok.Data;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.DigestUtils;
import pub.zjh.mall.util.CompositePropertySourceFactory;

import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource(value = "classpath:application.yml", factory = CompositePropertySourceFactory.class)
@Data
public class MD5Config {

    @Value("${password.salt}")
    private String passwordSalt;

    public String md5EncodeAddSalt(String str) {
        return DigestUtils.md5DigestAsHex((str + passwordSalt).getBytes(StandardCharsets.UTF_8));
    }

    public String md5Encode(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        MD5Config md5Config = new MD5Config();
        md5Config.setPasswordSalt("geelysdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.,");
        System.out.println(md5Config.md5EncodeAddSalt("admin123"));
        Md5Hash md5Hash = new Md5Hash("admin123", "geelysdafaqj23ou89ZXcj@#$@#$#@KJdjklj;D../dSF.,");
        System.out.println(md5Hash);
    }

}
