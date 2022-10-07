<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.javawebinar.topjava.model.MealTo" %><%--
  Created by IntelliJ IDEA.
  User: SP
  Date: 07.10.2022
  Time: 14:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<%
    List <MealTo> mealsTo = (List<MealTo>) request.getAttribute("mealsTo");
%>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<table>
    <tr>
        <th>Date</th>
        <th>Time</th>
        <th>Calories</th>
        <th style="text-align: left">Description</th>
    </tr>

   <c:forEach items="${mealsTo}" var="mealTo" varStatus="status">
    <tr>
        <c:set var="mealColor" value="red"/>
        <c:if test="${mealTo.excess}">
        <c:set var="mealColor" value="green"/>
        </c:if>

        <td style="color:${mealColor}; text-align: center;">${mealTo.date}&nbsp; </td>
        <td style="color:${mealColor}; text-align: center;">${mealTo.time}</>
        <td style="color:${mealColor}; text-align: center;">${mealTo.calories}</>
        <td style="color:${mealColor}; text-align: left;">${mealTo.description}</>
    </tr>
    </c:forEach>

</table>

</body>
</html>
