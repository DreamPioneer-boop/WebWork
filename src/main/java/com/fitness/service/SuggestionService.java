package com.fitness.service;

import com.fitness.dao.HealthRecordDao;
import com.fitness.dao.UserDao;
import com.fitness.entity.HealthRecord;
import com.fitness.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康建议规则引擎：根据最新身体指标，用一串 if/else 规则生成中文建议。
 */
public class SuggestionService {

    private final HealthRecordDao healthRecordDao = new HealthRecordDao();
    private final UserDao userDao = new UserDao();

    public Map<String, Object> latest(Long userId) {
        HealthRecord record = healthRecordDao.latest(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (record == null) {
            result.put("hasData", false);
            result.put("adviceList", Collections.emptyList());
            return result;
        }

        User user = userDao.findById(userId);
        List<Map<String, String>> adviceList = new ArrayList<>();

        if (record.getBmi() != null) {
            adviceList.add(bmiAdvice(record.getBmi()));
        }
        if (record.getSystolicPressure() != null && record.getDiastolicPressure() != null) {
            adviceList.add(bloodPressureAdvice(record.getSystolicPressure(), record.getDiastolicPressure()));
        }
        if (record.getHeartRate() != null) {
            adviceList.add(heartRateAdvice(record.getHeartRate()));
        }
        if (record.getBloodSugar() != null) {
            adviceList.add(bloodSugarAdvice(record.getBloodSugar()));
        }
        if (record.getSleepHours() != null) {
            adviceList.add(sleepAdvice(record.getSleepHours()));
        }
        if (record.getBodyFatRate() != null && user != null) {
            adviceList.add(bodyFatAdvice(record.getBodyFatRate(), user.getGender()));
        }
        adviceList.add(generalAdvice(user));

        result.put("hasData", true);
        result.put("recordDate", record.getRecordDate());
        result.put("adviceList", adviceList);
        return result;
    }

    private Map<String, String> advice(String title, String level, String content) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("title", title);
        m.put("level", level);
        m.put("content", content);
        return m;
    }

    private Map<String, String> bmiAdvice(BigDecimal bmi) {
        double v = bmi.doubleValue();
        if (v < 18.5) {
            return advice("体重管理", "warn", "您的 BMI 为 " + bmi + "，属于偏瘦。建议适当增加蛋白质和优质碳水的摄入，配合力量训练增加肌肉量。");
        } else if (v < 24) {
            return advice("体重管理", "good", "您的 BMI 为 " + bmi + "，处于正常范围，请继续保持均衡饮食和规律运动。");
        } else if (v < 28) {
            return advice("体重管理", "warn", "您的 BMI 为 " + bmi + "，属于超重。建议控制高热量食物摄入，每周进行 3-5 次有氧运动。");
        }
        return advice("体重管理", "bad", "您的 BMI 为 " + bmi + "，属于肥胖范围。建议在专业指导下制定减重计划，避免高强度运动对关节造成负担。");
    }

    private Map<String, String> bloodPressureAdvice(int systolic, int diastolic) {
        if (systolic >= 140 || diastolic >= 90) {
            return advice("血压", "bad", "您的血压为 " + systolic + "/" + diastolic + " mmHg，偏高。建议低盐饮食、规律作息，并定期监测，必要时就医。");
        } else if (systolic < 90 || diastolic < 60) {
            return advice("血压", "warn", "您的血压为 " + systolic + "/" + diastolic + " mmHg，偏低。注意补充水分和盐分，避免长时间站立，起身时动作放缓。");
        }
        return advice("血压", "good", "您的血压为 " + systolic + "/" + diastolic + " mmHg，处于正常范围。");
    }

    private Map<String, String> heartRateAdvice(int heartRate) {
        if (heartRate > 100) {
            return advice("心率", "warn", "您的静息心率为 " + heartRate + " 次/分，偏快。建议减少咖啡因摄入，保证充足睡眠，如持续偏高请就医检查。");
        } else if (heartRate < 60) {
            return advice("心率", "warn", "您的静息心率为 " + heartRate + " 次/分，偏慢。若长期规律运动属正常现象，如伴有头晕乏力请及时就医。");
        }
        return advice("心率", "good", "您的静息心率为 " + heartRate + " 次/分，处于正常范围。");
    }

    private Map<String, String> bloodSugarAdvice(BigDecimal bloodSugar) {
        double v = bloodSugar.doubleValue();
        if (v > 7.0) {
            return advice("血糖", "bad", "您的血糖为 " + bloodSugar + " mmol/L，偏高。建议控制精制糖摄入，增加膳食纤维，并及时就医。");
        } else if (v >= 6.1) {
            return advice("血糖", "warn", "您的血糖为 " + bloodSugar + " mmol/L，处于偏高临界值。建议减少甜食和含糖饮料，加强运动。");
        } else if (v < 3.9) {
            return advice("血糖", "warn", "您的血糖为 " + bloodSugar + " mmol/L，偏低。注意规律进食，随身备一些含糖食物以防低血糖。");
        }
        return advice("血糖", "good", "您的血糖为 " + bloodSugar + " mmol/L，处于正常范围。");
    }

    private Map<String, String> sleepAdvice(BigDecimal sleepHours) {
        double v = sleepHours.doubleValue();
        if (v < 6) {
            return advice("睡眠", "warn", "您的睡眠时长为 " + sleepHours + " 小时，偏少。建议固定作息、睡前远离电子设备，保证每天 7-8 小时睡眠。");
        } else if (v > 9) {
            return advice("睡眠", "warn", "您的睡眠时长为 " + sleepHours + " 小时，偏多。长期嗜睡可能与睡眠质量差有关，建议规律作息并适当增加运动。");
        }
        return advice("睡眠", "good", "您的睡眠时长为 " + sleepHours + " 小时，处于健康范围。");
    }

    private Map<String, String> bodyFatAdvice(BigDecimal bodyFatRate, String gender) {
        double v = bodyFatRate.doubleValue();
        boolean female = "女".equals(gender);
        double normalMin = female ? 18 : 10;
        double normalMax = female ? 28 : 20;
        if (v < normalMin) {
            return advice("体脂率", "warn", "您的体脂率为 " + bodyFatRate + "%，偏低。建议增加营养摄入并配合力量训练。");
        } else if (v <= normalMax) {
            return advice("体脂率", "good", "您的体脂率为 " + bodyFatRate + "%，处于正常范围。");
        }
        return advice("体脂率", "warn", "您的体脂率为 " + bodyFatRate + "%，偏高。建议控制饮食、增加有氧和力量训练以降低体脂。");
    }

    private Map<String, String> generalAdvice(User user) {
        String content = (user != null && "EXPERIENCED".equals(user.getExperienceLevel()))
                ? "作为有经验的健身者，建议继续保持规律训练，注重动作质量和训练周期化，同时关注营养与恢复。"
                : "作为初学者，建议从低强度运动开始，每周 2-3 次，循序渐进。运动前充分热身、运动后拉伸，注意动作规范，避免受伤。";
        return advice("综合建议", "good", content);
    }
}
