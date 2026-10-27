package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.service.AuthService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 注册：GET 显示注册页，POST 创建新账号。
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");
        String experienceLevel = req.getParameter("experienceLevel");
        try {
            authService.register(username == null ? "" : username.trim(),
                    password == null ? "" : password,
                    nickname == null ? "" : nickname.trim(),
                    experienceLevel);
            WebUtil.flash(req, "注册成功，请登录");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
