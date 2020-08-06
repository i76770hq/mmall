package pub.zjh.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ftp")
@Data
public class FtpConfig {

    private String user;

    private String pass;

    private String ip;

    private String port;

    private String prefix;


}
