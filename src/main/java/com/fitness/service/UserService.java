package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.UserDao;
import com.fitness.entity.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * 用户个人资料服务。
 */
public class UserService {

    private final UserDao userDao = new UserDao();

    public User getProfile(Long userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    public User updateProfile(Long userId, User update) {
        User user = getProfile(userId);
        user.setNickname(update.getNickname());
        user.setAvatar(update.getAvatar());
        user.setGender(update.getGender());
        user.setAge(update.getAge());
        user.setHeight(update.getHeight());
        user.setWeight(update.getWeight());
        user.setExperienceLevel(update.getExperienceLevel());
        userDao.updateProfile(user);
        return user;
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getProfile(userId);
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        userDao.updatePassword(userId, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
    }
}
