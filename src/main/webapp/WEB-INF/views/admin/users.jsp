<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="用户管理" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>👥 用户管理</span>
    <form action="${ctx}/admin/users" method="get" class="search-box">
      <input class="input" name="keyword" value="<c:out value='${keyword}'/>" placeholder="搜索用户名/昵称">
      <button class="btn" type="submit">🔍</button>
    </form>
  </div>
  <div class="card-body">
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>ID</th><th>用户名</th><th>昵称</th><th>性别</th><th>年龄</th><th>健身经验</th><th>角色</th><th>状态</th><th>注册时间</th><th>操作</th></tr></thead>
        <tbody>
          <c:forEach var="u" items="${pageResult.records}">
            <tr>
              <td>${u.id}</td>
              <td>${u.username}</td>
              <td>${u.nickname}</td>
              <td>${u.gender}</td>
              <td>${u.age}</td>
              <td>${u.experienceLevel == 'EXPERIENCED' ? '有健身经验' : '初学者'}</td>
              <td><span class="tag tag-${u.role == 'ADMIN' ? 'danger' : 'info'}">${u.role == 'ADMIN' ? '管理员' : '普通用户'}</span></td>
              <td>
                <form action="${ctx}/admin/users" method="post" style="display:inline">
                  <input type="hidden" name="action" value="status">
                  <input type="hidden" name="id" value="${u.id}">
                  <input type="hidden" name="status" value="${u.status == 1 ? 0 : 1}">
                  <label class="switch">
                    <input type="checkbox" ${u.status == 1 ? 'checked' : ''} ${u.role == 'ADMIN' ? 'disabled' : ''} onchange="this.form.submit()">
                    <span class="slider"></span>
                  </label>
                </form>
              </td>
              <td>${u.createTime}</td>
              <td>
                <form action="${ctx}/admin/users" method="post" style="display:inline" data-confirm="确定将「${u.nickname}」${u.role == 'ADMIN' ? '设为普通用户' : '设为管理员'}吗？">
                  <input type="hidden" name="action" value="role">
                  <input type="hidden" name="id" value="${u.id}">
                  <input type="hidden" name="role" value="${u.role == 'ADMIN' ? 'USER' : 'ADMIN'}">
                  <button class="btn-link" type="submit">${u.role == 'ADMIN' ? '设为用户' : '设为管理员'}</button>
                </form>
                <form action="${ctx}/admin/users" method="post" style="display:inline" data-confirm="确定删除用户「${u.nickname}」吗？该操作不可恢复" ${u.role == 'ADMIN' ? 'onsubmit="return false"' : ''}>
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${u.id}">
                  <button class="btn-link danger" type="submit" ${u.role == 'ADMIN' ? 'disabled' : ''}>删除</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty pageResult.records}">
            <tr><td colspan="10"><div class="empty"><span class="empty-icon">👥</span>暂无用户</div></td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
    <c:set var="total" value="${pageResult.total}" scope="request"/>
    <c:set var="page" value="${page}" scope="request"/>
    <c:set var="size" value="${size}" scope="request"/>
    <c:set var="extraQuery" scope="request"><c:if test="${not empty keyword}">&amp;keyword=${keyword}</c:if></c:set>
    <jsp:include page="/WEB-INF/views/common/pagination.jsp"/>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
