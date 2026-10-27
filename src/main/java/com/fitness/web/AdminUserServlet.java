package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.service.AdminService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 用户管理：GET 分页列表，POST 启用/禁用、改角色、删除。
 */
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = WebUtil.intParam(req, "page", 1);
        int size = WebUtil.intParam(req, "size", 10);
        String keyword = req.getParameter("keyword");
        req.setAttribute("pageResult", service.users(keyword, page, size));
        req.setAttribute("keyword", keyword);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            Long id = Long.valueOf(req.getParameter("id"));
            switch (action == null ? "" : action) {
                case "status" -> {
                    service.updateStatus(id, WebUtil.intParam(req, "status", 0));
                    WebUtil.flash(req, "操作成功");
                }
                case "role" -> {
                    service.updateRole(id, req.getParameter("role"));
                    WebUtil.flash(req, "操作成功");
                }
                case "delete" -> {
                    service.deleteUser(id);
                    WebUtil.flash(req, "删除成功");
                }
                default -> {
                }
            }
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
