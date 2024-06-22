package org.yznal.matrixprofile.utils;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
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
    @Getter(AccessLevel.PROTECTED)
    private final double[] matrixProfile;
    /**
     * Текущее количество заполненных значений временного ряда.
     *
     * <p>Не превышает {@link TimeSeriesWithMatrixProfile#capacity}</p>
     */
    private int seriesLength;
    /**
     * Текущий индекс элемента временного ряда, который мы считаем самым ранним в нем
     */
    private int startIndex;
    /**
     * Найденные недавно аномалии
     */
    private final Deque<DiscordInfo<T>> discords;

    /**
     * Данные об аномалии
     *
     * @param <T>    тип метрики
     * @param index  индекс аномалии в матричном профиле
     * @param value  значение матричного профиля
     * @param metric метрика, соответствующая аномалии
     */
    public record DiscordInfo<T>(int index, double value, T metric) {
        // empty record
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

    /**
     * Добавляет новую метрику и пересчитывает матричный профиль при необходимости
     *
     * @param newMetric новая метрика к добавлению
     */
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
            seriesLength++; // иначе увеличим текущее число хранимых метрик
        }

        if (seriesLength > windowSize) { // Если мы уже храним достаточное количество метрик для анализа,
            updateMatrixProfile(index); // начнем плавно высчитывать матричный профиль
        }
    }

    private void updateMatrixProfile(int newMetricIndex) {
        var discordCandidate = discords.isEmpty() ? null : discords.peekLast();

        final var traversingLength = Math.min(seriesLength - windowSize + 1, matrixProfile.length);
        for (var i = 0; i < traversingLength; i++) {
            final var index = (startIndex + i) % capacity;
            for (var j = 0; j < traversingLength; j++) {
                final var slidingWindowIndex = (startIndex + j) % capacity;
                if (index == slidingWindowIndex) {
                    continue;
                }

                var distance = calculateDistance(index, slidingWindowIndex);
                if (matrixProfile[i] > distance) {
                    matrixProfile[i] = distance;
                }

                if (discordCandidate == null || distance > discordCandidate.value) {
                    int actualIndex = (i + startIndex) % capacity;
                    T metric = timeSeries[newMetricIndex];
                    discordCandidate = new DiscordInfo<>(actualIndex, distance, metric);
                }

            }
        }

        while (discords.size() > 1 && discords.peekFirst().metric.timestamp() < timeSeries[startIndex].timestamp()) {
            discords.removeFirst();
        }

        var lastDiscord = discords.peekLast();
        if (lastDiscord == null || lastDiscord.value() < discordCandidate.value()) {
            discords.addLast(discordCandidate);
        }
    }

    private double calculateDistance(int startIndex1, int startIndex2) {
        var distance = 0.0;
        for (var k = 0; k < windowSize; k++) {
            final var index1 = (startIndex1 + k) % capacity;
            final var index2 = (startIndex2 + k) % capacity;
            final var diff = index1 < seriesLength && index2 < seriesLength
                    ? timeSeries[index1].value() - timeSeries[index2].value()
                    : 0;
            distance += diff * diff;
        }
        return Math.sqrt(distance);
    }

    /**
     * @return snapshot значений временного ряда метрик
     */
    protected T[] getTimeSeries() {
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

    /**
     * Возвращает признак заполненности буфера.
     *
     * @return {@code true},
     * если {@link TimeSeriesWithMatrixProfile#seriesLength} == {@link TimeSeriesWithMatrixProfile#capacity};
     * иначе {@code false}
     */
    protected boolean isFull() {
        return seriesLength == capacity;
    }

    /**
     * Возвращает последняя обнаруженная аномалия, если такая есть.
     *
     * @return последняя обнаруженная аномалия, если такая есть
     */
    @Nullable
    public T getDiscord() {
        final var lastDiscord = discords.peekLast();
        return lastDiscord != null
                ? lastDiscord.metric
                : null;
    }
}
