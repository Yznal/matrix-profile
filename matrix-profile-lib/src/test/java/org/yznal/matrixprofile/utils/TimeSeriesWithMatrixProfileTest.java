package org.yznal.matrixprofile.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yznal.matrixprofile.vo.MetricValue;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TimeSeriesWithMatrixProfileTest {
    private static final int CAPACITY = 10;
    private static final int WINDOW_SIZE = 3;

    private TimeSeriesWithMatrixProfile<SimpleMetric> timeSeries;

    @BeforeEach
    void setUp() {
        timeSeries = new TimeSeriesWithMatrixProfile<>(CAPACITY, WINDOW_SIZE);
    }

    @Test
    void testAddMetric() {
        var metric = new SimpleMetric(1.0, System.currentTimeMillis());
        timeSeries.addMetric(metric);
        assertArrayEquals(new SimpleMetric[]{metric}, timeSeries.getTimeSeries());
    }

    @Test
    void testMatrixProfileCalculation() {
        var metrics = new SimpleMetric[]{
                new SimpleMetric(1.0, 1),
                new SimpleMetric(2.0, 2),
                new SimpleMetric(3.0, 3),
                new SimpleMetric(4.0, 4),
                new SimpleMetric(5.0, 5)
        };

        for (var metric : metrics) {
            timeSeries.addMetric(metric);
        }

        var matrixProfile = timeSeries.getMatrixProfile();
        assertEquals(metrics.length - WINDOW_SIZE + 1, Arrays.stream(matrixProfile).filter(profileValue -> profileValue != Double.MAX_VALUE).count());
    }

    @Test
    void testDiscordDetection() {
        var metrics = new SimpleMetric[]{
                new SimpleMetric(1.1, 1),
                new SimpleMetric(1.2, 2),
                new SimpleMetric(0.9, 3),
                new SimpleMetric(10.0, 4),
                new SimpleMetric(1.0, 5),
                new SimpleMetric(1.11, 6),
                new SimpleMetric(0.95, 7)
        };

        for (var metric : metrics) {
            timeSeries.addMetric(metric);
        }

        var discords = timeSeries.getDiscords();
        assertFalse(discords.isEmpty());
        var discord = discords.getLast();
        assertEquals(1.0, discord.metric().value());
    }

    @Test
    void testCircularBuffer() {
        var capacity = 5;
        var windowSize = 3;
        var circularTimeSeries = new TimeSeriesWithMatrixProfile<>(capacity, windowSize);

        for (int i = 0; i < capacity + 2; i++) {
            circularTimeSeries.addMetric(new SimpleMetric(i, i));
        }

        var timeSeries = circularTimeSeries.getTimeSeries();
        assertEquals(capacity, timeSeries.length);
        assertEquals(2, timeSeries[0].value());
    }

    @Test
    void testDiscordUpdate() {
        var metrics = new SimpleMetric[]{
                new SimpleMetric(1.0, 1),
                new SimpleMetric(2.0, 2),
                new SimpleMetric(5.0, 3),
                new SimpleMetric(4.0, 4),
                new SimpleMetric(0.5, 5)
        };

        for (var metric : metrics) {
            timeSeries.addMetric(metric);
        }

        var discords = timeSeries.getDiscords();
        assertFalse(discords.isEmpty());

        var newMetric = new SimpleMetric(6.0, 6);
        timeSeries.addMetric(newMetric);
        discords = timeSeries.getDiscords();

        assertFalse(discords.isEmpty());
        var discord = discords.getLast();
        assertEquals(newMetric, discord.metric());
    }

    public static class SimpleMetric implements MetricValue {
        private final double value;
        private final long timestamp;

        public SimpleMetric(double value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        @Override
        public double value() {
            return value;
        }

        @Override
        public long timestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "SimpleMetric{" +
                    "value=" + value +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}