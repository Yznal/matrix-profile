package org.yznal.matrixprofile.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Параметры для анализа метрик
 */
@Getter
@ConfigurationProperties(prefix = "matrix-profile")
public class MatrixProfileProperties {
    /**
     * Количество хранимых метрик
     */
    private int egressMetricsLength;
    /**
     * Размер скользящего окна для анализа
     */
    private int windowSize;
}
