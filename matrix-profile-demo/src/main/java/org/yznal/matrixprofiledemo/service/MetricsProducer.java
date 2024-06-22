package org.yznal.matrixprofiledemo.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yznal.matrixprofile.service.MetricsService;
import org.yznal.matrixprofile.vo.Metric;

import java.util.Random;

@Slf4j
@Service
public class MetricsProducer {
    private static final int METRICS_TO_PRODUCE = 500;

    private final Random random;
    private final MetricsService metricsService;
    private int parsedMetricsCounter;
    private final int meanMetricValue;

    @SneakyThrows
    public MetricsProducer(MetricsService metricsService) {
        this.random = new Random(42);
        this.metricsService = metricsService;
        this.parsedMetricsCounter = 0;
        this.meanMetricValue = random.nextInt(1000, 3000);
    }

    @Scheduled(fixedRate = 10)
    @SneakyThrows
    public void readMetric() {
        if (parsedMetricsCounter < METRICS_TO_PRODUCE) {
            final var metric = new Metric(getMetricId(),
                    getMetricValue(),
                    System.currentTimeMillis());
            log.info("Produced #{} metric: {}", ++parsedMetricsCounter, metric);
            metricsService.registerNewMetric(metric);
        } else {
            log.info("Parsed {} metrics. Closing application", parsedMetricsCounter);
            System.exit(0);
        }
    }

    private String getMetricId() {
        return "A";
    }

    private Double getMetricValue() {
        final var offset = random.nextDouble() > 0.05
                ? random.nextInt(200) + random.nextDouble()
                : random.nextInt(2000) + random.nextDouble();
        final var direction = random.nextDouble() > 0.5 ? 1 : -1;
        return meanMetricValue + offset * direction;
    }
}
