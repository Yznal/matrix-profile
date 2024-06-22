package org.yznal.matrixprofile.service;

import org.yznal.matrixprofile.vo.Metric;

/**
 * Реактор на аномалии
 */
public interface AnomalyReactor {
    /**
     * Обработчик метрики с аномальным значением относительно профиля
     *
     * @param metric аномальная метрика
     */
    void onDiscord(Metric metric);
}
