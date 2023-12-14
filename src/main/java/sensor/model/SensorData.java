package sensor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "sensor_data")
@NoArgsConstructor
public class SensorData {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "sensor_id", nullable = false)
    @NotNull
    private Integer sensorId;

    @Column(name = "metric_name", nullable = false)
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(temperature|humidity|windspeed)$")
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    @NotNull
    private double metricValue;

    @Column(name = "metric_timestamp")
    private LocalDateTime metricTimestamp;
}
