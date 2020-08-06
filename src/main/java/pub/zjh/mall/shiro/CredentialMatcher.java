package pub.zjh.mall.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pub.zjh.mall.config.MD5Config;

@Component
public class CredentialMatcher extends SimpleCredentialsMatcher {

    @Autowired
    private MD5Config md5Config;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String password = new String(usernamePasswordToken.getPassword());
        String dbPassword = (String) info.getCredentials();
        //密码MD5处理
        String md5Password = md5Config.md5EncodeAddSalt(password);
        return this.equals(md5Password, dbPassword);
    }

}
