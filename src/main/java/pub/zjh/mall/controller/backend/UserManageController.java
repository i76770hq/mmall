package pub.zjh.mall.controller.backend;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.form.UserLoginForm;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService userService;

    /**
     * 管理员登陆
     *
     * @param loginForm
     * @param session
     * @return
     */
    @PostMapping("login.do")
    public ResponseVo login(@Valid UserLoginForm loginForm,
                            HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginForm.getUsername(), loginForm.getPassword());
        subject.login(token);
        ResponseVo<User> responseVo = userService.login(loginForm.getUsername(), loginForm.getPassword());
        session.setAttribute(MallConst.CURRENT_USER, responseVo.getData());
        return responseVo;
    }

}
