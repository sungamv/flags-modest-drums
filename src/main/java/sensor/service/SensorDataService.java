package sensor.service;

import sensor.model.SensorData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SensorDataService {
    void addMetricData(SensorData sensorData);

    Map<String, Double> querySensorData(List<Integer> sensorIds, List<String> metrics, String statistic,
                                        LocalDate startDate, LocalDate endDate);
}
