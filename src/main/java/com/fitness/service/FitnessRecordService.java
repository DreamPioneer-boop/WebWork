package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.FitnessRecordDao;
import com.fitness.entity.FitnessRecord;
import com.fitness.entity.PageResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健身记录服务。
 */
public class FitnessRecordService {

    private final FitnessRecordDao dao = new FitnessRecordDao();

    public PageResult<FitnessRecord> list(Long userId, int page, int size) {
        return dao.page(userId, page, size);
    }

    public void create(Long userId, FitnessRecord record) {
        record.setId(null);
        record.setUserId(userId);
        dao.insert(record);
    }

    public void update(Long userId, Long id, FitnessRecord record) {
        FitnessRecord exist = getOwn(userId, id);
        exist.setExerciseType(record.getExerciseType());
        exist.setDuration(record.getDuration());
        exist.setCalories(record.getCalories());
        exist.setIntensity(record.getIntensity());
        exist.setRecordDate(record.getRecordDate());
        exist.setNote(record.getNote());
        dao.update(exist);
    }

    public void delete(Long userId, Long id) {
        dao.delete(getOwn(userId, id).getId());
    }

    /** 累计统计：总次数 / 总时长 / 总消耗 */
    public Map<String, Object> stats(Long userId) {
        Object[] row = dao.sumStats(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCount", row[0]);
        result.put("totalDuration", row[1]);
        result.put("totalCalories", row[2]);
        return result;
    }

    private FitnessRecord getOwn(Long userId, Long id) {
        FitnessRecord record = dao.findById(id);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该记录");
        }
        return record;
    }
}
