<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="身体指标" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>📏 身体各项指标</span>
    <button class="btn btn-primary" onclick="openAdd()">＋ 新增记录</button>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>记录日期</th><th>体重(kg)</th><th>身高(cm)</th><th>BMI</th><th>体脂率(%)</th>
            <th>肌肉量(kg)</th><th>血压(mmHg)</th><th>心率</th><th>血糖</th><th>睡眠(h)</th><th>备注</th><th>操作</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="r" items="${pageResult.records}">
            <tr>
              <td>${r.recordDate}</td>
              <td>${r.weight}</td>
              <td>${r.height}</td>
              <td><span class="tag tag-${empty r.bmi ? 'info' : (r.bmi < 18.5 ? 'warning' : (r.bmi < 24 ? 'success' : (r.bmi < 28 ? 'warning' : 'danger')))}">${r.bmi}</span></td>
              <td>${r.bodyFatRate}</td>
              <td>${r.muscleMass}</td>
              <td>${r.systolicPressure}/${r.diastolicPressure}</td>
              <td>${r.heartRate}</td>
              <td>${r.bloodSugar}</td>
              <td>${r.sleepHours}</td>
              <td>${r.note}</td>
              <td>
                <button class="btn-link" data-edit data-id="${r.id}" data-date="${r.recordDate}" data-height="${r.height}" data-weight="${r.weight}" data-bodyfat="${r.bodyFatRate}" data-muscle="${r.muscleMass}" data-sys="${r.systolicPressure}" data-dia="${r.diastolicPressure}" data-hr="${r.heartRate}" data-bs="${r.bloodSugar}" data-sleep="${r.sleepHours}" data-note="<c:out value='${r.note}'/>">编辑</button>
                <form action="${ctx}/health-record" method="post" style="display:inline" data-confirm="确定删除这条记录吗？">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${r.id}">
                  <button class="btn-link danger" type="submit">删除</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty pageResult.records}">
            <tr><td colspan="12"><div class="empty"><span class="empty-icon">📭</span>暂无记录</div></td></tr>
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
    <div class="modal-header"><span id="dialogTitle">新增身体指标</span><button class="modal-close" onclick="closeDialog()">×</button></div>
    <div class="modal-body">
      <form action="${ctx}/health-record" method="post">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" id="f_id">
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>记录日期</label>
          <input class="input" type="date" name="recordDate" id="f_recordDate">
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>身高(cm)</label>
          <input class="input" type="number" name="height" id="f_height" min="50" max="250" step="0.1">
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>体重(kg)</label>
          <input class="input" type="number" name="weight" id="f_weight" min="20" max="300" step="0.1">
        </div>
        <div class="form-row">
          <label class="form-label">BMI</label>
          <div class="form-inline">
            <input class="input" id="f_bmi" disabled style="width:120px">
            <span class="tag tag-info" id="f_bmiTag">--</span>
          </div>
        </div>
        <div class="form-row">
          <label class="form-label">体脂率(%)</label>
          <input class="input" type="number" name="bodyFatRate" id="f_bodyFatRate" min="0" max="60" step="0.1">
        </div>
        <div class="form-row">
          <label class="form-label">肌肉量(kg)</label>
          <input class="input" type="number" name="muscleMass" id="f_muscleMass" min="0" max="150" step="0.1">
        </div>
        <div class="form-row">
          <label class="form-label">收缩压(mmHg)</label>
          <input class="input" type="number" name="systolicPressure" id="f_systolicPressure" min="0" max="300">
        </div>
        <div class="form-row">
          <label class="form-label">舒张压(mmHg)</label>
          <input class="input" type="number" name="diastolicPressure" id="f_diastolicPressure" min="0" max="200">
        </div>
        <div class="form-row">
          <label class="form-label">心率(次/分)</label>
          <input class="input" type="number" name="heartRate" id="f_heartRate" min="0" max="250">
        </div>
        <div class="form-row">
          <label class="form-label">血糖(mmol/L)</label>
          <input class="input" type="number" name="bloodSugar" id="f_bloodSugar" min="0" max="40" step="0.1">
        </div>
        <div class="form-row">
          <label class="form-label">睡眠(小时)</label>
          <input class="input" type="number" name="sleepHours" id="f_sleepHours" min="0" max="24" step="0.1">
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
  function calcBmi() {
    var h = parseFloat(document.getElementById('f_height').value);
    var w = parseFloat(document.getElementById('f_weight').value);
    var input = document.getElementById('f_bmi');
    var tag = document.getElementById('f_bmiTag');
    if (h && w) {
      var bmi = (w / Math.pow(h / 100, 2)).toFixed(1);
      input.value = bmi;
      var v = parseFloat(bmi), cls = 'info', label = '--';
      if (v < 18.5) { cls = 'warning'; label = '偏瘦'; }
      else if (v < 24) { cls = 'success'; label = '正常'; }
      else if (v < 28) { cls = 'warning'; label = '超重'; }
      else { cls = 'danger'; label = '肥胖'; }
      tag.className = 'tag tag-' + cls;
      tag.textContent = label;
    } else {
      input.value = '';
      tag.className = 'tag tag-info';
      tag.textContent = '--';
    }
  }
  function openAdd() {
    document.getElementById('dialogTitle').textContent = '新增身体指标';
    setVal('f_id', '');
    setVal('f_recordDate', todayStr());
    setVal('f_height', ''); setVal('f_weight', '');
    setVal('f_bodyFatRate', ''); setVal('f_muscleMass', '');
    setVal('f_systolicPressure', ''); setVal('f_diastolicPressure', '');
    setVal('f_heartRate', ''); setVal('f_bloodSugar', ''); setVal('f_sleepHours', '');
    setVal('f_note', '');
    calcBmi();
    document.getElementById('dialog').classList.add('show');
  }
  function openEdit(d) {
    document.getElementById('dialogTitle').textContent = '编辑身体指标';
    setVal('f_id', d.id);
    setVal('f_recordDate', d.date);
    setVal('f_height', d.height); setVal('f_weight', d.weight);
    setVal('f_bodyFatRate', d.bodyfat); setVal('f_muscleMass', d.muscle);
    setVal('f_systolicPressure', d.sys); setVal('f_diastolicPressure', d.dia);
    setVal('f_heartRate', d.hr); setVal('f_bloodSugar', d.bs); setVal('f_sleepHours', d.sleep);
    setVal('f_note', d.note);
    calcBmi();
    document.getElementById('dialog').classList.add('show');
  }
  function closeDialog() { document.getElementById('dialog').classList.remove('show'); }
  document.getElementById('f_height').addEventListener('input', calcBmi);
  document.getElementById('f_weight').addEventListener('input', calcBmi);
  document.querySelectorAll('[data-edit]').forEach(function (b) {
    b.addEventListener('click', function () { openEdit(b.dataset); });
  });
</script>
