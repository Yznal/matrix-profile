package org.yznal.matrixprofile.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 */
@Getter
@ConfigurationProperties(prefix = "matrix-profile")
public class MatrixProfileProperties {

    private int windowSize;

}
