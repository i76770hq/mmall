package pub.zjh.mall.shiro;

import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import pub.zjh.mall.dao.UserMapper;
import pub.zjh.mall.enums.RoleEnum;

import java.util.Set;

@Data
public class CustomRealm extends AuthorizingRealm {

    private UserMapper userMapper;


    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 从数据库获取对应用户名密码的用户
        String username = token.getUsername();
        String password = userMapper.selectPasswordByUsername(username);
        if (null == password) {
            return null;
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username,
                password, this.getClass().getName());
//        info.setCredentialsSalt(ByteSource.Util.bytes(md5Config.getPasswordSalt()));
        return info;
    }

    /**
     * 获取授权信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        //获得该用户角色
        int role = userMapper.selectRoleByUsername(username);
        //需要将 role 封装到 Set 作为 info.setRoles() 的参数
        Set<String> roleSet = Sets.newHashSet(RoleEnum.value(role).getDesc());
        //设置该用户拥有的角色
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleSet);
        return info;
    }
}
