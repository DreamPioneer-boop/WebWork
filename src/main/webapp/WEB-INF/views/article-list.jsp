<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="经验分享" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header">
    <span>📚 经验分享</span>
    <c:if test="${sessionScope.user.role == 'ADMIN' || sessionScope.user.experienceLevel == 'EXPERIENCED'}">
      <a class="btn btn-primary" href="${ctx}/my-article">发布经验</a>
    </c:if>
  </div>
  <div class="card-body">
    <form action="${ctx}/article" method="get" class="filter-bar">
      <select class="select" name="category" onchange="this.form.submit()" style="width:160px">
        <option value="" ${empty category ? 'selected' : ''}>全部分类</option>
        <option value="EXPERIENCE" ${category == 'EXPERIENCE' ? 'selected' : ''}>经验分享</option>
        <option value="TRAINING" ${category == 'TRAINING' ? 'selected' : ''}>训练方法</option>
        <option value="DIET" ${category == 'DIET' ? 'selected' : ''}>饮食建议</option>
      </select>
      <div class="search-box">
        <input class="input" name="keyword" value="<c:out value='${keyword}'/>" placeholder="搜索文章标题">
        <button class="btn" type="submit">🔍</button>
      </div>
    </form>

    <c:choose>
      <c:when test="${empty pageResult.records}">
        <div class="empty"><span class="empty-icon">📚</span>暂无文章</div>
      </c:when>
      <c:otherwise>
        <c:forEach var="a" items="${pageResult.records}">
          <div class="article-item" onclick="location.href='${ctx}/article?action=detail&id=${a.id}'">
            <div class="article-title-row">
              <span class="tag tag-${a.category == 'EXPERIENCE' ? 'success' : (a.category == 'TRAINING' ? 'primary' : 'warning')}">${a.category == 'EXPERIENCE' ? '经验分享' : (a.category == 'TRAINING' ? '训练方法' : '饮食建议')}</span>
              <span class="article-title">${a.title}</span>
            </div>
            <div class="article-content">${a.content}</div>
            <div class="article-meta">
              <span>👤 ${a.authorNickname}</span>
              <span>👁 ${a.viewCount} 浏览</span>
              <span>👍 ${a.likeCount} 点赞</span>
              <span>🕐 ${a.createTime}</span>
            </div>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>

    <c:set var="total" value="${pageResult.total}" scope="request"/>
    <c:set var="page" value="${page}" scope="request"/>
    <c:set var="size" value="${size}" scope="request"/>
    <c:set var="extraQuery" scope="request"><c:if test="${not empty category}">&amp;category=${category}</c:if><c:if test="${not empty keyword}">&amp;keyword=${keyword}</c:if></c:set>
    <jsp:include page="/WEB-INF/views/common/pagination.jsp"/>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
