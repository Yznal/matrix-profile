package org.yznal.matrixprofile.service;

import org.yznal.matrixprofile.repository.MetricRepository;
import org.yznal.matrixprofile.vo.MetricValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 */
public class MetricsService {
    private final Map<String, MetricRepository<MetricValue>> registeredMetrics;

    public MetricsService(Collection<String> metricIds,
                          Supplier<MetricRepository<MetricValue>> metricRepositorySupplier) {
        registeredMetrics = new HashMap<>(metricIds.size());
        for (var metricId : metricIds) {
            registeredMetrics.put(metricId, metricRepositorySupplier.get());
        }
    }

    public double[] getMetrics(String metricId) {
        final var repository = registeredMetrics.get(metricId);
        final var metrics = new double[repository.size()];

        var scanIterator = repository.scan().iterator();
        var i = 0;
        while (scanIterator.hasNext()) {
            metrics[i] = scanIterator.next().value();
        }

        return metrics;
    }
}
