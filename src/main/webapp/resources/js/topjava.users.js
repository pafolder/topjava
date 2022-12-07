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
                $(row).addClass($(data.enabled).is(':checked') ? "activeUser" : "inactiveUser");
            }
        })
    );
});

function onActiveStatusChanged(userId, object) {
    let isEnabled = $("#userActive" + userId).is(':checked');
    let row = $(object).closest('tr');
    $.ajax({
        url: ctx.ajaxUrl + userId + "/enabled",
        type: "POST",
        data: "isEnabled=" + isEnabled
    }).done(function () {
        $(row).removeClass(isEnabled ? 'inactiveUser' : 'activeUser');
        $(row).addClass(isEnabled ? 'activeUser' : 'inactiveUser');
        successNoty("Status changed to " + (isEnabled ? "Active" : "Disabled"));
    });
}