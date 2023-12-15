# flags-modest-drums

Instructions to run:

MySQL Database details can be found in application.yaml.

To create the database and table:
- CREATE DATABASE sensorData; 
- USE sensorData;
- Create database table:
  ```
  CREATE TABLE sensor_data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sensor_id INT NOT NULL,
  metric_name VARCHAR(255) NOT NULL,
  metric_value DOUBLE,
  metric_timestamp DATETIME
  );
  ```


- Insert initial data (with random metric values) into the database for tests to pass:
  ```
  INSERT INTO sensor_data (sensor_id, metric_name, metric_value, metric_timestamp)
  VALUES
  (1, 'temperature', ROUND(RAND() * 100, 1), '2023-12-05 09:00:00'),
  (1, 'humidity', ROUND(RAND() * 100, 1), '2023-12-07 11:00:00'),
  (1, 'windspeed', ROUND(RAND() * 100, 1), '2023-12-09 13:00:00'),
  (2, 'temperature', ROUND(RAND() * 100, 1), '2023-12-10 10:00:00'),
  (2, 'humidity', ROUND(RAND() * 100, 1), '2023-12-12 15:00:00'),
  (2, 'windspeed', ROUND(RAND() * 100, 1), '2023-12-14 16:00:00'),
  (3, 'temperature', ROUND(RAND() * 100, 1), '2023-12-14 16:00:00'),
  (3, 'humidity', ROUND(RAND() * 100, 1), '2023-12-15 11:00:00'),
  (3, 'windspeed', ROUND(RAND() * 100, 1), '2023-12-15 13:00:00');
  ```

Start up SensorDataApplication.java.

Example curls:

To add a new metric value for a new/existing sensor ID:
```
curl -L 'http://localhost:8080/sensor-data/add' \
  -H 'Content-Type: application/json' \
  -d '{
    "sensorId": 5,
    "metricName": "windspeed",
    "metricValue": 103,
    "metricTimestamp": "2023-12-12T12:37:01"
  }'
```
  - If a timestamp is not provided, the current date/time will be used.
  - If the sensor ID does not exist in the database, a new sensor ID will be created with the provided sensor data.


To query sensor data:
```
curl -L 'http://localhost:8080/sensor-data/query?sensorIds=1%2C2%2C3&metrics=temperature%2Chumidity&statistic=average&startDate=2023-12-10&endDate=2023-12-15'
``` 
- Query: Give me the average temperature and humidity for sensors 1, 2 and 3 in the last 5 days.
