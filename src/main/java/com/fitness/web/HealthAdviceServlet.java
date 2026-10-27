package com.fitness.web;

import com.fitness.entity.User;
import com.fitness.service.SuggestionService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 健康建议：GET 调用规则引擎生成建议列表。
 */
@WebServlet("/health-advice")
public class HealthAdviceServlet extends HttpServlet {

    private final SuggestionService service = new SuggestionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        Map<String, Object> result = service.latest(user.getId());
        boolean hasData = Boolean.TRUE.equals(result.get("hasData"));
        req.setAttribute("hasData", hasData);
        req.setAttribute("recordDate", result.get("recordDate"));
        req.setAttribute("adviceList", result.get("adviceList"));
        req.getRequestDispatcher("/WEB-INF/views/health-advice.jsp").forward(req, resp);
    }
}
