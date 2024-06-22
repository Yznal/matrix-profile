package org.yznal.matrixprofile.service;

import org.yznal.matrixprofile.utils.TimeSeriesWithMatrixProfile;
import org.yznal.matrixprofile.vo.Metric;

import java.util.HashMap;
import java.util.Map;

/**
 * Отслеживает метрики и нотифицирует об аномалиях
 */
public class MetricsService {
    private final Map<String, TimeSeriesWithMatrixProfile<Metric>> registeredMetrics;
    private final Map<String, Metric> lastDiscord;
    private final AnomalyReactor anomalyReactor;

    public MetricsService(MetricsServiceProperties matrixProfileProperties,
                          AnomalyReactor anomalyReactor) {
        final var metricIds = matrixProfileProperties.getMetricIds();
        final var timeSeriesLength = matrixProfileProperties.getTimeSeriesLength();
        final var windowSize = matrixProfileProperties.getWindowSize();

        this.registeredMetrics = new HashMap<>(metricIds.size());
        this.lastDiscord = new HashMap<>(metricIds.size());
        for (var metricId : metricIds) {
            this.registeredMetrics.put(metricId,
                    new TimeSeriesWithMatrixProfile<>(
                            timeSeriesLength,
                            windowSize));
            this.lastDiscord.put(metricId, null);
        }
        this.anomalyReactor = anomalyReactor;
    }

    public void registerNewMetric(Metric metric) {
        final var metricId = metric.id();
        final var tsmp = registeredMetrics.get(metricId);
        tsmp.addMetric(metric);

        final var discord = tsmp.getDiscord();
        if (discord != lastDiscord.get(metricId)) {
            lastDiscord.put(metricId, discord);
            anomalyReactor.onDiscord(discord);
        }
    }
}
