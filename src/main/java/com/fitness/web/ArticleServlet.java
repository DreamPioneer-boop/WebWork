package com.fitness.web;

import com.fitness.common.BusinessException;
import com.fitness.entity.Article;
import com.fitness.entity.User;
import com.fitness.service.ArticleService;
import com.fitness.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 文章模块：列表 / 详情 / 发布 / 编辑 / 删除 / 点赞 / 评论。
 */
@WebServlet("/article")
public class ArticleServlet extends HttpServlet {

    private final ArticleService service = new ArticleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        String action = req.getParameter("action");
        try {
            if ("detail".equals(action)) {
                Long id = Long.valueOf(req.getParameter("id"));
                req.setAttribute("article", service.detail(id, user.getId()));
                req.getRequestDispatcher("/WEB-INF/views/article-detail.jsp").forward(req, resp);
                return;
            }
            // 列表（已通过审核）
            int page = WebUtil.intParam(req, "page", 1);
            int size = WebUtil.intParam(req, "size", 10);
            String category = req.getParameter("category");
            String keyword = req.getParameter("keyword");
            req.setAttribute("pageResult", service.listApproved(category, keyword, page, size));
            req.setAttribute("category", category);
            req.setAttribute("keyword", keyword);
            req.setAttribute("page", page);
            req.setAttribute("size", size);
            req.getRequestDispatcher("/WEB-INF/views/article-list.jsp").forward(req, resp);
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
            resp.sendRedirect(req.getContextPath() + "/article");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action) {
                case "publish" -> {
                    Article article = parseArticle(req);
                    service.publish(user.getId(), article);
                    WebUtil.flash(req, "发布成功，等待管理员审核");
                    resp.sendRedirect(req.getContextPath() + "/my-article");
                    return;
                }
                case "update" -> {
                    Article article = parseArticle(req);
                    article.setId(Long.valueOf(req.getParameter("id")));
                    service.update(user.getId(), article.getId(), article);
                    WebUtil.flash(req, "修改成功，已重新提交审核");
                    resp.sendRedirect(req.getContextPath() + "/my-article");
                    return;
                }
                case "delete" -> {
                    service.delete(user.getId(), Long.valueOf(req.getParameter("id")));
                    WebUtil.flash(req, "删除成功");
                    resp.sendRedirect(req.getContextPath() + "/my-article");
                    return;
                }
                case "like" -> {
                    Long id = Long.valueOf(req.getParameter("id"));
                    service.toggleLike(id, user.getId());
                    resp.sendRedirect(req.getContextPath() + "/article?action=detail&id=" + id);
                    return;
                }
                case "comment" -> {
                    Long id = Long.valueOf(req.getParameter("id"));
                    String content = req.getParameter("content");
                    service.addComment(id, user.getId(), content == null ? "" : content.trim());
                    resp.sendRedirect(req.getContextPath() + "/article?action=detail&id=" + id);
                    return;
                }
                default -> resp.sendRedirect(req.getContextPath() + "/article");
            }
        } catch (BusinessException | NumberFormatException e) {
            WebUtil.flash(req, e instanceof BusinessException ? e.getMessage() : "参数错误");
            resp.sendRedirect(req.getContextPath() + "/article");
        }
    }

    private Article parseArticle(HttpServletRequest req) {
        Article a = new Article();
        a.setTitle(req.getParameter("title"));
        a.setCategory(req.getParameter("category"));
        a.setContent(req.getParameter("content"));
        return a;
    }
}
