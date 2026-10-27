package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.ArticleDao;
import com.fitness.dao.FitnessRecordDao;
import com.fitness.dao.HealthRecordDao;
import com.fitness.dao.UserDao;
import com.fitness.entity.Article;
import com.fitness.entity.ArticleVO;
import com.fitness.entity.PageResult;
import com.fitness.entity.User;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 管理员后台服务：统计、用户管理、内容审核。
 */
public class AdminService {

    private final UserDao userDao = new UserDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final HealthRecordDao healthRecordDao = new HealthRecordDao();
    private final FitnessRecordDao fitnessRecordDao = new FitnessRecordDao();
    private final ArticleService articleService = new ArticleService();

    public Map<String, Object> stats() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalUsers", userDao.countAll());
        result.put("totalArticles", articleDao.countAll());
        result.put("totalHealthRecords", healthRecordDao.countAll());
        result.put("totalFitnessRecords", fitnessRecordDao.countAll());
        result.put("pendingArticles", articleDao.countByStatus("PENDING"));

        // 近 6 个月用户增长
        Map<String, Long> monthCount = new TreeMap<>();
        for (String m : userDao.listCreateTimeMonths()) {
            monthCount.merge(m, 1L, Long::sum);
        }
        YearMonth now = YearMonth.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<Map<String, Object>> userGrowth = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            String key = now.minusMonths(i).format(fmt);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", key);
            item.put("count", monthCount.getOrDefault(key, 0L));
            userGrowth.add(item);
        }
        result.put("userGrowth", userGrowth);

        // 文章分类分布
        Map<String, String> catNames = new LinkedHashMap<>();
        catNames.put("EXPERIENCE", "经验分享");
        catNames.put("TRAINING", "训练方法");
        catNames.put("DIET", "饮食建议");
        List<Map<String, Object>> categoryDist = new ArrayList<>();
        for (Object[] row : articleDao.countByCategory()) {
            String code = (String) row[0];
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", catNames.getOrDefault(code, code));
            item.put("value", row[1]);
            categoryDist.add(item);
        }
        result.put("categoryDistribution", categoryDist);

        result.put("recentUsers", userDao.recentUsers(5));
        return result;
    }

    public PageResult<User> users(String keyword, int page, int size) {
        return userDao.page(keyword, page, size);
    }

    public void updateStatus(Long id, int status) {
        User user = getUser(id);
        if ("ADMIN".equals(user.getRole()) && status == 0) {
            throw new BusinessException("不能禁用管理员账号");
        }
        userDao.updateStatus(id, status);
    }

    public void updateRole(Long id, String role) {
        User user = getUser(id);
        if ("ADMIN".equals(user.getRole()) && !"ADMIN".equals(role)) {
            throw new BusinessException("不能修改管理员的角色");
        }
        userDao.updateRole(id, role);
    }

    public void deleteUser(Long id) {
        User user = getUser(id);
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能删除管理员账号");
        }
        userDao.delete(id);
    }

    public PageResult<ArticleVO> articles(String status, String keyword, int page, int size) {
        PageResult<Article> p = articleDao.search(status, keyword, page, size);
        List<ArticleVO> vos = new ArrayList<>();
        for (Article a : p.getRecords()) {
            vos.add(articleService.toVO(a, false, false));
        }
        return new PageResult<>(p.getTotal(), vos);
    }

    public void audit(Long id, String status, String rejectReason) {
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new BusinessException("审核状态不正确");
        }
        articleDao.audit(id, status, rejectReason);
    }

    private User getUser(Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
