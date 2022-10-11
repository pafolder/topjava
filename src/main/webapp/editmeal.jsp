<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="ru">
<head>
    <title>Users</title>
    <script>
        window.onload = function () {
            document.getElementById("number-input").addEventListener('keypress', e => {
                if (e.key >= '0' && e.key <= '9') // top key
                    return true;
                if (e.preventDefault) {
                    e.preventDefault(); // Cancel event
                }
            });
        }
    </script>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>${isUpdate? "Edit": "New"} meal</h2>

<form action="meals" method="POST">
    <input type="hidden" name="mealId" value="${meal.id}">
    <table>
        <tr>
            <td>
                <label for="1">Date Time:</label>
            </td>
            <td>
                <input type="datetime-local" id="1" name="dateTime" value=${meal.dateTime}>
            </td>
        </tr>
        <tr>
            <td>
                <label for="number-input">Calories:</label>
            </td>
            <td>
                <input type="number" id="number-input" size="5ch;" name="calories"
                       value="${meal.calories}"/>
            </td>
        </tr>
        <tr>
            <td>
                <label style="size: 30ch" for="3">Description:</label>
            </td>
            <td>
                <input type="text" size="50ch" name="description" id="3" value="${meal.description}">
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" name="SubmitButton" value="Submit"/>
                <input type="button" onclick="location.replace('meals')" value="Cancel"/>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
