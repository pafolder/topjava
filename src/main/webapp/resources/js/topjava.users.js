const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
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
            "order": [
                [
                    0,
                    "asc"
                ]
            ],
            createdRow: function (row, data, dataIndex) {
                if (!$(data.enabled).is(':checked')) {
                    $(row).css("color", "lightgray");
                } else {
                    $(row).css("color", "black");
                }
            }
        })
    );
});

function onActiveStatusChanged(userId) {
    $.ajax({
        url: ctx.ajaxUrl + userId + "/enabled",
        type: "POST",
        data: "isEnabled=" + $("#userActive" + userId).is(':checked')
    }).done(function () {
    location.reload();
        successNoty("Active status changed");
    });
}