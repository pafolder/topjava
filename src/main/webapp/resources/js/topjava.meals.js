const mealAjaxUrl = "/ui/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctxMeal = {
    ajaxUrl: mealAjaxUrl
};

let addMealForm;
$(document).ready(function () {
    addMealForm = $('#addMealForm');
    ctxMeal.datatableApi = $('#mealsTable').DataTable({
        paging: false,
        info: true,
        "columns": [
            {
                "data": "dateTime"
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ]
    });
});

$("#addMealButton").click(function () {
    $("#addMeal").modal();
});

$(".deleteMeal").click(function () {
    if (confirm('Are you sure?')) {
        deleteMeal($(this).closest('tr').attr("id"));
    }
});

function saveMeal() {
    $.ajax({
        type: "POST",
        url: ctxMeal.ajaxUrl,
        data: $(addMealForm).serialize()
    }).done(function () {
        $("#addMeal").modal("hide");
        updateMealsTable();
        // successNoty("Saved");
    });
}

function deleteMeal(id) {
    $.ajax({
        type: "DELETE",
        url: ctxMeal.ajaxUrl + id,
    }).done(function () {
        updateMealsTable();
        // successNoty("Saved");
    });
}

function updateMealsTable() {
    $.get(ctxMeal.ajaxUrl, function (data) {
        ctxMeal.datatableApi.clear().rows.add(data).draw();
    });
}

