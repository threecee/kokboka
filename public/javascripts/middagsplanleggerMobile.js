$(document).ready(function () {


    $("li.task-item").click(function () {
        addRecipe($(this).attr("id"), $("#activeRecipeId").text());
        $(this).find(".title").html($("#activeRecipeTitle").text());
        $("#activeRecipeId").detach();
        $("#activeRecipeTitle").detach();

    });


    //select all the a tag with name equal to modal
    $('.recipeListItem').click(function (e) {
        //Cancel the link behavior
        e.preventDefault();

        //Get the A tag
        var id = $(this).find(".recipeItem").attr('id');
        var title = $(this).find(".recipeTitle").text();

        var recipeIdDiv = $('<div />');
        recipeIdDiv.attr("id", "#activeRecipeId");
        recipeIdDiv.text(id)
        recipeIdDiv.hide();
        recipeIdDiv.appendTo('body');

        var recipeTitleDiv = $('<div />');
        recipeTitleDiv.attr("id", "#activeRecipeTitle");
        recipeTitleDiv.text(title)
        recipeTitleDiv.hide();
        recipeTitleDiv.appendTo('body');



            //Get the screen height and width
            var
        maskHeight = $(document).height();
        var maskWidth = $(window).width();

        //Set heigth and width to mask to fill up the whole screen
        $('#mask').css({'width': maskWidth, 'height': maskHeight});

        //transition effect
        $('#mask').fadeIn(1000);
        $('#mask').fadeTo("slow", 0.8);

        //Get the window height and width
        var winH = $(window).height();
        var winW = $(window).width();

        //Set the popup window to center
        $(id).css('top', winH / 2 - $(id).height() / 2);
        $(id).css('left', winW / 2 - $(id).width() / 2);

        //transition effect
        $(id).fadeIn(2000);

    });

    //if close button is clicked
    $('.window .close').click(function (e) {
        //Cancel the link behavior
        e.preventDefault();

        $('#mask').hide();
        $('.window').hide();
    });

    //if mask is clicked
    $('#mask').click(function () {
        $(this).hide();
        $('.window').hide();
    });

    $(window).resize(function () {

        var box = $('#boxes .window');

        //Get the screen height and width
        var maskHeight = $(document).height();
        var maskWidth = $(window).width();

        //Set height and width to mask to fill up the whole screen
        $('#mask').css({'width': maskWidth, 'height': maskHeight});

        //Get the window height and width
        var winH = $(window).height();
        var winW = $(window).width();

        //Set the popup window to center
        box.css('top', winH / 2 - box.height() / 2);
        box.css('left', winW / 2 - box.width() / 2);

    });

});

function addRecipe(day, recipeId) {
    $.ajax({

        type: "POST",
        url: "/menus/planrecipeByDay",
        data: "recipeId=" + recipeId + "&day=" + day,
        dataType: "xml"
    });
}






