package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.UserDao;
import com.fitness.entity.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * 认证服务：登录、注册（会话式鉴权，登录后由 Servlet 写入 Session）。
 */
public class AuthService {

    private final UserDao userDao = new UserDao();

    /** 登录：校验账号密码与状态，成功返回用户 */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        return user;
    }

    /** 注册：创建普通用户账号，密码 BCrypt 加密存储 */
    public User register(String username, String password, String nickname, String experienceLevel) {
        if (userDao.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(nickname);
        user.setExperienceLevel(experienceLevel == null || experienceLevel.isBlank() ? "BEGINNER" : experienceLevel);
        user.setRole("USER");
        user.setStatus(1);
        user.setGender("男");
        userDao.insert(user);
        return user;
    }
}
