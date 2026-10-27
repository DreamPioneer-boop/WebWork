<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="数据统计" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="stat-grid cols-5">
  <div class="card hover"><div class="stat-card"><span class="stat-icon" style="color:#409eff">👥</span>
    <div><div class="stat-label">用户总数</div><div class="stat-value">${stats.totalUsers}</div></div></div></div>
  <div class="card hover"><div class="stat-card"><span class="stat-icon" style="color:#43a047">📄</span>
    <div><div class="stat-label">文章总数</div><div class="stat-value">${stats.totalArticles}</div></div></div></div>
  <div class="card hover"><div class="stat-card"><span class="stat-icon" style="color:#e6a23c">📏</span>
    <div><div class="stat-label">身体指标记录</div><div class="stat-value">${stats.totalHealthRecords}</div></div></div></div>
  <div class="card hover"><div class="stat-card"><span class="stat-icon" style="color:#909399">🏃</span>
    <div><div class="stat-label">健身记录</div><div class="stat-value">${stats.totalFitnessRecords}</div></div></div></div>
  <div class="card hover"><div class="stat-card"><span class="stat-icon" style="color:#f56c6c">⏳</span>
    <div><div class="stat-label">待审核文章</div><div class="stat-value">${stats.pendingArticles}</div></div></div></div>
</div>

<div class="row">
  <div class="col col-14">
    <div class="card">
      <div class="card-header"><span>📈 近 6 个月用户增长</span></div>
      <div class="card-body"><div id="growthChart" class="chart-lg"></div></div>
    </div>
  </div>
  <div class="col col-10">
    <div class="card">
      <div class="card-header"><span>📊 文章分类分布</span></div>
      <div class="card-body"><div id="categoryChart" class="chart-lg"></div></div>
    </div>
  </div>
</div>

<div class="card">
  <div class="card-header"><span>👥 最近注册用户</span></div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>用户名</th><th>昵称</th><th>角色</th><th>健身经验</th><th>注册时间</th></tr></thead>
        <tbody>
          <c:forEach var="u" items="${stats.recentUsers}">
            <tr>
              <td>${u.username}</td>
              <td>${u.nickname}</td>
              <td><span class="tag tag-${u.role == 'ADMIN' ? 'danger' : 'info'}">${u.role == 'ADMIN' ? '管理员' : '用户'}</span></td>
              <td>${u.experienceLevel == 'EXPERIENCED' ? '有健身经验' : '初学者'}</td>
              <td>${u.createTime}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script src="${ctx}/static/js/echarts.min.js"></script>
<script>
  var stats = ${statsJson};
  var charts = [];
  var growth = echarts.init(document.getElementById('growthChart'));
  growth.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 30, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: (stats.userGrowth || []).map(function (d) { return d.month; }) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '新增用户', type: 'line', smooth: true, data: (stats.userGrowth || []).map(function (d) { return d.count; }), areaStyle: { opacity: 0.2 }, itemStyle: { color: '#409eff' } }]
  });
  charts.push(growth);
  var category = echarts.init(document.getElementById('categoryChart'));
  category.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{ name: '文章分类', type: 'pie', radius: ['40%', '65%'], data: (stats.categoryDistribution || []).map(function (d) { return { name: d.name, value: d.value }; }), label: { formatter: '{b}\n{c}' } }]
  });
  charts.push(category);
  window.addEventListener('resize', function () { charts.forEach(function (c) { c.resize(); }); });
</script>
