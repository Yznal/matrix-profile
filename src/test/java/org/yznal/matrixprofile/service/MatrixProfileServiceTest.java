package org.yznal.matrixprofile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.yznal.matrixprofile.configuration.MatrixProfileProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@MockitoSettings
class MatrixProfileServiceTest {

    @InjectMocks
    MatrixProfileService matrixProfileService;

    @Mock
    MatrixProfileProperties matrixProfileProperties;

    @BeforeEach
    void setUp() {
        when(matrixProfileProperties.getWindowSize()).thenReturn(3);
    }

    @Test
    void detectAnomalies() {
        final var metrics = new double[]{1.0, 2.0, 1.5, 1.0, 14.0, 2.0, 1.0, 1.2, 1.0, 1.1};
        final var anomalies = matrixProfileService.detectAnomalies(metrics);
        assertFalse(anomalies.isEmpty());
    }

    @Test
    void detectAnomaliesLoHi() {
        final var metrics = new double[]{1.0, 2.0, 1.5, 1.0, 14.0, 2.0, 1.0, 1.2, 1.0, 1.1};
        final var lowest = 0;
        final var highest = 3;
        for (int i = 0; i < metrics.length; i++) {
            metrics[i] = (metrics[i] - lowest) / highest;
        }
        final var anomalies = matrixProfileService.detectAnomalies(metrics);
        assertFalse(anomalies.isEmpty());
    }

    @Test
    void detectAnomaliesTarget() {
        final var metrics = new double[]{1.0, 2.0, 1.5, 1.0, 14.0, 2.0, 1.0, 1.2, 1.0, 1.1};
        final var target = 3;
        for (int i = 0; i < metrics.length; i++) {
            metrics[i] = metrics[i] / target;
        }
        final var anomalies = matrixProfileService.detectAnomalies(metrics);
        assertFalse(anomalies.isEmpty());
    }
}