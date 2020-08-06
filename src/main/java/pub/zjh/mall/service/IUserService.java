package pub.zjh.mall.service;

import pub.zjh.mall.form.UpdateUserForm;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.vo.ResponseVo;

public interface IUserService {

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @return
     */
    public ResponseVo<User> login(String username, String password);

    /**
     * 注册
     *
     * @param user
     * @return
     */
    public ResponseVo register(User user);

    /**
     * 检查用户名或者email是否有效
     *
     * @param str
     * @param type
     * @return
     */
    public ResponseVo checkValid(String str, String type);

    /**
     * 忘记密码,获取问题
     *
     * @param username
     * @return
     */
    public ResponseVo selectQuestion(String username);

    /**
     * 检查问题答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ResponseVo checkAnswer(String username, String question, String answer);

    /**
     * 忘记密码的重设密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    public ResponseVo forgetRestPassword(String username, String passwordNew, String forgetToken);

    /**
     * 登录中状态重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    public ResponseVo restPassword(String passwordOld, String passwordNew, User user);

    /**
     * 登录状态更新个人信息
     *
     * @param updateUserForm
     * @param currentUser
     * @return
     */
    public ResponseVo updateInformation(UpdateUserForm updateUserForm, User currentUser);

    /**
     * 检查是否为管理员角色
     * @param user
     */
    public void checkAdminRole(User user);

}
