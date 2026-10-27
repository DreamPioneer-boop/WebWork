package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.HealthRecordDao;
import com.fitness.entity.HealthRecord;
import com.fitness.entity.PageResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 身体指标记录服务。
 */
public class HealthRecordService {

    private final HealthRecordDao dao = new HealthRecordDao();

    public PageResult<HealthRecord> list(Long userId, int page, int size) {
        return dao.page(userId, page, size);
    }

    public HealthRecord create(Long userId, HealthRecord record) {
        record.setId(null);
        record.setUserId(userId);
        if (record.getBmi() == null && record.getWeight() != null && record.getHeight() != null) {
            record.setBmi(calcBmi(record.getWeight(), record.getHeight()));
        }
        dao.insert(record);
        return record;
    }

    public HealthRecord update(Long userId, Long id, HealthRecord record) {
        HealthRecord exist = getOwn(userId, id);
        exist.setWeight(record.getWeight());
        exist.setHeight(record.getHeight());
        exist.setBmi(record.getBmi());
        exist.setBodyFatRate(record.getBodyFatRate());
        exist.setMuscleMass(record.getMuscleMass());
        exist.setSystolicPressure(record.getSystolicPressure());
        exist.setDiastolicPressure(record.getDiastolicPressure());
        exist.setHeartRate(record.getHeartRate());
        exist.setBloodSugar(record.getBloodSugar());
        exist.setSleepHours(record.getSleepHours());
        exist.setRecordDate(record.getRecordDate());
        exist.setNote(record.getNote());
        if (exist.getBmi() == null && exist.getWeight() != null && exist.getHeight() != null) {
            exist.setBmi(calcBmi(exist.getWeight(), exist.getHeight()));
        }
        dao.update(exist);
        return exist;
    }

    public void delete(Long userId, Long id) {
        dao.delete(getOwn(userId, id).getId());
    }

    public HealthRecord latest(Long userId) {
        return dao.latest(userId);
    }

    /** 趋势数据：按日期升序，把各指标拆成数组，便于前端画折线图 */
    public Map<String, Object> trend(Long userId) {
        List<HealthRecord> list = dao.listByUserIdAsc(userId);
        List<String> dates = new ArrayList<>();
        List<BigDecimal> weight = new ArrayList<>();
        List<BigDecimal> bmi = new ArrayList<>();
        List<BigDecimal> bodyFatRate = new ArrayList<>();
        List<BigDecimal> muscleMass = new ArrayList<>();
        List<Integer> systolic = new ArrayList<>();
        List<Integer> diastolic = new ArrayList<>();
        List<Integer> heartRate = new ArrayList<>();
        List<BigDecimal> bloodSugar = new ArrayList<>();
        List<BigDecimal> sleepHours = new ArrayList<>();
        for (HealthRecord r : list) {
            dates.add(r.getRecordDate());
            weight.add(r.getWeight());
            bmi.add(r.getBmi());
            bodyFatRate.add(r.getBodyFatRate());
            muscleMass.add(r.getMuscleMass());
            systolic.add(r.getSystolicPressure());
            diastolic.add(r.getDiastolicPressure());
            heartRate.add(r.getHeartRate());
            bloodSugar.add(r.getBloodSugar());
            sleepHours.add(r.getSleepHours());
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dates", dates);
        result.put("weight", weight);
        result.put("bmi", bmi);
        result.put("bodyFatRate", bodyFatRate);
        result.put("muscleMass", muscleMass);
        result.put("systolic", systolic);
        result.put("diastolic", diastolic);
        result.put("heartRate", heartRate);
        result.put("bloodSugar", bloodSugar);
        result.put("sleepHours", sleepHours);
        return result;
    }

    private HealthRecord getOwn(Long userId, Long id) {
        HealthRecord record = dao.findById(id);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该记录");
        }
        return record;
    }

    private BigDecimal calcBmi(BigDecimal weight, BigDecimal height) {
        if (height == null || height.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal h = height.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return weight.divide(h.multiply(h), 1, RoundingMode.HALF_UP);
    }
}
