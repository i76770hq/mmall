package pub.zjh.mall.dao;

import org.apache.ibatis.annotations.Param;
import pub.zjh.mall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(String username);

    int countByEmail(String email);

    String selectPasswordByUsername(String username);

    int selectRoleByUsername(String username);

    User selectByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    int countByUsernameAndPassword(@Param("username") String username, @Param("passwordOld") String passwordOld);

}