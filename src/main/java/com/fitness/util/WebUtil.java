package com.fitness.util;

import com.fitness.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Web 层小工具：当前用户、flash 提示消息等。
 */
public final class WebUtil {

    public static final String SESSION_USER = "user";
    private static final String SESSION_FLASH = "flash";

    private WebUtil() {
    }

    /** 当前登录用户（可能为 null） */
    public static User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(SESSION_USER);
    }

    /** 写入一条重定向后仍可见的提示消息 */
    public static void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute(SESSION_FLASH, message);
    }

    /** 取出并清除 flash 消息（由 JSP 调用，只显示一次） */
    public static String consumeFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        String msg = (String) session.getAttribute(SESSION_FLASH);
        session.removeAttribute(SESSION_FLASH);
        return msg;
    }

    /** 解析请求中的整数参数，缺省返回默认值 */
    public static int intParam(HttpServletRequest req, String name, int def) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) {
            return def;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /** 解析可空的 Integer（空串返回 null） */
    public static Integer parseInteger(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 解析可空的 BigDecimal（空串返回 null） */
    public static java.math.BigDecimal parseDecimal(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return new java.math.BigDecimal(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
