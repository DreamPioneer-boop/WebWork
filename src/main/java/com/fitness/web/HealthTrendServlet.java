package com.fitness.web;

import com.fitness.entity.User;
import com.fitness.service.HealthRecordService;
import com.fitness.util.WebUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 健康趋势：GET 计算趋势数据并序列化成 JSON，供页面内嵌 ECharts 使用。
 */
@WebServlet("/health-trend")
public class HealthTrendServlet extends HttpServlet {

    private final HealthRecordService service = new HealthRecordService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        Map<String, Object> trend = service.trend(user.getId());
        boolean hasData = trend.get("dates") != null && !((java.util.List<?>) trend.get("dates")).isEmpty();
        req.setAttribute("hasData", hasData);
        req.setAttribute("trendJson", gson.toJson(trend));
        req.getRequestDispatcher("/WEB-INF/views/health-trend.jsp").forward(req, resp);
    }
}
