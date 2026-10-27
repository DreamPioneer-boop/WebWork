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
 * 内容审核：GET 分页列表（可按状态筛选），POST 通过 / 驳回。
 */
@WebServlet("/admin/audit")
public class AdminAuditServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = WebUtil.intParam(req, "page", 1);
        int size = WebUtil.intParam(req, "size", 10);
        String status = req.getParameter("status");
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
        req.setAttribute("pageResult", service.articles(status, null, page, size));
        req.setAttribute("status", status);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.getRequestDispatcher("/WEB-INF/views/admin/audit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = Long.valueOf(req.getParameter("id"));
            String status = req.getParameter("status");
            String rejectReason = req.getParameter("rejectReason");
            service.audit(id, status, rejectReason);
            WebUtil.flash(req, "操作成功");
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/audit");
    }
}
