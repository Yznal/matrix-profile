package org.yznal.matrixprofile.repository.impl;

import org.yznal.matrixprofile.repository.MetricRepository;
import org.yznal.matrixprofile.vo.Metric;
import org.yznal.matrixprofile.vo.MetricValue;

import java.util.ArrayList;
import java.util.Collection;

public class RamMetricRepository implements MetricRepository<RamMetricRepository.MetricEntry> {

    private final int maxSize;
    private final MetricEntry[] buffer;
    private int index;
    private int size;

    public RamMetricRepository(int maxSize) {
        this.maxSize = maxSize;
        buffer = new MetricEntry[maxSize];
        for (int i = 0; i < maxSize; i++) {
            buffer[i] = new MetricEntry();
        }
        index = 0;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    public void add(Metric metric) {
        final var bufferMetric = buffer[index];
        bufferMetric.value = metric.value();
        bufferMetric.timestamp = metric.timestamp();

        ++index;
        if (index < maxSize) {
            ++size;
        } else {
            index = index - maxSize;
        }
    }

    @Override
    public Collection<MetricEntry> scan() {
        final var scan = new ArrayList<MetricEntry>(size);
        final var currentIndex = index;
        for (int i = 0; i < size; i++) {
            var bufferIndex = currentIndex + i;
            if (bufferIndex >= size) {
                bufferIndex = bufferIndex - size;
            }
            scan.set(i, buffer[bufferIndex]);
        }

        return scan;
    }

    public static class MetricEntry implements MetricValue {
        private double value;
        private long timestamp;

        @Override
        public double value() {
            return value;
        }

        @Override
        public long timestamp() {
            return timestamp;
        }
    }

}