$(document).ready(function () {

     $(".custom-file-input").change(function (e) {

        $('.custom-file-label').text(e.target.files[0].name);

     });

    $("#confirm").dialog({
        resizable: false,
        height:190,
        autoOpen: false,
        width: 330,
        modal: true,
        buttons: {
            Yes: function() {
                window.location = "/adm/delete/" + $(this).data("id_remove") ;
            },
            No: function() {
                $(this).dialog("close");
            }
        }
    });

    $("#personphoto").click(function (e) {

        var url = "https://www.google.pl/searchbyimage?image_url=" + $(this).data("googleurl")
        console.log(url);
        window.open(url,"_blank");

    })


    $("td > a").click(function () {

        $('#confirm').data('id_remove',$(this).data('id') ).dialog('open');
    })


});
