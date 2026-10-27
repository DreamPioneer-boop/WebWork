<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="cur" value="${pageContext.request.servletPath}"/>
<c:set var="u" value="${sessionScope.user}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${pageTitle} - 体态与健康管理系统</title>
  <link rel="stylesheet" href="${ctx}/static/css/style.css">
</head>
<body>
<c:if test="${not empty sessionScope.flash}">
  <div class="toast-wrap">
    <div class="toast info">${sessionScope.flash}</div>
  </div>
  <script>setTimeout(function(){var t=document.querySelector('.toast-wrap');if(t)t.remove();},2600);</script>
  <c:remove var="flash" scope="session"/>
</c:if>

<div class="layout">
  <aside class="sidebar" id="sidebar">
    <div class="logo"><span class="logo-icon">💪</span><span class="logo-text">体态与健康管理</span></div>
    <nav>
      <a href="${ctx}/home" class="${cur == '/home' ? 'active' : ''}"><span class="menu-icon">🏠</span><span>首页</span></a>
      <a href="${ctx}/profile" class="${cur == '/profile' ? 'active' : ''}"><span class="menu-icon">👤</span><span>个人基本信息</span></a>
      <a href="${ctx}/health-record" class="${cur == '/health-record' ? 'active' : ''}"><span class="menu-icon">📏</span><span>身体指标</span></a>
      <a href="${ctx}/health-trend" class="${cur == '/health-trend' ? 'active' : ''}"><span class="menu-icon">📈</span><span>健康趋势</span></a>
      <a href="${ctx}/health-advice" class="${cur == '/health-advice' ? 'active' : ''}"><span class="menu-icon">💡</span><span>健康建议</span></a>
      <a href="${ctx}/fitness-record" class="${cur == '/fitness-record' ? 'active' : ''}"><span class="menu-icon">🏃</span><span>健身记录</span></a>
      <a href="${ctx}/article" class="${cur == '/article' ? 'active' : ''}"><span class="menu-icon">📚</span><span>经验分享</span></a>
      <c:if test="${u.role == 'ADMIN' || u.experienceLevel == 'EXPERIENCED'}">
        <a href="${ctx}/my-article" class="${cur == '/my-article' ? 'active' : ''}"><span class="menu-icon">📝</span><span>我的发布</span></a>
      </c:if>
      <c:if test="${u.role == 'ADMIN'}">
        <a href="${ctx}/admin/dashboard" class="${cur == '/admin/dashboard' ? 'active' : ''}"><span class="menu-icon">📊</span><span>数据统计</span></a>
        <a href="${ctx}/admin/users" class="${cur == '/admin/users' ? 'active' : ''}"><span class="menu-icon">👥</span><span>用户管理</span></a>
        <a href="${ctx}/admin/audit" class="${cur == '/admin/audit' ? 'active' : ''}"><span class="menu-icon">✅</span><span>内容审核</span></a>
        <a href="${ctx}/admin/announcement" class="${cur == '/admin/announcement' ? 'active' : ''}"><span class="menu-icon">📢</span><span>公告管理</span></a>
      </c:if>
    </nav>
  </aside>

  <div class="main">
    <header class="header">
      <div class="header-left">
        <span class="collapse-btn" id="collapseBtn">☰</span>
        <span class="page-title">${pageTitle}</span>
      </div>
      <div class="header-right">
        <c:choose>
          <c:when test="${u.role == 'ADMIN'}"><span class="tag tag-dark">管理员</span></c:when>
          <c:when test="${u.experienceLevel == 'EXPERIENCED'}"><span class="tag tag-warning">健身达人</span></c:when>
          <c:otherwise><span class="tag tag-success">健身初学者</span></c:otherwise>
        </c:choose>
        <div class="user-info" id="userInfo">
          <span class="avatar">${fn:substring(u.nickname, 0, 1)}</span>
          <span class="nickname" style="font-size:14px">${u.nickname}</span>
          <span style="font-size:12px;color:#999">▼</span>
          <div class="dropdown-menu" id="dropdownMenu">
            <a href="${ctx}/profile">个人资料</a>
            <a href="${ctx}/logout" class="divided">退出登录</a>
          </div>
        </div>
      </div>
    </header>
    <main class="main-content">
