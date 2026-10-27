<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="健康建议" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card">
  <div class="card-header"><span>💡 基础健康建议</span></div>
  <div class="card-body">
    <c:choose>
      <c:when test="${!hasData}">
        <div class="empty"><span class="empty-icon">📭</span>暂无身体指标数据，请先添加身体指标记录
          <div style="margin-top:14px"><a class="btn btn-primary" href="${ctx}/health-record">去记录</a></div>
        </div>
      </c:when>
      <c:otherwise>
        <div class="alert alert-info">基于 <b>${recordDate}</b> 的最新身体指标生成以下建议，仅供参考，如有不适请咨询专业医生。</div>
        <c:if test="${empty adviceList}">
          <div class="empty"><span class="empty-icon">💡</span>暂无建议</div>
        </c:if>
        <div class="advice-grid">
          <c:forEach var="item" items="${adviceList}">
            <div class="card hover advice-card ${item.level}">
              <div class="card-body">
                <div class="advice-header">
                  <span class="tag tag-${item.level == 'good' ? 'success' : (item.level == 'warn' ? 'warning' : 'danger')}">${item.level == 'good' ? '正常' : (item.level == 'warn' ? '注意' : '需关注')}</span>
                  <span class="advice-title">${item.title}</span>
                </div>
                <div class="advice-content">${item.content}</div>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
