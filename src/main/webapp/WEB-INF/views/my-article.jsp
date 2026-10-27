<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="我的发布" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>📝 我的发布</span>
    <button class="btn btn-primary" onclick="openAdd()">＋ 发布新文章</button>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>标题</th><th>分类</th><th>状态</th><th>驳回原因</th><th>浏览</th><th>点赞</th><th>发布时间</th><th>操作</th></tr></thead>
        <tbody>
          <c:forEach var="a" items="${pageResult.records}">
            <tr>
              <td>${a.title}</td>
              <td><span class="tag tag-${a.category == 'EXPERIENCE' ? 'success' : (a.category == 'TRAINING' ? 'primary' : 'warning')}">${a.category == 'EXPERIENCE' ? '经验分享' : (a.category == 'TRAINING' ? '训练方法' : '饮食建议')}</span></td>
              <td><span class="tag tag-${a.status == 'PENDING' ? 'warning' : (a.status == 'APPROVED' ? 'success' : 'danger')}">${a.status == 'PENDING' ? '待审核' : (a.status == 'APPROVED' ? '已通过' : '已驳回')}</span></td>
              <td>${empty a.rejectReason ? '—' : a.rejectReason}</td>
              <td>${a.viewCount}</td>
              <td>${a.likeCount}</td>
              <td>${a.createTime}</td>
              <td>
                <button class="btn-link" data-edit data-id="${a.id}" data-title="<c:out value='${a.title}'/>" data-category="${a.category}" data-content="<c:out value='${a.content}'/>">编辑</button>
                <form action="${ctx}/article" method="post" style="display:inline" data-confirm="确定删除这篇文章吗？">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${a.id}">
                  <button class="btn-link danger" type="submit">删除</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty pageResult.records}">
            <tr><td colspan="8"><div class="empty"><span class="empty-icon">📝</span>还没有发布过文章</div></td></tr>
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

<!-- 发布 / 编辑弹窗 -->
<div class="modal-mask" id="dialog">
  <div class="modal" style="width:640px">
    <div class="modal-header"><span id="dialogTitle">发布文章</span><button class="modal-close" onclick="closeDialog()">×</button></div>
    <div class="modal-body">
      <form action="${ctx}/article" method="post" id="articleForm">
        <input type="hidden" name="action" id="f_action" value="publish">
        <input type="hidden" name="id" id="f_id">
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>标题</label>
          <input class="input" name="title" id="f_title" maxlength="50" placeholder="请输入文章标题">
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>分类</label>
          <div class="radio-group">
            <label><input type="radio" name="category" value="EXPERIENCE" checked> 经验分享</label>
            <label><input type="radio" name="category" value="TRAINING"> 训练方法</label>
            <label><input type="radio" name="category" value="DIET"> 饮食建议</label>
          </div>
        </div>
        <div class="form-row">
          <label class="form-label"><span class="required">*</span>内容</label>
          <textarea class="textarea" name="content" id="f_content" rows="10" placeholder="分享你的健身经验、训练方法或饮食建议..."></textarea>
        </div>
        <div class="alert alert-info">发布后需经管理员审核，审核通过后才会展示在经验分享列表中</div>
        <div class="modal-footer">
          <button class="btn" type="button" onclick="closeDialog()">取消</button>
          <button class="btn btn-primary" type="submit">提交</button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
<script>
  function openAdd() {
    document.getElementById('dialogTitle').textContent = '发布文章';
    document.getElementById('f_action').value = 'publish';
    document.getElementById('f_id').value = '';
    document.getElementById('f_title').value = '';
    document.getElementById('f_content').value = '';
    document.querySelector('input[name="category"][value="EXPERIENCE"]').checked = true;
    document.getElementById('dialog').classList.add('show');
  }
  function openEdit(d) {
    document.getElementById('dialogTitle').textContent = '编辑文章';
    document.getElementById('f_action').value = 'update';
    document.getElementById('f_id').value = d.id;
    document.getElementById('f_title').value = d.title;
    document.getElementById('f_content').value = d.content;
    var rb = document.querySelector('input[name="category"][value="' + d.category + '"]');
    if (rb) rb.checked = true;
    document.getElementById('dialog').classList.add('show');
  }
  function closeDialog() { document.getElementById('dialog').classList.remove('show'); }
  document.querySelectorAll('[data-edit]').forEach(function (b) {
    b.addEventListener('click', function () { openEdit(b.dataset); });
  });
</script>
