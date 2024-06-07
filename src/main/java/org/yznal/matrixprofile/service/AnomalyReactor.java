package org.yznal.matrixprofile.service;

import org.yznal.matrixprofile.vo.Metric;

public interface AnomalyReactor {
    void onDiscord(Metric metric);
}
