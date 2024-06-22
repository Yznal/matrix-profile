package org.yznal.matrixprofile.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * Параметры для анализа метрик
 */
@Data
@ConfigurationProperties(prefix = "yznal.matrix.profile")
public class MatrixProfileProperties {
    /**
     * Флаг включения конфигурации по умолчанию
     */
    private boolean enabled = true;
    /**
     * ID отслеживаемых метрик
     */
    private List<String> metricIds = Collections.emptyList();
    /**
     * Количество хранимых метрик
     */
    private int timeSeriesLength = 10;
    /**
     * Размер скользящего окна для анализа
     */
    private int windowSize = 4;
}
