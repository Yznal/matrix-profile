package org.yznal.matrixprofile.service;

import io.github.ensozos.core.MatrixProfile;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.yznal.matrixprofile.configuration.MatrixProfileProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MikahilShad
 */
public class MatrixProfileService {

    private final MatrixProfile matrixProfile;
    private final MatrixProfileProperties properties;

    public MatrixProfileService(MatrixProfileProperties properties) {
        matrixProfile = new MatrixProfile();
        this.properties = properties;
    }

    List<Integer> detectAnomalies(double[] metricValues) {
        final INDArray values = Nd4j.create(metricValues, new int[]{1, metricValues.length});
        final var result = matrixProfile.stmp(values, properties.getWindowSize());
        final var matrixProfile = result.getKey();
        final var profileIndex = result.getValue();

        double mean = matrixProfile.meanNumber().doubleValue();
        double std = matrixProfile.stdNumber().doubleValue();
        double threshold = mean + 2 * std;

        List<Integer> anomalies = new ArrayList<>();
        for (int i = 0; i < matrixProfile.columns(); i++) {
            if (matrixProfile.getDouble(i) > threshold) {
                anomalies.add(i);
            }
        }

        return anomalies;
    }

}
