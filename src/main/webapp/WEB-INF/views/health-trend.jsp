<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="健康趋势" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>📈 健康变化趋势</span>
    <a href="${ctx}/health-record">补充记录</a>
  </div>
  <div class="card-body">
    <c:if test="${!hasData}">
      <div class="alert alert-info">暂无身体指标数据，请先添加至少一条身体指标记录后再查看趋势</div>
    </c:if>
    <div class="row">
      <div class="col col-12"><div class="card hover"><div class="card-body">
        <div class="chart-title">体重趋势 (kg)</div><div id="weightChart" class="chart"></div>
      </div></div></div>
      <div class="col col-12"><div class="card hover"><div class="card-body">
        <div class="chart-title">BMI 趋势</div><div id="bmiChart" class="chart"></div>
      </div></div></div>
    </div>
    <div class="row">
      <div class="col col-12"><div class="card hover"><div class="card-body">
        <div class="chart-title">血压趋势 (mmHg)</div><div id="bpChart" class="chart"></div>
      </div></div></div>
      <div class="col col-12"><div class="card hover"><div class="card-body">
        <div class="chart-title">心率趋势 (次/分)</div><div id="hrChart" class="chart"></div>
      </div></div></div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script src="${ctx}/static/js/echarts.min.js"></script>
<script>
  var trend = ${trendJson};
  var charts = [];
  function baseOption(title, xData) {
    return {
      title: { text: title, left: 'center', textStyle: { fontSize: 14, fontWeight: 'normal' } },
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 30, top: 50, bottom: 30 },
      xAxis: { type: 'category', data: xData, boundaryGap: false, axisLabel: { rotate: xData.length > 8 ? 30 : 0 } },
      yAxis: { type: 'value', scale: true },
      color: ['#409eff', '#43a047', '#e6a23c', '#f56c6c']
    };
  }
  function renderLine(id, xData, series) {
    var dom = document.getElementById(id);
    if (!dom || !xData.length) return;
    var chart = echarts.init(dom);
    chart.setOption({
      series: series.map(function (s) {
        return { name: s.name, type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, data: s.data, areaStyle: series.length === 1 ? { opacity: 0.15 } : undefined };
      })
    });
    chart.setOption(baseOption(series[0] ? series[0].name : '', xData));
    charts.push(chart);
  }
  if (trend.dates && trend.dates.length) {
    renderLine('weightChart', trend.dates, [{ name: '体重', data: trend.weight }]);
    renderLine('bmiChart', trend.dates, [{ name: 'BMI', data: trend.bmi }]);
    renderLine('bpChart', trend.dates, [{ name: '收缩压', data: trend.systolic }, { name: '舒张压', data: trend.diastolic }]);
    renderLine('hrChart', trend.dates, [{ name: '心率', data: trend.heartRate }]);
  }
  window.addEventListener('resize', function () { charts.forEach(function (c) { c.resize(); }); });
</script>
