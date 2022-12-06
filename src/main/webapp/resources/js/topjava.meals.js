const mealAjaxUrl = "ui/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl
};

$(document).ready(function () {
    form = $('#addMealForm');
    makeEditable(
        $("#datatable").DataTable({
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
            ],
            "columnDefs": [{type: 'date', 'targets': [0]}],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
        })
    );
});

$("#filterButton").click(function () {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "filter?" + $("[name='filterForm']").serialize(),
    }).done(function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
});

$("#resetFilterButton").click(function () {
    $("[name='filterForm']").trigger("reset");
    updateTable();
});