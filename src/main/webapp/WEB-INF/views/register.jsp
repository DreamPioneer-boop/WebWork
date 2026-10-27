<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>注册 - 体态与健康管理系统</title>
  <link rel="stylesheet" href="${ctx}/static/css/style.css">
</head>
<body>
<div class="auth-page">
  <div class="auth-card" style="width:460px">
    <div class="auth-title">
      <span class="logo">💪</span>
      <h2>注册账号</h2>
    </div>

    <c:if test="${not empty error}">
      <div class="alert alert-info" style="color:#f56c6c">${error}</div>
    </c:if>

    <form action="${ctx}/register" method="post">
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>用户名</label>
        <input class="input" type="text" name="username" placeholder="请输入用户名" autocomplete="username">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>昵称</label>
        <input class="input" type="text" name="nickname" placeholder="请输入昵称">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>密码</label>
        <input class="input" type="password" name="password" placeholder="请输入密码（至少6位）" autocomplete="new-password">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>健身经验</label>
        <div class="radio-group">
          <label><input type="radio" name="experienceLevel" value="BEGINNER" checked> 初学者</label>
          <label><input type="radio" name="experienceLevel" value="EXPERIENCED"> 有健身经验</label>
        </div>
        <div class="form-hint">有健身经验的用户可以发布健身经验、训练方法等内容</div>
      </div>
      <div class="form-row">
        <button class="btn btn-primary btn-block" type="submit">注 册</button>
      </div>
    </form>

    <div class="auth-footer">
      已有账号？<a href="${ctx}/login">返回登录</a>
    </div>
  </div>
</div>
</body>
</html>
