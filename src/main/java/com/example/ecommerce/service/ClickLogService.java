package com.example.ecommerce.service;

import com.example.ecommerce.entity.ClickLog;
import java.util.List;

public interface ClickLogService {
    ClickLog saveClickLog(ClickLog clickLog);
    long countByPageCode(String pageCode);
    List<ClickLog> getRecentClicksByPage(String pageCode, int limit);
}