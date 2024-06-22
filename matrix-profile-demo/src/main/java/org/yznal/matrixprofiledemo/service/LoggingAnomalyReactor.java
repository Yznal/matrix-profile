package org.yznal.matrixprofiledemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yznal.matrixprofile.service.AnomalyReactor;
import org.yznal.matrixprofile.vo.Metric;

@Slf4j
@Service
public class LoggingAnomalyReactor implements AnomalyReactor {
    @Override
    public void onDiscord(Metric metric) {
        log.info("Found discord metric: {}", metric);
    }
}
