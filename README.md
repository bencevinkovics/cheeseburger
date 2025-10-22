# Using the Weather API

Database contains hourly data of temp, wind_speed, humidity and rain for 3 different sensors (s1,s2,s3) dated between 2025 August through September.

### Setting Up Database

cd database

docker-compose up -d

### Example Queries
To get all data from the database at once:

/weatherData/all

To get specific data:

/weatherData?statistic=avg&metrics=temp,rain,wind_speed,humidity&sensors=s1,s2&start=2025-08-01T00:00:00&end=2025-08-10T23:59:59

* statictic - can be min,max or avg, if not provided defaults to avg
* metrics - can be temp, wind_speed, humidity and rain (at least one needed)
* sensors - can be one or more, if not provided defaults to all sensors (available s1,s2,s3)
* date - both start and end has to be provided (default date and max date period yet to be implemented)

