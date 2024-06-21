package org.yznal.matrixprofile.vo;

/**
 * Метрика
 */
public record Metric(String id, double value, long timestamp) implements MetricValue {
}
