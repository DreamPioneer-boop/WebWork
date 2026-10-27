<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- 入口页：根据登录态跳转 --%>
<c:redirect url="${sessionScope.user != null ? '/home' : '/login'}"/>
