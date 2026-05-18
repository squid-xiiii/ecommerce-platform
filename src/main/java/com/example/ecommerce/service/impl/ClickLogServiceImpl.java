package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.ClickLog;
import com.example.ecommerce.repository.ClickLogRepository;
import com.example.ecommerce.service.ClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ClickLogServiceImpl implements ClickLogService {

    @Autowired
    private ClickLogRepository clickLogRepository;

    @Override
    public ClickLog saveClickLog(ClickLog clickLog) {
        if (clickLog.getClickTime() == null) {
            clickLog.setClickTime(new Date());
        }
        return clickLogRepository.save(clickLog);
    }

    @Override
    public long countByPageCode(String pageCode) {
        return clickLogRepository.countByPageCode(pageCode);
    }

    @Override
    public List<ClickLog> getRecentClicksByPage(String pageCode, int limit) {
        List<ClickLog> all = clickLogRepository.findByPageCodeOrderByClickTimeDesc(pageCode);
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }
}