package sensor.repository;

import sensor.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE SensorData s " +
            "SET s.metricValue = :metricValue, " +
            "s.metricTimestamp = :metricTimestamp " +
            "WHERE s.sensorId = :sensorId " +
            "AND s.metricName = :metricName")
    void upsertMetricData(@Param("sensorId") Integer sensorId,
                          @Param("metricName") String metricName,
                          @Param("metricValue") double metricValue,
                          @Param("metricTimestamp") LocalDateTime metricTimestamp);

    @Query("SELECT s FROM SensorData s " +
            "WHERE s.sensorId = :sensorId " +
            "AND s.metricName = :metricName")
    Optional<SensorData> findBySensorIdAndMetric(@Param("sensorId") Integer sensorId,
                                                 @Param("metricName") String metricName);

    @Query("SELECT s FROM SensorData s " +
            "WHERE s.sensorId IN :sensorIds " +
            "AND s.metricName IN :metricNames " +
            "AND (:startTime IS NULL OR s.metricTimestamp >= :startTime) " +
            "AND (:endTime IS NULL OR s.metricTimestamp <= :endTime) " +
            "ORDER BY s.metricTimestamp DESC")
    List<SensorData> findBySensorIdAndMetricNameAndTimestampBetween(
            @Param("sensorIds") List<Integer> sensorIds,
            @Param("metricNames") List<String> metricNames,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}

