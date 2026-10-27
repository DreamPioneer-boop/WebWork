<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="首页" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card welcome-card">
  <div class="card-body">
    <div class="welcome">
      <div>
        <h2>你好，${sessionScope.user.nickname} 👋</h2>
        <p class="welcome-sub">
          <c:choose>
            <c:when test="${sessionScope.user.role == 'ADMIN'}">欢迎进入系统管理后台</c:when>
            <c:when test="${sessionScope.user.experienceLevel == 'EXPERIENCED'}">欢迎回来，坚持分享与锻炼！</c:when>
            <c:otherwise>欢迎使用体态与健康管理系统，开始记录你的健康数据吧</c:otherwise>
          </c:choose>
        </p>
      </div>
      <c:choose>
        <c:when test="${sessionScope.user.role == 'ADMIN'}"><span class="tag tag-dark">管理员</span></c:when>
        <c:when test="${sessionScope.user.experienceLevel == 'EXPERIENCED'}"><span class="tag tag-warning">健身达人</span></c:when>
        <c:otherwise><span class="tag tag-success">健身初学者</span></c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<div class="stat-grid">
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#409eff">⚖️</span>
    <div><div class="stat-label">最新体重</div>
      <div class="stat-value"><c:out value="${empty latestRecord ? '--' : latestRecord.weight}"/><c:if test="${not empty latestRecord}"> kg</c:if></div>
    </div>
  </div></div>
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#43a047">📊</span>
    <div><div class="stat-label">最新 BMI</div>
      <div class="stat-value"><c:out value="${empty latestRecord ? '--' : latestRecord.bmi}"/></div>
    </div>
  </div></div>
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#e6a23c">🕐</span>
    <div><div class="stat-label">体脂率</div>
      <div class="stat-value"><c:out value="${empty latestRecord ? '--' : latestRecord.bodyFatRate}"/><c:if test="${not empty latestRecord}">%</c:if></div>
    </div>
  </div></div>
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#f56c6c">🌙</span>
    <div><div class="stat-label">睡眠时长</div>
      <div class="stat-value"><c:out value="${empty latestRecord ? '--' : latestRecord.sleepHours}"/><c:if test="${not empty latestRecord}"> 小时</c:if></div>
    </div>
  </div></div>
</div>

<div class="row">
  <div class="col col-14">
    <div class="card">
      <div class="card-header"><span>📊 最新身体指标</span><a href="${ctx}/health-record">查看全部</a></div>
      <div class="card-body">
        <c:choose>
          <c:when test="${not empty latestRecord}">
            <table class="table">
              <tbody>
                <tr><td class="desc-label">记录日期</td><td>${latestRecord.recordDate}</td><td class="desc-label">身高</td><td>${latestRecord.height} cm</td></tr>
                <tr><td class="desc-label">体重</td><td>${latestRecord.weight} kg</td><td class="desc-label">BMI</td><td>${latestRecord.bmi}</td></tr>
                <tr><td class="desc-label">血压</td><td>${latestRecord.systolicPressure}/${latestRecord.diastolicPressure} mmHg</td><td class="desc-label">心率</td><td>${latestRecord.heartRate} 次/分</td></tr>
                <tr><td class="desc-label">血糖</td><td>${latestRecord.bloodSugar} mmol/L</td><td class="desc-label">肌肉量</td><td>${latestRecord.muscleMass} kg</td></tr>
              </tbody>
            </table>
          </c:when>
          <c:otherwise>
            <div class="empty"><span class="empty-icon">📭</span>还没有身体指标记录，去添加一条吧
              <div style="margin-top:14px"><a class="btn btn-primary" href="${ctx}/health-record">去记录</a></div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
  <div class="col col-10">
    <div class="card">
      <div class="card-header"><span>📢 系统公告</span></div>
      <div class="card-body">
        <c:choose>
          <c:when test="${not empty announcements}">
            <ul class="timeline">
              <c:forEach var="a" items="${announcements}">
                <li><span class="tl-time">${a.createTime}</span>
                  <div class="tl-title">${a.title}</div>
                  <div class="tl-content">${a.content}</div></li>
              </c:forEach>
            </ul>
          </c:when>
          <c:otherwise><div class="empty"><span class="empty-icon">📢</span>暂无公告</div></c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<style>.desc-label{background:#fafafa;color:#909399;font-weight:600;width:140px;white-space:nowrap;}</style>
