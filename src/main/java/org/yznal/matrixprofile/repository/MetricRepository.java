package org.yznal.matrixprofile.repository;

import org.yznal.matrixprofile.vo.Metric;
import org.yznal.matrixprofile.vo.MetricValue;

import java.util.Collection;

/**
 * DAO class for {@link Metric} values
 */
public interface MetricRepository<T extends MetricValue> {

    /**
     * @return number of stored {@link Metric} values
     */
    int size();

    /**
     * @return iterable collection of stored {@link Metric} values of {@link MetricRepository#size()} length.
     */
    Collection<T> scan();

}
