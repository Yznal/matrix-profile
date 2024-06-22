package org.yznal.matrixprofile.service;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Параметры для анализа метрик
 */
@Getter
@Builder
public final class MetricsServiceProperties {
    /**
     * ID отслеживаемых метрик
     */
    @Builder.Default
    private List<String> metricIds = Collections.emptyList();
    /**
     * Количество хранимых метрик
     */
    @Builder.Default
    private int timeSeriesLength = 10;
    /**
     * Размер скользящего окна для анализа
     */
    @Builder.Default
    private int windowSize = 4;
}
