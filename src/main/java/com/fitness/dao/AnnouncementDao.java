package com.fitness.dao;

import com.fitness.entity.Announcement;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告数据访问层（JDBC）。
 */
public class AnnouncementDao {

    private Announcement map(ResultSet rs) throws Exception {
        Announcement a = new Announcement();
        a.setId(rs.getLong("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setCreateTime(rs.getString("create_time"));
        a.setUpdateTime(rs.getString("update_time"));
        return a;
    }

    public List<Announcement> listAll() {
        String sql = "SELECT * FROM t_announcement ORDER BY create_time DESC, id DESC";
        List<Announcement> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Announcement findById(Long id) {
        String sql = "SELECT * FROM t_announcement WHERE id = ?";
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

    public void insert(Announcement a) {
        String sql = "INSERT INTO t_announcement(title, content, create_time, update_time) VALUES (?, ?, NOW(), NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getContent());
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

    public void update(Announcement a) {
        String sql = "UPDATE t_announcement SET title = ?, content = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getContent());
            ps.setLong(3, a.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM t_announcement WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
