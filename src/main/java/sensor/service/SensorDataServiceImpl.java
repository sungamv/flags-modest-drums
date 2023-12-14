package sensor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sensor.model.SensorData;
import sensor.repository.SensorDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static java.time.LocalTime.MAX;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl implements SensorDataService {
    private final SensorDataRepository repository;

    @Override
    public void addMetricData(SensorData sensorData) {
        Optional<SensorData> sensorId = repository.findBySensorIdAndMetric(sensorData.getSensorId(),
                sensorData.getMetricName());

        sensorData.setMetricTimestamp(sensorData.getMetricTimestamp() == null ? now() : sensorData.getMetricTimestamp());

        if (sensorId.isPresent()) {
            repository.upsertMetricData(sensorData.getSensorId(), sensorData.getMetricName(), sensorData.getMetricValue(),
                    sensorData.getMetricTimestamp()
            );
        } else {
            repository.save(sensorData);
        }
    }

    @Override
    public Map<String, Double> querySensorData(List<Integer> sensorIds, List<String> metrics, String statistic, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(MAX);
        }

        List<SensorData> sensorDataList = repository.findBySensorIdAndMetricNameAndTimestampBetween(sensorIds, metrics, startDateTime,
                endDateTime);

        return calculateStatistic(statistic, sensorDataList);
    }

    private Map<String, Double> calculateStatistic(String statistic, List<SensorData> sensorData) {
        Map<String, Double> results = new HashMap<>();

        Map<String, List<Double>> metricValues = sensorData.stream()
                .collect(groupingBy(SensorData::getMetricName,
                        mapping(SensorData::getMetricValue, toList())));

        metricValues.forEach((metric, values) -> {
            Double calculatedValue;
            switch (statistic) {
                case "min":
                    calculatedValue = Collections.min(values);
                    break;
                case "max":
                    calculatedValue = Collections.max(values);
                    break;
                case "sum":
                    calculatedValue = values.stream().mapToDouble(Double::doubleValue).sum();
                    break;
                case "average":
                default:
                    calculatedValue = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    calculatedValue = round(calculatedValue * 100.0) / 100.0;
                    break;
            }
            results.put(metric, calculatedValue);
        });
        return results;
    }
}
