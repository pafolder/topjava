<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<style>
    table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
        padding: 3px;
    }

    .small {
        font-size: small;
        background: lavenderblush;
    }
</style>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<a href="meals?action=new">&nbsp;New Meal&nbsp;</a>
<p></p>
<table>
    <tr class="small">
        <th style="width: 20ch;">Date Time</th>
        <th style="width: 9ch;">Calories</th>
        <th style="width: 30ch;">Description</th>
        <th style="width: 7ch;"></th>
        <th style="width: 8ch;"></th>
    </tr>

    <c:forEach items="${mealsTo}" var="mealTo">
        <tr style="color:${mealTo.excess? "red": "green"}; text-align: center;">
            <td>${mealTo.date}&nbsp;${mealTo.time}</td>
            <td>${mealTo.calories}</td>
            <td style="text-align: left;">&nbsp;${mealTo.description}</td>
            <td><a href="meals?action=edit&mealId=<c:out value="${mealTo.id}"/>">Edit</a></td>
            <td><a href="meals?action=delete&mealId=<c:out value="${mealTo.id}"/>">Delete</a></td>
        </tr>
    </c:forEach>

</table>

</body>
</html>
