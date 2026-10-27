package com.fitness.service;

import com.fitness.common.BusinessException;
import com.fitness.dao.AnnouncementDao;
import com.fitness.entity.Announcement;

import java.util.List;

/**
 * 公告服务。
 */
public class AnnouncementService {

    private final AnnouncementDao dao = new AnnouncementDao();

    public List<Announcement> list() {
        return dao.listAll();
    }

    public void add(String title, String content) {
        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        dao.insert(a);
    }

    public void update(Long id, String title, String content) {
        Announcement a = dao.findById(id);
        if (a == null) {
            throw new BusinessException("公告不存在");
        }
        a.setTitle(title);
        a.setContent(content);
        dao.update(a);
    }

    public void delete(Long id) {
        if (dao.findById(id) == null) {
            throw new BusinessException("公告不存在");
        }
        dao.delete(id);
    }
}
