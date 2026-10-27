package com.fitness.web;

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
 * 我的发布：分页展示当前用户自己的文章。
 */
@WebServlet("/my-article")
public class MyArticleServlet extends HttpServlet {

    private final ArticleService service = new ArticleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        int page = WebUtil.intParam(req, "page", 1);
        int size = WebUtil.intParam(req, "size", 10);
        req.setAttribute("pageResult", service.myList(user.getId(), page, size));
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.getRequestDispatcher("/WEB-INF/views/my-article.jsp").forward(req, resp);
    }
}
