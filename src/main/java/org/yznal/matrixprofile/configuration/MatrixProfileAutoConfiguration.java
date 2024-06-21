package org.yznal.matrixprofile.configuration;

import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.yznal.matrixprofile.service.AnomalyReactor;
import org.yznal.matrixprofile.service.MetricsService;

@AutoConfiguration
@ConditionalOnClass(MetricsService.class)
@EnableConfigurationProperties(MatrixProfileProperties.class)
public class MatrixProfileAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AnomalyReactor anomalyReactor() {
        throw new BeanCreationNotAllowedException("anomalyReactor",
                "Необходимо реализовать интерфейс AnomalyReactor в явном виде");
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricsService metricsService(MatrixProfileProperties matrixProfileProperties, AnomalyReactor anomalyReactor) {
        return new MetricsService(matrixProfileProperties, anomalyReactor);
    }

}
