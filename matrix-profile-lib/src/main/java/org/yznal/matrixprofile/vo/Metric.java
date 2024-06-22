package org.yznal.matrixprofile.vo;

/**
 * Метрика
 *
 * @param id        ID метрики
 * @param value     значение метрики
 * @param timestamp время возникновения
 */
public record Metric(String id, double value, long timestamp) implements MetricValue {
}
