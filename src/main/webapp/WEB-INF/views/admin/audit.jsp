<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="内容审核" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>✅ 内容审核</span>
    <form action="${ctx}/admin/audit" method="get">
      <select class="select" name="status" onchange="this.form.submit()" style="width:140px">
        <option value="" ${empty status ? 'selected' : ''}>全部</option>
        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>待审核</option>
        <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>已通过</option>
        <option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>已驳回</option>
      </select>
    </form>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>标题</th><th>分类</th><th>作者</th><th>状态</th><th>发布时间</th><th>操作</th></tr></thead>
        <tbody>
          <c:forEach var="a" items="${pageResult.records}">
            <tr>
              <td>${a.title}</td>
              <td><span class="tag tag-${a.category == 'EXPERIENCE' ? 'success' : (a.category == 'TRAINING' ? 'primary' : 'warning')}">${a.category == 'EXPERIENCE' ? '经验分享' : (a.category == 'TRAINING' ? '训练方法' : '饮食建议')}</span></td>
              <td>${a.authorNickname}</td>
              <td><span class="tag tag-${a.status == 'PENDING' ? 'warning' : (a.status == 'APPROVED' ? 'success' : 'danger')}">${a.status == 'PENDING' ? '待审核' : (a.status == 'APPROVED' ? '已通过' : '已驳回')}</span></td>
              <td>${a.createTime}</td>
              <td>
                <button class="btn-link" data-view data-title="<c:out value='${a.title}'/>" data-category="${a.category}" data-author="<c:out value='${a.authorNickname}'/>" data-time="${a.createTime}" data-content="<c:out value='${a.content}'/>">查看</button>
                <c:if test="${a.status == 'PENDING'}">
                  <form action="${ctx}/admin/audit" method="post" style="display:inline" data-confirm="确定通过这篇文章吗？">
                    <input type="hidden" name="action" value="audit">
                    <input type="hidden" name="id" value="${a.id}">
                    <input type="hidden" name="status" value="APPROVED">
                    <button class="btn-link success" type="submit">通过</button>
                  </form>
                  <button class="btn-link danger" data-reject data-id="${a.id}">驳回</button>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty pageResult.records}">
            <tr><td colspan="6"><div class="empty"><span class="empty-icon">✅</span>暂无文章</div></td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
    <c:set var="total" value="${pageResult.total}" scope="request"/>
    <c:set var="page" value="${page}" scope="request"/>
    <c:set var="size" value="${size}" scope="request"/>
    <c:set var="extraQuery" scope="request"><c:if test="${not empty status}">&amp;status=${status}</c:if></c:set>
    <jsp:include page="/WEB-INF/views/common/pagination.jsp"/>
  </div>
</div>

<!-- 查看详情弹窗 -->
<div class="modal-mask" id="viewDialog">
  <div class="modal" style="width:640px">
    <div class="modal-header"><span>文章详情</span><button class="modal-close" onclick="closeView()">×</button></div>
    <div class="modal-body">
      <h3 style="margin-bottom:10px" id="viewTitle"></h3>
      <div style="color:#999;font-size:13px;margin-bottom:16px" id="viewMeta"></div>
      <div style="line-height:1.8;white-space:pre-wrap;color:#444" id="viewContent"></div>
    </div>
  </div>
</div>

<!-- 驳回原因弹窗 -->
<div class="modal-mask" id="rejectDialog">
  <div class="modal" style="width:480px">
    <div class="modal-header"><span>驳回原因</span><button class="modal-close" onclick="closeReject()">×</button></div>
    <div class="modal-body">
      <form action="${ctx}/admin/audit" method="post">
        <input type="hidden" name="action" value="audit">
        <input type="hidden" name="id" id="rejectId">
        <input type="hidden" name="status" value="REJECTED">
        <textarea class="textarea" name="rejectReason" rows="3" maxlength="100" placeholder="请输入驳回原因"></textarea>
        <div class="modal-footer" style="padding-left:0;padding-right:0">
          <button class="btn" type="button" onclick="closeReject()">取消</button>
          <button class="btn btn-danger" type="submit">确定驳回</button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script>
  var catMap = { EXPERIENCE: '经验分享', TRAINING: '训练方法', DIET: '饮食建议' };
  document.querySelectorAll('[data-view]').forEach(function (b) {
    b.addEventListener('click', function () {
      var d = b.dataset;
      document.getElementById('viewTitle').textContent = d.title;
      document.getElementById('viewMeta').textContent = (catMap[d.category] || d.category) + ' · 作者：' + d.author + ' · ' + d.time;
      document.getElementById('viewContent').textContent = d.content;
      document.getElementById('viewDialog').classList.add('show');
    });
  });
  document.querySelectorAll('[data-reject]').forEach(function (b) {
    b.addEventListener('click', function () {
      document.getElementById('rejectId').value = b.dataset.id;
      document.getElementById('rejectDialog').classList.add('show');
    });
  });
  function closeView() { document.getElementById('viewDialog').classList.remove('show'); }
  function closeReject() { document.getElementById('rejectDialog').classList.remove('show'); }
</script>
