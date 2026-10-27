package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.entity.FitnessRecord;
import com.fitness.entity.User;
import com.fitness.service.FitnessRecordService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 健身记录：GET 列表 + 统计，POST 保存 / 删除。
 */
@WebServlet("/fitness-record")
public class FitnessRecordServlet extends HttpServlet {

    private final FitnessRecordService service = new FitnessRecordService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int page = WebUtil.intParam(req, "page", 1);
        int size = WebUtil.intParam(req, "size", 10);
        req.setAttribute("pageResult", service.list(user.getId(), page, size));
        req.setAttribute("stats", service.stats(user.getId()));
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.getRequestDispatcher("/WEB-INF/views/fitness-record.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        String action = req.getParameter("action");
        try {
            if ("delete".equals(action)) {
                service.delete(user.getId(), Long.valueOf(req.getParameter("id")));
                WebUtil.flash(req, "删除成功");
            } else {
                FitnessRecord record = parseRecord(req);
                String idStr = req.getParameter("id");
                if (idStr != null && !idStr.isBlank()) {
                    record.setId(Long.valueOf(idStr));
                    service.update(user.getId(), record.getId(), record);
                    WebUtil.flash(req, "更新成功");
                } else {
                    service.create(user.getId(), record);
                    WebUtil.flash(req, "添加成功");
                }
            }
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
        }
        resp.sendRedirect(req.getContextPath() + "/fitness-record");
    }

    private FitnessRecord parseRecord(HttpServletRequest req) {
        FitnessRecord r = new FitnessRecord();
        r.setRecordDate(req.getParameter("recordDate"));
        r.setExerciseType(req.getParameter("exerciseType"));
        r.setDuration(WebUtil.parseInteger(req.getParameter("duration")));
        r.setCalories(WebUtil.parseInteger(req.getParameter("calories")));
        r.setIntensity(req.getParameter("intensity"));
        r.setNote(req.getParameter("note"));
        return r;
    }
}
