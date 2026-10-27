package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.entity.HealthRecord;
import com.fitness.entity.User;
import com.fitness.service.HealthRecordService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 身体指标记录：GET 分页列表，POST 保存 / 删除。
 */
@WebServlet("/health-record")
public class HealthRecordServlet extends HttpServlet {

    private final HealthRecordService service = new HealthRecordService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int page = WebUtil.intParam(req, "page", 1);
        int size = WebUtil.intParam(req, "size", 10);
        req.setAttribute("pageResult", service.list(user.getId(), page, size));
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.getRequestDispatcher("/WEB-INF/views/health-record.jsp").forward(req, resp);
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
                HealthRecord record = parseRecord(req);
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
        resp.sendRedirect(req.getContextPath() + "/health-record");
    }

    private HealthRecord parseRecord(HttpServletRequest req) {
        HealthRecord r = new HealthRecord();
        r.setRecordDate(req.getParameter("recordDate"));
        r.setHeight(WebUtil.parseDecimal(req.getParameter("height")));
        r.setWeight(WebUtil.parseDecimal(req.getParameter("weight")));
        r.setBmi(WebUtil.parseDecimal(req.getParameter("bmi")));
        r.setBodyFatRate(WebUtil.parseDecimal(req.getParameter("bodyFatRate")));
        r.setMuscleMass(WebUtil.parseDecimal(req.getParameter("muscleMass")));
        r.setSystolicPressure(WebUtil.parseInteger(req.getParameter("systolicPressure")));
        r.setDiastolicPressure(WebUtil.parseInteger(req.getParameter("diastolicPressure")));
        r.setHeartRate(WebUtil.parseInteger(req.getParameter("heartRate")));
        r.setBloodSugar(WebUtil.parseDecimal(req.getParameter("bloodSugar")));
        r.setSleepHours(WebUtil.parseDecimal(req.getParameter("sleepHours")));
        r.setNote(req.getParameter("note"));
        return r;
    }
}
