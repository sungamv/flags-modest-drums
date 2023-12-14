package sensor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sensor.model.SensorData;
import sensor.service.SensorDataService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Validated
@RestController
@RequestMapping(value = "/sensor-data", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @PostMapping("/add")
    public ResponseEntity<?> addMetricData(@Valid @RequestBody SensorData sensorData) {
        if (sensorData.getMetricTimestamp().isAfter(now())) {
            return badRequest().body("Timestamp cannot be later than today's date");
        }

        sensorDataService.addMetricData(sensorData);

        return ok("Metric data added successfully.");
    }

    @GetMapping("/query")
    public ResponseEntity<?> querySensorData(
            @RequestParam List<Integer> sensorIds,
            @RequestParam List<String> metrics,
            @RequestParam String statistic,
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate endDate
    ) {
        if (startDate != null && endDate != null) {
            long dateRange = DAYS.between(startDate, endDate);

            if (startDate.isAfter(endDate)) {
                return badRequest().body("Start date must be before end date.");
            }
            if (dateRange > 31) {
                return badRequest().body("Date range must be between one day and one month.");
            }
        }
        return ok(sensorDataService.querySensorData(sensorIds, metrics, statistic, startDate, endDate));
    }
}
