### MealRestController Tests 
#### Examples:
curl -v -L "http://localhost:8080/rest/meals/filter?startDate=2020-01-30&startTime=00:00:00&endDate=2020-01-30&startTime=00:00:00&endTime=23:00:00"

curl -v -L "http://localhost:8080/rest/meals/filter?startDate=2020-01-31&startTime=null&endDate=2020-01-31&startTime=null&endTime=null"

curl -v -L "http://localhost:8080/rest/meals/filter?startDate=null&startTime=null&endDate=null&startTime=null&endTime=null"


