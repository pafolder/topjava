REST API Test Examples
=====
___
Create new meal:
~~~
curl -i -X POST -H "Content-Type: application/json;charset=UTF-8" -d {\"id\":null,\"dateTime\":\"2011-03-13T20:00:05\",\"description\":\"Pусский\u0020текст\",\"calories\":510} http://localhost:8080/rest/meals
~~~
___
Delete Meal with id=100017
~~~
curl -i -X DELETE http://localhost:8080/rest/meals/100017
~~~
___
Get all meals:
~~~
curl -i http://localhost:8080/rest/meals
~~~
___
Get all meals filtered:
~~~
curl -i "http://localhost:8080/rest/meals/filter?startDate=2020-01-30&endDate=2020-01-30&startTime=00:00&endTime=20:00"
~~~
___
Update meal with id=100003:
~~~
curl -i -X PUT -H "Content-Type: application/json;charset=UTF-8" -d {\"id\":100003,\"dateTime\":\"2011-03-13T20:00:05\",\"description\":\"Обновлённая\u0020еда\",\"calories\":510} http://localhost:8080/rest/meals/100003
~~~

