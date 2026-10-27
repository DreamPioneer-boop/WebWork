package com.fitness.listener;

import com.fitness.dao.ArticleDao;
import com.fitness.dao.UserDao;
import com.fitness.entity.Article;
import com.fitness.entity.User;
import com.fitness.util.DBUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 应用启动初始化：自动建表（IF NOT EXISTS）+ 首次启动写入演示数据。
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final String[] DDL = {
            "CREATE TABLE IF NOT EXISTS t_user (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, username VARCHAR(50) NOT NULL, password VARCHAR(100) NOT NULL, " +
                    "nickname VARCHAR(50) NOT NULL, avatar VARCHAR(255) DEFAULT NULL, gender VARCHAR(10) DEFAULT NULL, " +
                    "age INT DEFAULT NULL, height DECIMAL(6,2) DEFAULT NULL, weight DECIMAL(6,2) DEFAULT NULL, " +
                    "experience_level VARCHAR(20) DEFAULT 'BEGINNER', role VARCHAR(20) DEFAULT 'USER', status INT DEFAULT 1, " +
                    "create_time DATETIME DEFAULT NULL, update_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id), UNIQUE KEY uk_username (username)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_health_record (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, weight DECIMAL(6,2) DEFAULT NULL, " +
                    "height DECIMAL(6,2) DEFAULT NULL, bmi DECIMAL(6,2) DEFAULT NULL, body_fat_rate DECIMAL(6,2) DEFAULT NULL, " +
                    "muscle_mass DECIMAL(6,2) DEFAULT NULL, systolic_pressure INT DEFAULT NULL, diastolic_pressure INT DEFAULT NULL, " +
                    "heart_rate INT DEFAULT NULL, blood_sugar DECIMAL(6,2) DEFAULT NULL, sleep_hours DECIMAL(4,1) DEFAULT NULL, " +
                    "record_date DATE NOT NULL, note VARCHAR(500) DEFAULT NULL, create_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id), KEY idx_health_user (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_fitness_record (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, exercise_type VARCHAR(50) NOT NULL, " +
                    "duration INT NOT NULL, calories INT DEFAULT NULL, intensity VARCHAR(20) DEFAULT NULL, " +
                    "record_date DATE NOT NULL, note VARCHAR(500) DEFAULT NULL, create_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id), KEY idx_fitness_user (user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_article (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, user_id BIGINT NOT NULL, title VARCHAR(100) NOT NULL, " +
                    "category VARCHAR(20) NOT NULL, content TEXT NOT NULL, cover VARCHAR(255) DEFAULT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', reject_reason VARCHAR(255) DEFAULT NULL, view_count INT DEFAULT 0, " +
                    "like_count INT DEFAULT 0, create_time DATETIME DEFAULT NULL, update_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id), KEY idx_article_user (user_id), KEY idx_article_status (status)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_comment (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, article_id BIGINT NOT NULL, user_id BIGINT NOT NULL, " +
                    "content VARCHAR(500) NOT NULL, create_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id), KEY idx_comment_article (article_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_article_like (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, article_id BIGINT NOT NULL, user_id BIGINT NOT NULL, " +
                    "create_time DATETIME DEFAULT NULL, PRIMARY KEY (id), " +
                    "UNIQUE KEY uk_article_user (article_id, user_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS t_announcement (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT, title VARCHAR(100) NOT NULL, content TEXT NOT NULL, " +
                    "create_time DATETIME DEFAULT NULL, update_time DATETIME DEFAULT NULL, " +
                    "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
    };

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DBUtil.getConnection()) {
            for (String ddl : DDL) {
                try (PreparedStatement ps = conn.prepareStatement(ddl)) {
                    ps.execute();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化数据表失败", e);
        }
        seedIfEmpty();
    }

    /** 首次启动（用户表为空）时写入演示账号与示例文章 */
    private void seedIfEmpty() {
        UserDao userDao = new UserDao();
        if (userDao.countAll() > 0) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        admin.setNickname("系统管理员");
        admin.setGender("男");
        admin.setAge(30);
        admin.setExperienceLevel("EXPERIENCED");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userDao.insert(admin);

        User demo = new User();
        demo.setUsername("demo");
        demo.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        demo.setNickname("健身达人小强");
        demo.setGender("男");
        demo.setAge(28);
        demo.setExperienceLevel("EXPERIENCED");
        demo.setRole("USER");
        demo.setStatus(1);
        userDao.insert(demo);

        User beginner = new User();
        beginner.setUsername("beginner");
        beginner.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        beginner.setNickname("健身小白");
        beginner.setGender("女");
        beginner.setAge(22);
        beginner.setExperienceLevel("BEGINNER");
        beginner.setRole("USER");
        beginner.setStatus(1);
        userDao.insert(beginner);

        ArticleDao articleDao = new ArticleDao();
        saveArticle(articleDao, demo.getId(), "初学者三个月增肌训练计划", "TRAINING",
                "对于刚接触力量训练的新手，建议每周训练 3-4 次，采用全身分化或上下肢分化的方式。" +
                        "重点掌握深蹲、卧推、硬拉、划船、推举这五大基础动作，先用轻重量打磨动作质量。" +
                        "每个动作做 3-4 组，每组 8-12 次，组间休息 60-90 秒。坚持三个月，配合充足的蛋白质摄入和睡眠，肌肉量会有明显提升。",
                "APPROVED");
        saveArticle(articleDao, demo.getId(), "减脂期的一日三餐饮食安排", "DIET",
                "减脂的核心是热量缺口，但要保证营养均衡。早餐建议：燕麦 + 鸡蛋 + 牛奶；" +
                        "午餐：一拳主食 + 一掌蛋白质（鸡胸肉/鱼虾）+ 两拳蔬菜；晚餐：少量主食 + 蛋白质 + 大量蔬菜。" +
                        "避免含糖饮料和油炸食品，每天饮水 2000ml 以上。减脂速度控制在每周 0.5-1 公斤为宜，过快容易反弹。",
                "APPROVED");
        saveArticle(articleDao, demo.getId(), "新手健身最常见的五个误区", "EXPERIENCE",
                "误区一：只做有氧不练力量，容易掉肌肉导致代谢下降。误区二：认为出汗多就是减脂效果好。" +
                        "误区三：练完不拉伸，容易堆积疲劳和受伤。误区四：天天训练不休息，肌肉在休息时生长。" +
                        "误区五：只看体重不看体脂，体重下降不代表变瘦。避开这些坑，你的健身之路会顺畅很多。",
                "PENDING");
    }

    private void saveArticle(ArticleDao dao, Long userId, String title, String category, String content, String status) {
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(title);
        article.setCategory(category);
        article.setContent(content);
        article.setStatus(status);
        article.setViewCount(0);
        article.setLikeCount(0);
        dao.insert(article);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
