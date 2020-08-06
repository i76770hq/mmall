package pub.zjh.mall.controller.portal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.form.*;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/user/")
@Validated
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登陆
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

    /**
     * 登出
     *
     * @param session
     * @returxxn
     */
    @PostMapping("logout.do")
    public ResponseVo logout(HttpSession session) {
//        session.removeAttribute(MallConst.CURRENT_USER);
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResponseVo.success();
    }

    /**
     * 注册
     *
     * @param userRegisterForm
     * @return
     */
    @PostMapping("register.do")
    public ResponseVo register(@Valid UserRegisterForm userRegisterForm) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        ResponseVo responseVo = userService.register(user);
        return responseVo;
    }

    /**
     * 检查用户名或者email是否有效
     *
     * @param str
     * @param type
     * @return
     */
    @PostMapping("check_valid.do")
    public ResponseVo checkValid(@NotBlank String str, @NotBlank String type) {
        ResponseVo responseVo = userService.checkValid(str,
                type);
        return responseVo;
    }

    /**
     * 获取登录用户信息
     *
     * @param sessionUser
     * @return
     */
    @PostMapping("get_user_info.do")
    @RequiresRoles("customer")
    public ResponseVo getUserInfo(User sessionUser) {
        return ResponseVo.success(sessionUser);
    }

    /**
     * 忘记密码,获取问题
     *
     * @param username
     * @return
     */
    @PostMapping("forget_get_question.do")
    public ResponseVo forgetGetQuestion(@RequestParam("username") String username) {
        ResponseVo responseVo = userService.selectQuestion(username);
        return responseVo;
    }

    /**
     * 检查问题答案
     *
     * @param questionAnswerForm
     * @return
     */
    @PostMapping("forget_check_answer.do")
    public ResponseVo forgetCheckAnswer(@Valid QuestionAnswerForm questionAnswerForm) {
        ResponseVo responseVo = userService.checkAnswer(questionAnswerForm.getUsername(),
                questionAnswerForm.getQuestion(),
                questionAnswerForm.getAnswer());
        return responseVo;
    }

    /**
     * 忘记密码的重设密码
     *
     * @param restPasswordForm
     * @return
     */
    @PostMapping("forget_reset_password.do")
    public ResponseVo forgetRestPassword(@Valid RestPasswordForm restPasswordForm) {
        ResponseVo responseVo = userService.forgetRestPassword(restPasswordForm.getUsername(),
                restPasswordForm.getPasswordNew(),
                restPasswordForm.getForgetToken());
        return responseVo;
    }

    /**
     * 登录中状态重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @param sessionUser
     * @return
     */
    @PostMapping("reset_password.do")
    @RequiresRoles("customer")
    public ResponseVo restPassword(@NotBlank String passwordOld,
                                   @NotBlank String passwordNew,
                                   User sessionUser) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户的，所以要密码和id一起查询
        ResponseVo responseVo = userService.restPassword(passwordOld, passwordNew, sessionUser);
        return responseVo;
    }

    /**
     * 登录状态更新个人信息
     *
     * @param updateUserForm
     * @param session
     * @param sessionUser
     * @return
     */
    @PostMapping("update_information.do")
    @RequiresRoles("customer")
    public ResponseVo updateInformation(@Valid UpdateUserForm updateUserForm,
                                        HttpSession session,
                                        User sessionUser) {
        ResponseVo responseVo = userService.updateInformation(updateUserForm, sessionUser);
        session.setAttribute(MallConst.CURRENT_USER, responseVo.getData());
        return responseVo;
    }

    /**
     * 获取当前登录用户的详细信息，并强制登录
     *
     * @param sessionUser
     * @return
     */
    @PostMapping("get_information.do")
//    @RequiresRoles("customer")
    public ResponseVo getInformation(User sessionUser) {

        return ResponseVo.success(sessionUser);
    }

//    @InitBinder("sessionUser")
//    public void ignoreUser(WebDataBinder binder) {
//        binder.setAllowedFields();
//    }

}
