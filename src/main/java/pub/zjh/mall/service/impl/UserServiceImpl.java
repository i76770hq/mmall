package pub.zjh.mall.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.config.MD5Config;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.UserMapper;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.enums.RoleEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.form.UpdateUserForm;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.util.RedisPoolUtil;
import pub.zjh.mall.vo.ResponseVo;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MD5Config md5Config;

    @Autowired
    private RedisPoolUtil redisPoolUtil;

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new MallException(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        }
        //密码MD5处理
        String md5Password = md5Config.md5EncodeAddSalt(password);
        if (!user.getPassword().equalsIgnoreCase(md5Password)) {
            //密码错误,返回用户名或密码错误
            throw new MallException(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }

    @Override
    public ResponseVo register(User user) {
        int usernameCount = userMapper.countByUsername(user.getUsername());
        if (usernameCount > 0) {
            throw new MallException(ResponseEnum.USERNAME_EXIST.getDesc());
        }

        int emailCount = userMapper.countByEmail(user.getEmail());
        if (emailCount > 0) {
            throw new MallException(ResponseEnum.EMAIL_EXIST.getDesc());
        }

        //MD5摘要算法
        String md5Password = md5Config.md5EncodeAddSalt(user.getPassword());
        user.setPassword(md5Password);

        user.setRole(RoleEnum.CUSTOMER.getCode());
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo checkValid(String str, String type) {

        if (type.equals(MallConst.USERNAME)) {
            int count = userMapper.countByUsername(str);
            if (count > 0) {
                throw new MallException(ResponseEnum.USERNAME_EXIST.getDesc());
            }
        }

        if (type.equals(MallConst.EMAIL)) {
            int count = userMapper.countByEmail(str);
            if (count > 0) {
                throw new MallException(ResponseEnum.EMAIL_EXIST.getDesc());
            }
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo selectQuestion(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new MallException(ResponseEnum.USERNAME_OR_PASSWORD_ERROR.getDesc());
        }
        String question = user.getQuestion();
        return ResponseVo.success(question);
    }

    @Override
    public ResponseVo checkAnswer(String username, String question, String answer) {

        int count = userMapper.checkAnswer(username, question, answer);
        if (count <= 0) {
            throw new MallException(ResponseEnum.QUESTION_ANSWER_ERROR.getDesc());
        }
        String forgetToken = UUID.randomUUID().toString();
        redisPoolUtil.setEx(MallConst.TOKEN_PREFIX + username, forgetToken, MallConst.TOKEN_EXPIRE);
        return ResponseVo.success(forgetToken);
    }

    @Override
    public ResponseVo forgetRestPassword(String username, String passwordNew, String forgetToken) {
        String token = redisPoolUtil.get(MallConst.TOKEN_PREFIX + username);

        if (forgetToken.equals(token)) {
            String newMd5Password = md5Config.md5EncodeAddSalt(passwordNew);
            int count = userMapper.updatePasswordByUsername(username, newMd5Password);
            if (count > 0) {
                return ResponseVo.successByMsg("修改密码成功");
            }
        }
        throw new MallException(ResponseEnum.MODIFY_PASSWORD_ERROR.getDesc());
    }

    @Override
    public ResponseVo restPassword(String passwordOld, String passwordNew, User user) {
        String oldMd5Password = md5Config.md5EncodeAddSalt(passwordOld);
        int count = userMapper.countByUsernameAndPassword(user.getUsername(), oldMd5Password);
        if (count <= 0) {
            throw new MallException(ResponseEnum.OLD_PASSWORD_ERROR.getDesc());
        }
        User updateUser = new User();
        BeanUtils.copyProperties(user, updateUser);
        String newMd5Password = md5Config.md5EncodeAddSalt(passwordNew);
        updateUser.setPassword(newMd5Password);
        count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count <= 0) {
            throw new MallException(ResponseEnum.MODIFY_PASSWORD_ERROR.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo updateInformation(UpdateUserForm updateUserForm, User currentUser) {
        if (!currentUser.getEmail().equals(updateUserForm.getEmail())) {
            int emailCount = userMapper.countByEmail(updateUserForm.getEmail());
            if (emailCount > 0) {
                throw new MallException(ResponseEnum.EMAIL_EXIST.getDesc());
            }
        }
        User updateUser = new User();
        BeanUtils.copyProperties(updateUserForm, updateUser);
        updateUser.setId(currentUser.getId());
        int count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count <= 0) {
            throw new MallException(ResponseEnum.UPDATE_USER_INFO_ERROR.getDesc());
        }
        BeanUtils.copyProperties(updateUserForm, currentUser);
        return ResponseVo.success(currentUser);
    }

    public void checkAdminRole(User user) {
        if (user == null || !user.getRole().equals(RoleEnum.ADMIN.getCode())) {
            throw new MallException(ResponseEnum.NOT_ADMIN_NOT_PERSSION_ERROR.getDesc());
        }
    }


}
