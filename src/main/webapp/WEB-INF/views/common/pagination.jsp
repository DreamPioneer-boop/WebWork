<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="sp" value="${pageContext.request.servletPath}"/>
<c:set var="extra" value="${empty extraQuery ? '' : extraQuery}"/>
<div class="pagination">
  <span>共 ${total} 条 · 第 ${page} 页</span>
  <a class="page-btn${page <= 1 ? ' disabled' : ''}" href="${ctx}${sp}?page=${page > 1 ? page - 1 : 1}&size=${size}${extra}">‹ 上一页</a>
  <a class="page-btn${page * size >= total ? ' disabled' : ''}" href="${ctx}${sp}?page=${page + 1}&size=${size}${extra}">下一页 ›</a>
  <span>
    <a href="${ctx}${sp}?page=1&size=10${extra}">10</a> /
    <a href="${ctx}${sp}?page=1&size=20${extra}">20</a> /
    <a href="${ctx}${sp}?page=1&size=50${extra}">50</a> 条/页
  </span>
</div>
