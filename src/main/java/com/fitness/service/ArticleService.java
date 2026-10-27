package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.ArticleDao;
import com.fitness.dao.ArticleLikeDao;
import com.fitness.dao.CommentDao;
import com.fitness.dao.UserDao;
import com.fitness.entity.Article;
import com.fitness.entity.ArticleVO;
import com.fitness.entity.Comment;
import com.fitness.entity.CommentVO;
import com.fitness.entity.PageResult;
import com.fitness.entity.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章（社区）服务：浏览、发布、编辑、删除、点赞、评论、审核。
 */
public class ArticleService {

    private final ArticleDao articleDao = new ArticleDao();
    private final UserDao userDao = new UserDao();
    private final CommentDao commentDao = new CommentDao();
    private final ArticleLikeDao likeDao = new ArticleLikeDao();

    public PageResult<ArticleVO> listApproved(String category, String keyword, int page, int size) {
        PageResult<Article> p = articleDao.pageApproved(category, keyword, page, size);
        List<ArticleVO> vos = new ArrayList<>();
        for (Article a : p.getRecords()) {
            vos.add(toVO(a, false, false));
        }
        return new PageResult<>(p.getTotal(), vos);
    }

    public ArticleVO detail(Long id, Long currentUserId) {
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!"APPROVED".equals(article.getStatus())) {
            boolean isOwner = currentUserId != null && currentUserId.equals(article.getUserId());
            boolean isAdmin = currentUserId != null && isAdminUser(currentUserId);
            if (!isOwner && !isAdmin) {
                throw new BusinessException("该文章尚未通过审核");
            }
        }
        articleDao.incrementViewCount(id);
        article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
        boolean liked = currentUserId != null && likeDao.exists(id, currentUserId);
        return toVO(article, true, liked);
    }

    public ArticleVO publish(Long userId, Article article) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"ADMIN".equals(user.getRole()) && !"EXPERIENCED".equals(user.getExperienceLevel())) {
            throw new BusinessException("只有有健身经验的用户才能发布内容");
        }
        article.setId(null);
        article.setUserId(userId);
        article.setStatus("PENDING");
        article.setRejectReason(null);
        article.setViewCount(0);
        article.setLikeCount(0);
        articleDao.insert(article);
        return toVO(article, false, false);
    }

    public PageResult<ArticleVO> myList(Long userId, int page, int size) {
        PageResult<Article> p = articleDao.pageByUser(userId, page, size);
        List<ArticleVO> vos = new ArrayList<>();
        for (Article a : p.getRecords()) {
            vos.add(toVO(a, false, false));
        }
        return new PageResult<>(p.getTotal(), vos);
    }

    public void update(Long userId, Long id, Article article) {
        Article exist = getOwn(userId, id);
        exist.setTitle(article.getTitle());
        exist.setCategory(article.getCategory());
        exist.setContent(article.getContent());
        exist.setStatus("PENDING");
        exist.setRejectReason(null);
        articleDao.updateContent(exist);
    }

    public void delete(Long userId, Long id) {
        Article exist = getOwn(userId, id);
        commentDao.deleteByArticle(id);
        likeDao.deleteByArticle(id);
        articleDao.delete(exist.getId());
    }

    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        boolean liked;
        if (likeDao.exists(articleId, userId)) {
            likeDao.delete(articleId, userId);
            liked = false;
        } else {
            likeDao.insert(articleId, userId);
            liked = true;
        }
        long count = likeDao.countByArticle(articleId);
        articleDao.updateLikeCount(articleId, (int) count);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("liked", liked);
        result.put("likeCount", count);
        return result;
    }

    public void addComment(Long articleId, Long userId, String content) {
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!"APPROVED".equals(article.getStatus())) {
            throw new BusinessException("文章未通过审核，无法评论");
        }
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentDao.insert(comment);
    }

    public List<CommentVO> comments(Long articleId) {
        List<CommentVO> vos = new ArrayList<>();
        for (Comment c : commentDao.listByArticle(articleId)) {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setArticleId(c.getArticleId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            User u = userDao.findById(c.getUserId());
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            vos.add(vo);
        }
        return vos;
    }

    public ArticleVO toVO(Article a, boolean withComments, boolean liked) {
        ArticleVO vo = new ArticleVO();
        vo.setId(a.getId());
        vo.setUserId(a.getUserId());
        vo.setTitle(a.getTitle());
        vo.setCategory(a.getCategory());
        vo.setContent(a.getContent());
        vo.setCover(a.getCover());
        vo.setStatus(a.getStatus());
        vo.setRejectReason(a.getRejectReason());
        vo.setViewCount(a.getViewCount());
        vo.setLikeCount(a.getLikeCount());
        vo.setCreateTime(a.getCreateTime());
        vo.setUpdateTime(a.getUpdateTime());
        User author = userDao.findById(a.getUserId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
            vo.setAuthorExperienceLevel(author.getExperienceLevel());
        }
        vo.setLiked(liked);
        if (withComments) {
            vo.setComments(comments(a.getId()));
        }
        return vo;
    }

    private boolean isAdminUser(Long userId) {
        User u = userDao.findById(userId);
        return u != null && "ADMIN".equals(u.getRole());
    }

    private Article getOwn(Long userId, Long id) {
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!article.getUserId().equals(userId) && !isAdminUser(userId)) {
            throw new BusinessException("无权操作该文章");
        }
        return article;
    }
}
