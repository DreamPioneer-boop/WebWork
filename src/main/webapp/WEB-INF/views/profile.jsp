<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="个人基本信息" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header"><span>👤 个人基本信息</span></div>
  <div class="card-body">
    <form action="${ctx}/profile" method="post" style="max-width:600px">
      <input type="hidden" name="action" value="update">
      <div class="form-row">
        <label class="form-label">用户名</label>
        <input class="input" value="${profile.username}" disabled>
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>昵称</label>
        <input class="input" type="text" name="nickname" value="<c:out value='${profile.nickname}'/>">
      </div>
      <div class="form-row">
        <label class="form-label">性别</label>
        <div class="radio-group">
          <label><input type="radio" name="gender" value="男" ${profile.gender == '男' ? 'checked' : ''}> 男</label>
          <label><input type="radio" name="gender" value="女" ${profile.gender == '女' ? 'checked' : ''}> 女</label>
        </div>
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>年龄</label>
        <input class="input" type="number" name="age" min="1" max="120" value="${profile.age}">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>身高(cm)</label>
        <input class="input" type="number" name="height" min="50" max="250" step="0.1" value="${profile.height}">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>体重(kg)</label>
        <input class="input" type="number" name="weight" min="20" max="300" step="0.1" value="${profile.weight}">
      </div>
      <div class="form-row">
        <label class="form-label">健身经验</label>
        <div class="radio-group">
          <label><input type="radio" name="experienceLevel" value="BEGINNER" ${profile.experienceLevel == 'BEGINNER' ? 'checked' : ''}> 初学者</label>
          <label><input type="radio" name="experienceLevel" value="EXPERIENCED" ${profile.experienceLevel == 'EXPERIENCED' ? 'checked' : ''}> 有健身经验</label>
        </div>
      </div>
      <div class="form-row">
        <button class="btn btn-primary" type="submit">保存修改</button>
      </div>
    </form>
  </div>
</div>

<div class="card">
  <div class="card-header"><span>🔒 修改密码</span></div>
  <div class="card-body">
    <form action="${ctx}/profile" method="post" style="max-width:500px">
      <input type="hidden" name="action" value="password">
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>原密码</label>
        <input class="input" type="password" name="oldPassword" autocomplete="current-password">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>新密码</label>
        <input class="input" type="password" name="newPassword" autocomplete="new-password">
      </div>
      <div class="form-row">
        <label class="form-label"><span class="required">*</span>确认新密码</label>
        <input class="input" type="password" name="confirmNewPassword" autocomplete="new-password">
      </div>
      <div class="form-row">
        <button class="btn btn-warning" type="submit">修改密码</button>
      </div>
    </form>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
