package com.fitness.dao;

import com.fitness.entity.Comment;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论数据访问层（JDBC）。
 */
public class CommentDao {

    private Comment map(ResultSet rs) throws Exception {
        Comment c = new Comment();
        c.setId(rs.getLong("id"));
        c.setArticleId(rs.getLong("article_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setContent(rs.getString("content"));
        c.setCreateTime(rs.getString("create_time"));
        return c;
    }

    public List<Comment> listByArticle(Long articleId) {
        String sql = "SELECT * FROM t_comment WHERE article_id = ? ORDER BY create_time ASC, id ASC";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, articleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Comment c) {
        String sql = "INSERT INTO t_comment(article_id, user_id, content, create_time) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, c.getArticleId());
            ps.setLong(2, c.getUserId());
            ps.setString(3, c.getContent());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getLong(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByArticle(Long articleId) {
        String sql = "DELETE FROM t_comment WHERE article_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, articleId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
