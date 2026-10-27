package com.fitness.dao;

import com.fitness.entity.Article;
import com.fitness.entity.PageResult;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章数据访问层（JDBC）。
 */
public class ArticleDao {

    private Article map(ResultSet rs) throws Exception {
        Article a = new Article();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setTitle(rs.getString("title"));
        a.setCategory(rs.getString("category"));
        a.setContent(rs.getString("content"));
        a.setCover(rs.getString("cover"));
        a.setStatus(rs.getString("status"));
        a.setRejectReason(rs.getString("reject_reason"));
        a.setViewCount(rs.getObject("view_count") == null ? 0 : rs.getInt("view_count"));
        a.setLikeCount(rs.getObject("like_count") == null ? 0 : rs.getInt("like_count"));
        a.setCreateTime(rs.getString("create_time"));
        a.setUpdateTime(rs.getString("update_time"));
        return a;
    }

    public void insert(Article a) {
        String sql = "INSERT INTO t_article(user_id, title, category, content, cover, status, reject_reason, view_count, like_count, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getUserId());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getCategory());
            ps.setString(4, a.getContent());
            ps.setString(5, a.getCover());
            ps.setString(6, a.getStatus());
            ps.setString(7, a.getRejectReason());
            ps.setInt(8, a.getViewCount() == null ? 0 : a.getViewCount());
            ps.setInt(9, a.getLikeCount() == null ? 0 : a.getLikeCount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setId(keys.getLong(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 更新文章正文（编辑：标题/分类/内容 + 重新待审核） */
    public void updateContent(Article a) {
        String sql = "UPDATE t_article SET title = ?, category = ?, content = ?, cover = ?, status = ?, reject_reason = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getCategory());
            ps.setString(3, a.getContent());
            ps.setString(4, a.getCover());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getRejectReason());
            ps.setLong(7, a.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 审核：更新状态与驳回原因 */
    public void audit(Long id, String status, String rejectReason) {
        String sql = "UPDATE t_article SET status = ?, reject_reason = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, rejectReason);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM t_article WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Article findById(Long id) {
        String sql = "SELECT * FROM t_article WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementViewCount(Long id) {
        String sql = "UPDATE t_article SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLikeCount(Long id, int count) {
        String sql = "UPDATE t_article SET like_count = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 已通过审核的文章分页（支持分类 + 标题关键字） */
    public PageResult<Article> pageApproved(String category, String keyword, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE status = 'APPROVED'");
        List<String> params = new ArrayList<>();
        if (category != null && !category.isBlank()) {
            where.append(" AND category = ?");
            params.add(category);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }
        return pageWithWhere(where.toString(), params, page, size);
    }

    /** 管理员审核列表（状态筛选 + 标题关键字） */
    public PageResult<Article> search(String status, String keyword, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<String> params = new ArrayList<>();
        if (status != null && !status.isBlank()) {
            where.append(" AND status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND title LIKE ?");
            params.add("%" + keyword + "%");
        }
        return pageWithWhere(where.toString(), params, page, size);
    }

    /** 某用户自己的文章分页 */
    public PageResult<Article> pageByUser(Long userId, int page, int size) {
        return pageWithWhere(" WHERE user_id = ?", List.of(String.valueOf(userId)), page, size);
    }

    private PageResult<Article> pageWithWhere(String where, List<String> params, int page, int size) {
        String countSql = "SELECT COUNT(*) FROM t_article" + where;
        String dataSql = "SELECT * FROM t_article" + where + " ORDER BY create_time DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    total = rs.next() ? rs.getLong(1) : 0;
                }
            }
            List<Article> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int idx = 1;
                for (String p : params) {
                    ps.setString(idx++, p);
                }
                ps.setInt(idx++, (page - 1) * size);
                ps.setInt(idx, size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                }
            }
            return new PageResult<>(total, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM t_article WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM t_article";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 按分类统计：返回 [分类代码, 数量] */
    public List<Object[]> countByCategory() {
        String sql = "SELECT category, COUNT(*) FROM t_article GROUP BY category";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getLong(2)});
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
