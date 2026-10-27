<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="公告管理" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>📢 公告管理</span>
    <button class="btn btn-primary" onclick="openAdd()">＋ 发布公告</button>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>ID</th><th>标题</th><th>内容</th><th>发布时间</th><th>操作</th></tr></thead>
        <tbody>
          <c:forEach var="a" items="${announcements}">
            <tr>
              <td>${a.id}</td>
              <td>${a.title}</td>
              <td>${a.content}</td>
              <td>${a.createTime}</td>
              <td>
                <button class="btn-link" data-edit data-id="${a.id}" data-title="<c:out value='${a.title}'/>" data-content="<c:out value='${a.content}'/>">编辑</button>
                <form action="${ctx}/admin/announcement" method="post" style="display:inline" data-confirm="确定删除这条公告吗？">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${a.id}">
                  <button class="btn-link danger" type="submit">删除</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty announcements}">
            <tr><td colspan="5"><div class="empty"><span class="empty-icon">📢</span>暂无公告</div></td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- 发布 / 编辑弹窗 -->
<div class="modal-mask" id="dialog">
  <div class="modal" style="width:520px">
    <div class="modal-header"><span id="dialogTitle">发布公告</span><button class="modal-close" onclick="closeDialog()">×</button></div>
    <div class="modal-body">
      <form action="${ctx}/admin/announcement" method="post">
        <input type="hidden" name="action" id="f_action" value="add">
        <input type="hidden" name="id" id="f_id">
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>标题</label>
          <input class="input" name="title" id="f_title" maxlength="50">
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>内容</label>
          <textarea class="textarea" name="content" id="f_content" rows="5" maxlength="500"></textarea>
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
  function openAdd() {
    document.getElementById('dialogTitle').textContent = '发布公告';
    document.getElementById('f_action').value = 'add';
    document.getElementById('f_id').value = '';
    document.getElementById('f_title').value = '';
    document.getElementById('f_content').value = '';
    document.getElementById('dialog').classList.add('show');
  }
  function openEdit(d) {
    document.getElementById('dialogTitle').textContent = '编辑公告';
    document.getElementById('f_action').value = 'update';
    document.getElementById('f_id').value = d.id;
    document.getElementById('f_title').value = d.title;
    document.getElementById('f_content').value = d.content;
    document.getElementById('dialog').classList.add('show');
  }
  function closeDialog() { document.getElementById('dialog').classList.remove('show'); }
  document.querySelectorAll('[data-edit]').forEach(function (b) {
    b.addEventListener('click', function () { openEdit(b.dataset); });
  });
</script>
