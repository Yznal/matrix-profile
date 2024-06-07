package org.yznal.matrixprofile.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(MatrixProfileProperties.class)
@RequiredArgsConstructor
public class MatrixProfileAutoConfiguration {

    private final MatrixProfileProperties properties;

}
