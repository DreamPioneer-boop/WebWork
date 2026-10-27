<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="健身记录" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="stat-grid cols-3">
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#409eff">📅</span>
    <div><div class="stat-label">累计健身次数</div><div class="stat-value">${stats.totalCount} 次</div></div>
  </div></div>
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#43a047">⏱️</span>
    <div><div class="stat-label">累计健身时长</div><div class="stat-value">${stats.totalDuration} 分钟</div></div>
  </div></div>
  <div class="card hover"><div class="stat-card">
    <span class="stat-icon" style="color:#e6a23c">⚡</span>
    <div><div class="stat-label">累计消耗热量</div><div class="stat-value">${stats.totalCalories} 千卡</div></div>
  </div></div>
</div>

<div class="card">
  <div class="card-header">
    <span>🏃 健身情况记录</span>
    <button class="btn btn-primary" onclick="openAdd()">＋ 新增记录</button>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>日期</th><th>运动类型</th><th>时长(分钟)</th><th>消耗(千卡)</th><th>强度</th><th>备注</th><th>操作</th></tr></thead>
        <tbody>
          <c:forEach var="r" items="${pageResult.records}">
            <tr>
              <td>${r.recordDate}</td>
              <td>${r.exerciseType}</td>
              <td>${r.duration}</td>
              <td>${r.calories}</td>
              <td><span class="tag tag-${r.intensity == 'LOW' ? 'info' : (r.intensity == 'MEDIUM' ? 'warning' : 'danger')}">${r.intensity == 'LOW' ? '低强度' : (r.intensity == 'MEDIUM' ? '中强度' : '高强度')}</span></td>
              <td>${r.note}</td>
              <td>
                <button class="btn-link" data-edit data-id="${r.id}" data-date="${r.recordDate}" data-type="<c:out value='${r.exerciseType}'/>" data-duration="${r.duration}" data-calories="${r.calories}" data-intensity="${r.intensity}" data-note="<c:out value='${r.note}'/>">编辑</button>
                <form action="${ctx}/fitness-record" method="post" style="display:inline" data-confirm="确定删除这条记录吗？">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${r.id}">
                  <button class="btn-link danger" type="submit">删除</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty pageResult.records}">
            <tr><td colspan="7"><div class="empty"><span class="empty-icon">🏃</span>暂无记录</div></td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
    <c:set var="total" value="${pageResult.total}" scope="request"/>
    <c:set var="page" value="${page}" scope="request"/>
    <c:set var="size" value="${size}" scope="request"/>
    <jsp:include page="/WEB-INF/views/common/pagination.jsp"/>
  </div>
</div>

<!-- 新增 / 编辑弹窗 -->
<div class="modal-mask" id="dialog">
  <div class="modal">
    <div class="modal-header"><span id="dialogTitle">新增健身记录</span><button class="modal-close" onclick="closeDialog()">×</button></div>
    <div class="modal-body">
      <form action="${ctx}/fitness-record" method="post">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" id="f_id">
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>日期</label>
          <input class="input" type="date" name="recordDate" id="f_recordDate">
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>运动类型</label>
          <select class="select" name="exerciseType" id="f_exerciseType">
            <option value="">请选择运动类型</option>
            <option>跑步</option><option>游泳</option><option>骑行</option><option>力量训练</option>
            <option>瑜伽</option><option>篮球</option><option>足球</option><option>羽毛球</option>
            <option>跳绳</option><option>健身操</option><option>其他</option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>时长(分钟)</label>
          <input class="input" type="number" name="duration" id="f_duration" min="1" max="1000">
        </div>
        <div class="form-row">
          <label class="form-label">消耗(千卡)</label>
          <input class="input" type="number" name="calories" id="f_calories" min="0" max="10000">
        </div>
        <div class="form-row">
          <label class="form-label">强度</label>
          <div class="radio-group">
            <label><input type="radio" name="intensity" value="LOW"> 低</label>
            <label><input type="radio" name="intensity" value="MEDIUM" checked> 中</label>
            <label><input type="radio" name="intensity" value="HIGH"> 高</label>
          </div>
        </div>
        <div class="form-row">
          <label class="form-label">备注</label>
          <textarea class="textarea" name="note" id="f_note" rows="2" maxlength="200"></textarea>
        </div>
        <div class="modal-footer">
          <button class="btn" type="button" onclick="closeDialog()">取消</button>
          <button class="btn btn-primary" type="submit">保存</button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script>
  function todayStr() {
    var d = new Date();
    return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
  }
  function setVal(id, v) { document.getElementById(id).value = (v === undefined || v === null) ? '' : v; }
  function openAdd() {
    document.getElementById('dialogTitle').textContent = '新增健身记录';
    setVal('f_id', ''); setVal('f_recordDate', todayStr());
    setVal('f_exerciseType', ''); setVal('f_duration', ''); setVal('f_calories', '');
    setVal('f_note', '');
    document.querySelector('input[name="intensity"][value="MEDIUM"]').checked = true;
    document.getElementById('dialog').classList.add('show');
  }
  function openEdit(d) {
    document.getElementById('dialogTitle').textContent = '编辑健身记录';
    setVal('f_id', d.id); setVal('f_recordDate', d.date);
    setVal('f_exerciseType', d.type); setVal('f_duration', d.duration); setVal('f_calories', d.calories);
    setVal('f_note', d.note);
    var rb = document.querySelector('input[name="intensity"][value="' + (d.intensity || 'MEDIUM') + '"]');
    if (rb) rb.checked = true;
    document.getElementById('dialog').classList.add('show');
  }
  function closeDialog() { document.getElementById('dialog').classList.remove('show'); }
  document.querySelectorAll('[data-edit]').forEach(function (b) {
    b.addEventListener('click', function () { openEdit(b.dataset); });
  });
</script>
