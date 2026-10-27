package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.entity.User;
import com.fitness.service.UserService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 个人资料：GET 显示，POST 处理「保存资料 / 修改密码」。
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = WebUtil.currentUser(req);
        req.setAttribute("profile", userService.getProfile(current.getId()));
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = WebUtil.currentUser(req);
        String action = req.getParameter("action");
        try {
            if ("password".equals(action)) {
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                userService.updatePassword(current.getId(),
                        oldPassword == null ? "" : oldPassword,
                        newPassword == null ? "" : newPassword);
                WebUtil.flash(req, "密码修改成功");
            } else {
                User update = new User();
                update.setNickname(req.getParameter("nickname"));
                update.setGender(req.getParameter("gender"));
                update.setAge(parseInteger(req.getParameter("age")));
                update.setHeight(parseDecimal(req.getParameter("height")));
                update.setWeight(parseDecimal(req.getParameter("weight")));
                update.setExperienceLevel(req.getParameter("experienceLevel"));
                User saved = userService.updateProfile(current.getId(), update);
                req.getSession().setAttribute("user", saved);
                WebUtil.flash(req, "保存成功");
            }
        } catch (BusinessException e) {
            WebUtil.flash(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private static Integer parseInteger(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal parseDecimal(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
