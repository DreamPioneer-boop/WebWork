<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>登录 - 体态与健康管理系统</title>
  <link rel="stylesheet" href="${ctx}/static/css/style.css">
</head>
<body>
<div class="auth-page">
  <div class="auth-card">
    <div class="auth-title">
      <span class="logo">💪</span>
      <h2>体态与健康管理系统</h2>
      <p>记录健康数据 · 分享健身经验</p>
    </div>

    <c:if test="${not empty error}">
      <div class="alert alert-info" style="color:#f56c6c">${error}</div>
    </c:if>

    <form action="${ctx}/login" method="post">
      <div class="form-row">
        <input class="input" type="text" name="username" placeholder="用户名" value="<c:out value='${username}'/>" autocomplete="username">
      </div>
      <div class="form-row">
        <input class="input" type="password" name="password" placeholder="密码" autocomplete="current-password">
      </div>
      <div class="form-row">
        <button class="btn btn-primary btn-block" type="submit">登 录</button>
      </div>
    </form>

    <div class="auth-footer">
      还没有账号？<a href="${ctx}/register">立即注册</a>
    </div>
    <div class="auth-divider">测试账号：admin / admin123（管理员）</div>
  </div>
</div>
</body>
</html>
