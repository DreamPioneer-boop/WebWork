package com.fitness.dao;

import com.fitness.entity.PageResult;
import com.fitness.entity.User;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表数据访问层（JDBC）。
 */
public class UserDao {

    /** 结果集 -> 用户对象 */
    private User map(ResultSet rs) throws Exception {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setAvatar(rs.getString("avatar"));
        u.setGender(rs.getString("gender"));
        u.setAge(rs.getObject("age") == null ? null : rs.getInt("age"));
        u.setHeight(rs.getBigDecimal("height"));
        u.setWeight(rs.getBigDecimal("weight"));
        u.setExperienceLevel(rs.getString("experience_level"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getObject("status") == null ? null : rs.getInt("status"));
        u.setCreateTime(rs.getString("create_time"));
        u.setUpdateTime(rs.getString("update_time"));
        return u;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM t_user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM t_user WHERE id = ?";
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

    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    /** 新增用户，返回自增主键并写回 user.id */
    public void insert(User user) {
        String sql = "INSERT INTO t_user(username, password, nickname, avatar, gender, age, height, weight, " +
                "experience_level, role, status, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNickname());
            ps.setString(4, user.getAvatar());
            ps.setString(5, user.getGender());
            ps.setObject(6, user.getAge());
            ps.setBigDecimal(7, user.getHeight());
            ps.setBigDecimal(8, user.getWeight());
            ps.setString(9, user.getExperienceLevel());
            ps.setString(10, user.getRole());
            ps.setObject(11, user.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 更新个人资料（不含用户名、密码、角色、状态） */
    public void updateProfile(User user) {
        String sql = "UPDATE t_user SET nickname = ?, avatar = ?, gender = ?, age = ?, height = ?, weight = ?, " +
                "experience_level = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNickname());
            ps.setString(2, user.getAvatar());
            ps.setString(3, user.getGender());
            ps.setObject(4, user.getAge());
            ps.setBigDecimal(5, user.getHeight());
            ps.setBigDecimal(6, user.getWeight());
            ps.setString(7, user.getExperienceLevel());
            ps.setLong(8, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(Long id, String passwordHash) {
        String sql = "UPDATE t_user SET password = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStatus(Long id, int status) {
        String sql = "UPDATE t_user SET status = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRole(Long id, String role) {
        String sql = "UPDATE t_user SET role = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM t_user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM t_user";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 分页查询用户，支持用户名/昵称关键字模糊搜索 */
    public PageResult<User> page(String keyword, int page, int size) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        StringBuilder where = new StringBuilder();
        if (hasKeyword) {
            where.append(" WHERE username LIKE ? OR nickname LIKE ?");
        }
        String countSql = "SELECT COUNT(*) FROM t_user" + where;
        String dataSql = "SELECT * FROM t_user" + where + " ORDER BY create_time DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                if (hasKeyword) {
                    ps.setString(1, "%" + keyword + "%");
                    ps.setString(2, "%" + keyword + "%");
                }
                try (ResultSet rs = ps.executeQuery()) {
                    total = rs.next() ? rs.getLong(1) : 0;
                }
            }
            List<User> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                int idx = 1;
                if (hasKeyword) {
                    ps.setString(idx++, "%" + keyword + "%");
                    ps.setString(idx++, "%" + keyword + "%");
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

    /** 最近注册的 limit 个用户 */
    public List<User> recentUsers(int limit) {
        String sql = "SELECT * FROM t_user ORDER BY create_time DESC LIMIT ?";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
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

    /** 所有用户注册月份（"yyyy-MM"），用于近 6 个月增长统计 */
    public List<String> listCreateTimeMonths() {
        String sql = "SELECT DATE_FORMAT(create_time, '%Y-%m') FROM t_user WHERE create_time IS NOT NULL";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
