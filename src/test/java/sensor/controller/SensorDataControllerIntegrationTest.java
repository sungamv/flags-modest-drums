package sensor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SensorDataControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private final String PATH = "/sensor-data";

    @BeforeEach
    void setContext() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void testAddMetricData_Success() throws Exception {
        String requestBody = """
                {
                    "sensorId": 5,
                    "metricName": "temperature",
                    "metricValue": 45,
                    "metricTimestamp": "2023-12-14T12:37:01"
                }
                """;


        mockMvc.perform(post(PATH + "/add")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateMetric_InvalidPayload() throws Exception {
        String requestBody = """
                {
                    "sensorId": 1,
                    "metricName": "invalidMetric",
                    "metricValue": 45,
                    "metricTimestamp": "2023-12-14T12:37:01"
                }
                """;


        mockMvc.perform(post(PATH + "/add")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddMetricData_NoSensorIdProvided() throws Exception {
        String requestBody = """
                {
                    "sensorId": "",
                    "metricName": "temperature",
                    "metricValue": 45,
                    "metricTimestamp": "2023-12-14T12:37:01"
                }
                """;


        mockMvc.perform(post(PATH + "/add")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddMetricData_TimestampIsInTheFuture() throws Exception {
        String requestBody = """
                {
                    "sensorId": 5,
                    "metricName": "temperature",
                    "metricValue": 45,
                    "metricTimestamp": "2023-12-25T12:37:01"
                }
                """;


        mockMvc.perform(post(PATH + "/add")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Timestamp cannot be later than today's date"));
    }

    @Test
    public void testQuerySensorData_Success() throws Exception {
        List<Integer> sensorIds = asList(1, 2);
        List<String> metrics = asList("temperature", "humidity");
        String statistic = "average";
        LocalDate startDate = LocalDate.of(2023, 12, 5);
        LocalDate endDate = LocalDate.of(2023, 12, 12);

        mockMvc.perform(get(PATH + "/query")
                        .param("sensorIds", valueOf(sensorIds.get(0)), valueOf(sensorIds.get(1)))
                        .param("metrics", metrics.get(0), metrics.get(1))
                        .param("statistic", statistic)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature", is(notNullValue())))
                .andExpect(jsonPath("$.humidity", is(notNullValue())));
    }

    @Test
    public void testQuerySensorData_EndDateIsBeforeStartDate() throws Exception {
        List<Integer> sensorIds = asList(1, 2);
        List<String> metrics = asList("temperature", "humidity");
        String statistic = "average";
        LocalDate startDate = now().toLocalDate();
        LocalDate endDate = now().minusDays(7).toLocalDate();

        mockMvc.perform(get(PATH + "/query")
                        .param("sensorIds", valueOf(sensorIds.get(0)), valueOf(sensorIds.get(1)))
                        .param("metrics", metrics.get(0), metrics.get(1))
                        .param("statistic", statistic)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Start date must be before end date."));
    }

    @Test
    public void testQuerySensorData_DateRangeExceedsOneMonth() throws Exception {
        List<Integer> sensorIds = asList(1, 2);
        List<String> metrics = asList("temperature", "humidity");
        String statistic = "average";
        LocalDate startDate = now().minusDays(7).toLocalDate();
        LocalDate endDate = now().plusDays(32).toLocalDate();

        mockMvc.perform(get(PATH + "/query")
                        .param("sensorIds", valueOf(sensorIds.get(0)), valueOf(sensorIds.get(1)))
                        .param("metrics", metrics.get(0), metrics.get(1))
                        .param("statistic", statistic)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Date range must be between one day and one month."));
    }
}
