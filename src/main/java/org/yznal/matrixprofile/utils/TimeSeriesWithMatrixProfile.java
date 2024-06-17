package org.yznal.matrixprofile.utils;

import lombok.Getter;
import org.yznal.matrixprofile.vo.MetricValue;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Структура данных для хранения временного ряда метрик и расчета его матричного профиля.
 *
 * <p>Реализует логику кольцевого буфера.</p>
 *
 * @param <T> тип хранимых метрик
 */
@Getter
public class TimeSeriesWithMatrixProfile<T extends MetricValue> {

    /**
     * Длина временного ряда
     */
    private final int capacity;
    /**
     * Размер скользящего окна
     */
    private final int windowSize;
    /**
     * Значение метрик временного ряда
     */
    private final T[] timeSeries;
    /**
     * Расчетные значения матричного профиля временного ряда {@link TimeSeriesWithMatrixProfile#timeSeries}
     */
    private final double[] matrixProfile;
    /**
     * Текущее количество заполненных значений временного ряда.
     *
     * <p>Не превышает {@link TimeSeriesWithMatrixProfile#capacity}</p>
     */
    private int seriesLength;
    /**
     * Индекс элемента временного ряда, который мы считаем самым ранним в нем
     */
    private int startIndex;
    /**
     * Найденные недавно аномалии
     */
    private final Deque<DiscordInfo<T>> discords;

    /**
     * Данные об аномалии
     *
     * @param <T> тип метрики
     */
    public record DiscordInfo<T>(int index, double value, T metric) {
    }

    @SuppressWarnings("unchecked")
    public TimeSeriesWithMatrixProfile(int capacity, int windowSize) {
        this.capacity = capacity;
        this.timeSeries = (T[]) new MetricValue[capacity];
        this.matrixProfile = new double[capacity - windowSize + 1];
        Arrays.fill(matrixProfile, Double.MAX_VALUE);
        this.windowSize = windowSize;
        this.seriesLength = 0;
        this.startIndex = 0;
        this.discords = new LinkedList<>();
    }

    public void addMetric(T newMetric) {
        // Добавляем новую метрику в конец буфера
        var index = startIndex + seriesLength;
        if (index >= capacity) {
            index -= capacity;
        }
        timeSeries[index] = newMetric;

        if (isFull()) { // Если буфер полный
            startIndex++; // Сдвинем начало буфера вправо
            if (startIndex >= capacity) {
                startIndex = 0;
            }
        } else {
            seriesLength++; //Иначе увеличим текущее число хранимых метрик
        }

        if (seriesLength >= windowSize) { // Если мы уже храним достаточное количество метрик для анализа,
            updateMatrixProfile(index); // начнем плавно высчитывать матричный профиль
        }
    }

    private void updateMatrixProfile(int newMetricIndex) {
        var start = (newMetricIndex - windowSize + 1 + capacity) % capacity;

        var discordCandidate = discords.isEmpty() ? null : discords.peekLast();
        for (var i = start; i <= newMetricIndex; i++) {
            for (var j = 0; j <= seriesLength - windowSize; j++) {
                if (i != j) {
                    var distance = calculateDistance(i, j);
                    var profileIndex = (i - startIndex + capacity) % capacity - windowSize + 1;
                    matrixProfile[profileIndex] = distance;

                    if (discordCandidate == null || distance > discordCandidate.value) {
                        int actualIndex = (i + startIndex) % capacity;
                        T metric = timeSeries[actualIndex];
                        discordCandidate = new DiscordInfo<>(actualIndex, distance, metric);
                    }
                }
            }
        }

        while (discords.size() > 1 && discords.peekFirst().metric.timestamp() < timeSeries[start].timestamp()) {
            discords.removeFirst();
        }

        var lastDiscord = discords.peekLast();
        if (lastDiscord != null && lastDiscord.value < discordCandidate.value) {
            discords.addLast(discordCandidate);
        }
    }

    private double calculateDistance(int startIndex1, int startIndex2) {
        var distance = 0.0;
        for (var k = 0; k < windowSize; k++) {
            var diff = timeSeries[(startIndex1 + k) % capacity].value() -
                    timeSeries[(startIndex2 + k) % capacity].value();
            distance += diff * diff;
        }
        return Math.sqrt(distance);
    }

    public T[] getTimeSeries() {
        var result = Arrays.copyOf(timeSeries, seriesLength);
        for (var i = 0; i < seriesLength; i++) {
            var index = startIndex + i;
            if (index >= capacity) {
                index -= capacity;
            }
            result[i] = timeSeries[index];
        }
        return result;
    }

    public double[] getMatrixProfile() {
        return matrixProfile;
    }

    public boolean isFull() {
        return seriesLength == capacity;
    }


}
