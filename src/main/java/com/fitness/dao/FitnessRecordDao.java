package com.fitness.dao;

import com.fitness.entity.FitnessRecord;
import com.fitness.entity.PageResult;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 健身记录数据访问层（JDBC）。
 */
public class FitnessRecordDao {

    private FitnessRecord map(ResultSet rs) throws Exception {
        FitnessRecord r = new FitnessRecord();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setExerciseType(rs.getString("exercise_type"));
        r.setDuration(rs.getInt("duration"));
        r.setCalories(rs.getObject("calories") == null ? null : rs.getInt("calories"));
        r.setIntensity(rs.getString("intensity"));
        r.setRecordDate(rs.getString("record_date"));
        r.setNote(rs.getString("note"));
        r.setCreateTime(rs.getString("create_time"));
        return r;
    }

    public PageResult<FitnessRecord> page(Long userId, int page, int size) {
        String countSql = "SELECT COUNT(*) FROM t_fitness_record WHERE user_id = ?";
        String dataSql = "SELECT * FROM t_fitness_record WHERE user_id = ? ORDER BY record_date DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    total = rs.next() ? rs.getLong(1) : 0;
                }
            }
            List<FitnessRecord> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {
                ps.setLong(1, userId);
                ps.setInt(2, (page - 1) * size);
                ps.setInt(3, size);
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

    public void insert(FitnessRecord r) {
        String sql = "INSERT INTO t_fitness_record(user_id, exercise_type, duration, calories, intensity, record_date, note, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getUserId());
            ps.setString(2, r.getExerciseType());
            ps.setInt(3, r.getDuration());
            ps.setObject(4, r.getCalories());
            ps.setString(5, r.getIntensity());
            ps.setString(6, r.getRecordDate());
            ps.setString(7, r.getNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getLong(1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(FitnessRecord r) {
        String sql = "UPDATE t_fitness_record SET exercise_type = ?, duration = ?, calories = ?, intensity = ?, " +
                "record_date = ?, note = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getExerciseType());
            ps.setInt(2, r.getDuration());
            ps.setObject(3, r.getCalories());
            ps.setString(4, r.getIntensity());
            ps.setString(5, r.getRecordDate());
            ps.setString(6, r.getNote());
            ps.setLong(7, r.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM t_fitness_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FitnessRecord findById(Long id) {
        String sql = "SELECT * FROM t_fitness_record WHERE id = ?";
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

    /** 聚合统计：返回 [总次数, 总时长, 总消耗] */
    public Object[] sumStats(Long userId) {
        String sql = "SELECT COUNT(*), COALESCE(SUM(duration), 0), COALESCE(SUM(calories), 0) FROM t_fitness_record WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{rs.getLong(1), rs.getLong(2), rs.getLong(3)};
                }
                return new Object[]{0L, 0L, 0L};
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM t_fitness_record";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
