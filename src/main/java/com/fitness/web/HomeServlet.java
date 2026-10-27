package com.fitness.web;

import com.fitness.entity.User;
import com.fitness.service.AnnouncementService;
import com.fitness.service.HealthRecordService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 首页：展示最新身体指标 + 系统公告。
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final HealthRecordService healthRecordService = new HealthRecordService();
    private final AnnouncementService announcementService = new AnnouncementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        req.setAttribute("latestRecord", healthRecordService.latest(user.getId()));
        req.setAttribute("announcements", announcementService.list());
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
