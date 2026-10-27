package com.fitness.dao;

import com.fitness.entity.HealthRecord;
import com.fitness.entity.PageResult;
import com.fitness.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 身体指标记录数据访问层（JDBC）。
 */
public class HealthRecordDao {

    private HealthRecord map(ResultSet rs) throws Exception {
        HealthRecord r = new HealthRecord();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setWeight(rs.getBigDecimal("weight"));
        r.setHeight(rs.getBigDecimal("height"));
        r.setBmi(rs.getBigDecimal("bmi"));
        r.setBodyFatRate(rs.getBigDecimal("body_fat_rate"));
        r.setMuscleMass(rs.getBigDecimal("muscle_mass"));
        r.setSystolicPressure(rs.getObject("systolic_pressure") == null ? null : rs.getInt("systolic_pressure"));
        r.setDiastolicPressure(rs.getObject("diastolic_pressure") == null ? null : rs.getInt("diastolic_pressure"));
        r.setHeartRate(rs.getObject("heart_rate") == null ? null : rs.getInt("heart_rate"));
        r.setBloodSugar(rs.getBigDecimal("blood_sugar"));
        r.setSleepHours(rs.getBigDecimal("sleep_hours"));
        r.setRecordDate(rs.getString("record_date"));
        r.setNote(rs.getString("note"));
        r.setCreateTime(rs.getString("create_time"));
        return r;
    }

    public PageResult<HealthRecord> page(Long userId, int page, int size) {
        String countSql = "SELECT COUNT(*) FROM t_health_record WHERE user_id = ?";
        String dataSql = "SELECT * FROM t_health_record WHERE user_id = ? ORDER BY record_date DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection()) {
            long total;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    total = rs.next() ? rs.getLong(1) : 0;
                }
            }
            List<HealthRecord> list = new ArrayList<>();
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

    public void insert(HealthRecord r) {
        String sql = "INSERT INTO t_health_record(user_id, weight, height, bmi, body_fat_rate, muscle_mass, " +
                "systolic_pressure, diastolic_pressure, heart_rate, blood_sugar, sleep_hours, record_date, note, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getUserId());
            ps.setBigDecimal(2, r.getWeight());
            ps.setBigDecimal(3, r.getHeight());
            ps.setBigDecimal(4, r.getBmi());
            ps.setBigDecimal(5, r.getBodyFatRate());
            ps.setBigDecimal(6, r.getMuscleMass());
            ps.setObject(7, r.getSystolicPressure());
            ps.setObject(8, r.getDiastolicPressure());
            ps.setObject(9, r.getHeartRate());
            ps.setBigDecimal(10, r.getBloodSugar());
            ps.setBigDecimal(11, r.getSleepHours());
            ps.setString(12, r.getRecordDate());
            ps.setString(13, r.getNote());
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

    public void update(HealthRecord r) {
        String sql = "UPDATE t_health_record SET weight = ?, height = ?, bmi = ?, body_fat_rate = ?, muscle_mass = ?, " +
                "systolic_pressure = ?, diastolic_pressure = ?, heart_rate = ?, blood_sugar = ?, sleep_hours = ?, " +
                "record_date = ?, note = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, r.getWeight());
            ps.setBigDecimal(2, r.getHeight());
            ps.setBigDecimal(3, r.getBmi());
            ps.setBigDecimal(4, r.getBodyFatRate());
            ps.setBigDecimal(5, r.getMuscleMass());
            ps.setObject(6, r.getSystolicPressure());
            ps.setObject(7, r.getDiastolicPressure());
            ps.setObject(8, r.getHeartRate());
            ps.setBigDecimal(9, r.getBloodSugar());
            ps.setBigDecimal(10, r.getSleepHours());
            ps.setString(11, r.getRecordDate());
            ps.setString(12, r.getNote());
            ps.setLong(13, r.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM t_health_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HealthRecord findById(Long id) {
        String sql = "SELECT * FROM t_health_record WHERE id = ?";
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

    /** 最新一条（按记录日期降序取第一条） */
    public HealthRecord latest(Long userId) {
        String sql = "SELECT * FROM t_health_record WHERE user_id = ? ORDER BY record_date DESC, id DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 按记录日期升序（用于趋势图） */
    public List<HealthRecord> listByUserIdAsc(Long userId) {
        String sql = "SELECT * FROM t_health_record WHERE user_id = ? ORDER BY record_date ASC, id ASC";
        List<HealthRecord> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
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

    public long countAll() {
        String sql = "SELECT COUNT(*) FROM t_health_record";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
