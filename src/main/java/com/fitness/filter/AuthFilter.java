package com.fitness.filter;

import com.fitness.entity.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 登录鉴权过滤器：未登录跳登录页；后台页面校验管理员；我的发布校验发布权限。
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (path.startsWith("/admin")) {
            if (!user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }
        if ("/my-article".equals(path)) {
            if (!user.canPublish()) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.equals("/login")
                || path.equals("/register")
                || path.equals("/logout")
                || path.equals("/")
                || path.equals("/index.jsp")
                || path.startsWith("/static/");
    }

    @Override
    public void destroy() {
    }
}
