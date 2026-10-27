<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="文章详情" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-body">
    <div class="page-header">
      <a class="back" href="${ctx}/article">‹ 返回</a>
      <span>文章详情</span>
    </div>
    <div class="detail">
      <h2 class="article-title">${article.title}</h2>
      <div class="article-meta">
        <span class="tag tag-${article.category == 'EXPERIENCE' ? 'success' : (article.category == 'TRAINING' ? 'primary' : 'warning')}">${article.category == 'EXPERIENCE' ? '经验分享' : (article.category == 'TRAINING' ? '训练方法' : '饮食建议')}</span>
        <span>作者：${article.authorNickname}</span>
        <span>发布于：${article.createTime}</span>
        <span>👁 ${article.viewCount} 浏览</span>
      </div>
      <div class="article-body">${article.content}</div>
      <div class="article-actions">
        <form action="${ctx}/article" method="post" style="display:inline">
          <input type="hidden" name="action" value="like">
          <input type="hidden" name="id" value="${article.id}">
          <button class="btn ${article.liked ? 'btn-danger' : ''}" type="submit">${article.liked ? '★ 已点赞' : '☆ 点赞'} ${article.likeCount}</button>
        </form>
      </div>
    </div>
  </div>
</div>

<div class="card">
  <div class="card-header"><span>💬 评论 (${fn:length(article.comments)})</span></div>
  <div class="card-body">
    <form action="${ctx}/article" method="post" class="comment-input">
      <input type="hidden" name="action" value="comment">
      <input type="hidden" name="id" value="${article.id}">
      <textarea class="textarea" name="content" rows="2" placeholder="发表你的看法..." maxlength="200"></textarea>
      <button class="btn btn-primary" type="submit" style="margin-top:8px">发表评论</button>
    </form>

    <c:choose>
      <c:when test="${empty article.comments}">
        <div class="empty"><span class="empty-icon">💬</span>暂无评论，快来抢沙发</div>
      </c:when>
      <c:otherwise>
        <c:forEach var="c" items="${article.comments}">
          <div class="comment-item">
            <span class="avatar">${fn:substring(c.nickname, 0, 1)}</span>
            <div class="comment-body">
              <div class="comment-nickname">${c.nickname}</div>
              <div class="comment-content">${c.content}</div>
              <div class="comment-time">${c.createTime}</div>
            </div>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
