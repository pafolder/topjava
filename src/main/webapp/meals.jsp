<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
            border-color: black;
        }

        .excess {
            color: red;
            border-color: black;
        }

        td {
            border-color: black;
        }

        tr {
            border-color: black;
        }

        dl {
            margin: 10px;
            padding: 0;
        }

        dt {
            display: inline-block;
            margin-left: 40px;
            vertical-align: top;
        }

        dd {
            display: inline-block;
            margin-left: 0px;
            vertical-align: top;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals of User ${userId}</h2>
    <h4>Filter:</h4>
    <form method="get" action="meals">
        <dl>
            <dd>Start Date:</dd>
            <dd><input type="date" value="${requestScope.formDateTimeFilter.startDate}" name="filterStartDate" required>
            </dd>
            <dt>End Date:</dt>
            <dd><input type="date" value="${requestScope.formDateTimeFilter.endDate}" name="filterEndDate" required>
            </dd>
        </dl>
        <dl>
            <dd>Start Time:</dd>
            <dd><input type="time" value="${requestScope.formDateTimeFilter.startTime}" name="filterStartTime" required>
            </dd>
            <dt>End Time:</dt>
            <dd><input type="time" value="${requestScope.formDateTimeFilter.endTime}" name="filterEndTime" required>
            </dd>
        </dl>
        <button type="submit">Apply Filter</button>
        <button type="reset">Reset</button>
        <input type="hidden" name="action" value="filter">
    </form>
    <br>
    <a href="meals?action=create">Add Meal</a>
    <br> <br>
    <table border="1" cellpadding="8" cellspacing="0" style="border-collapse: collapse">
        <thead>
        <tr style="text-align: center">
            <td>Date</td>
            <td>Description</td>
            <td>Calories</td>
            <td></td>
            <td></td>
        </tr>
        </thead>
        <c:forEach items="${requestScope.meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td style="text-align: center">${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>