package com.analyticore.analyseservice.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/** Expone el estado interno del Servicio Java para el panel /. */
@Service
public class DashboardService {

    public Map<String, Object> getStatus() {
        return ActivityTracker.getStatus();
    }
}
