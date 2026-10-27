package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.service.AnnouncementService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 公告管理：GET 列表，POST 发布 / 更新 / 删除。
 */
@WebServlet("/admin/announcement")
public class AdminAnnouncementServlet extends HttpServlet {

    private final AnnouncementService service = new AnnouncementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("announcements", service.list());
        req.getRequestDispatcher("/WEB-INF/views/admin/announcement.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        try {
            if ("delete".equals(action)) {
                service.delete(Long.valueOf(req.getParameter("id")));
                WebUtil.flash(req, "删除成功");
            } else if ("update".equals(action)) {
                service.update(Long.valueOf(req.getParameter("id")),
                        title == null ? "" : title.trim(),
                        content == null ? "" : content.trim());
                WebUtil.flash(req, "更新成功");
            } else {
                service.add(title == null ? "" : title.trim(), content == null ? "" : content.trim());
                WebUtil.flash(req, "发布成功");
            }
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/announcement");
    }
}
