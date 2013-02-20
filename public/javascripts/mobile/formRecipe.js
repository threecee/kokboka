
    /*
    $(function () {
        $('#thefile').fileupload({
            dataType: 'json',
            add: function (e, data) {
                data.submit();
                $('img#bilde').hide();
                $('#floatingBarsG').show();
            },
            done: function (e, data) {
                $.each(data.result.files, function (index, file) {
                    $('img#bilde').attr("src", file.url + "?random=" + Math.random());
                    $('#floatingBarsG').hide();
                    $('img#bilde').show();
                });
            }
        });
    });

    var typewatch = function () {
        var timer = 0;
        return function (callback, ms) {
            clearTimeout(timer);
            timer = setTimeout(callback, ms);
        }
    }();

    $(document).ready(function () {


        $("#myTags").tagit({
            availableTags: ${models.Tag.tagsAsCommaSeparatedList().raw()},
            allowSpaces:true,
            beforeTagAdded: function(event, ui) {
                  addTag(ui.tag);
              },
            beforeTagRemoved: function(event, ui) {
                removeTag(ui.tag);
            }
        });
    });



     */



$(document).bind('ready', function () {


    $(document).typing({target:"#editRecipe input#title", stop: function (event, $elem) {
        updateField(event.target)
    }, delay: 2000});
    $(document).typing({target:"#editRecipe textarea#description", stop: function (event, $elem) {
        updateField(event.target)
    }, delay: 2000});
    $(document).typing({target: "input#serves", stop: function (event, $elem) {
        updateField(event.target)
    }, delay: 2000});
    $(document).typing({target: "input#servesUnit", stop: function (event, $elem) {
        updateField(event.target)
    }, delay: 2000});
    $(document).typing({target: "textarea#steps", stop: function (event, $elem) {
        updateField(event.target)
    }, delay: 2000});


    $(document).on('click', "#editRecipe #addIngredientButton", function (e) {
        var amountInput = $("#editRecipe .addTask input#add-amount");
        var unitInput = $("#editRecipe .addTask input#add-unit");
        var descriptionInput = $("#editRecipe .addTask input#add-description");

        addIngredient(amountInput.val(), unitInput.val(), descriptionInput.val());
        amountInput.val("");
        unitInput.val("");
        descriptionInput.val("");
        amountInput.focus();

    });

    $(document).on('click', "#editRecipe .ui-icon-checkbox-off",  function () {
        removeIngredient($("#editRecipe").attr("data-recipe-id"), $(this).parent().parent().attr("for"));
        var taskItem = $(this).parent().parent().parent().detach();
        moveTaskFormRecipe(taskItem);
        return false;
    });

});

    $(document).on("pageshow", "#editRecipe", function(e) {
    			$("#editRecipe #add-description").autocomplete({
    				target: $('#suggestions'),
    				source: '/ingredients/autocompleteDescriptions',
    				link: '',
                    callback: function(e) {
                            var $a = $(e.currentTarget); // access the selected item
                            $('#editRecipe #add-description').val($a.text()); // place the value of the selection into the search box
                            $("#editRecipe #add-description").autocomplete('clear'); // clear the listview
                        },
    				minLength: 1
    			});

    		});



function addIngredient(amount, unit, description) {
    $.ajax({
        type: "POST",
        url: "/recipes/addIngredient",
        data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&amount=" + amount + "&unit=" + unit + "&description=" + description,
        dataType: "xml"

    }).always(function (data) {
            injectTaskFormRecipe(data.responseText, amount, unit, description);

        });
}

function removeIngredient(id, ingredientId) {
    $.ajax({
        type: "POST",
        url: "/recipes/removeIngredient",
        data: "recipeId=" + id + "&ingredientId=" + ingredientId,
        dataType: "xml"

    });
}
function addTag(tag) {
    $.ajax({
        type: "POST",
        url: "/recipes/addTag",
        data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&tag=" + $(tag).find(".tagit-label").text(),
        dataType: "xml"

    });
}
function removeTag(tag) {
    $.ajax({
        type: "POST",
        url: "/recipes/removeTag",
        data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&tag=" + $(tag).find(".tagit-label").text(),
        dataType: "xml"

    });
}

function updateField(element) {
    $.ajax({
        type: "POST",
        url: "/recipes/update" + $(element).attr("id"),
        data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&" + $(element).attr("id") + "=" + $(element).val(),
        dataType: "xml"

    });
}

    var taskTemplateRecipe = "<div class=\"ui-checkbox\">" +
            "<input id=\"$ID$\" name=\"\" type=\"checkbox\">" +
            "<label for=\"$ID$\" data-corners=\"true\" data-shadow=\"false\" data-iconshadow=\"true\" data-wrapperels=\"span\" data-icon=\"checkbox-off\" data-theme=\"c\" class=\"ui-btn ui-btn-icon-left ui-checkbox-off ui-btn-up-c\">" +
            "<span class=\"ui-btn-inner\"><span class=\"ui-btn-text\"><span>$AMOUNT$</span> $UNIT$ $DESCRIPTION$</span>" +
            "<span class=\"ui-icon ui-icon-checkbox-off ui-icon-shadow\">&nbsp;</span></span></label></div>";


function injectTaskFormRecipe(id, amount, unit, description) {
    var injectString = taskTemplateRecipe.replace("$ID$", id).replace("$AMOUNT$", amount).replace("$UNIT$", unit).replace("$DESCRIPTION$", description);

    injectFormRecipe($('<div/>').html(injectString), "#ingredient-tasks");

}

    function injectFormRecipe(taskItem, anchorTo) {

        var page = "#editRecipe";

        taskItem.find("label").addClass("ui-corner-top");
        taskItem.find("label").removeClass("ui-corner-bottom").removeClass("ui-controlgroup-last");


        if (!$(page + " " + anchorTo + " div.ui-controlgroup-controls div.ui-checkbox").length) {
            taskItem.find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
        }
        else {
            $(page + " " + anchorTo + " div.ui-controlgroup-controls div.ui-checkbox:first").find("label").removeClass("ui-corner-top");

        }

        taskItem.prependTo(page + " " + anchorTo + " div.ui-controlgroup-controls");


    }


    function moveTaskFormRecipe(taskItem) {
         //if taskItem is om top
         if (taskItem.find("label").hasClass("ui-corner-top")) { //&& !($("#ingredient-tasks:not(:has(*))"))) {

             $("#editRecipe #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:first").find("label").addClass("ui-corner-top");
         }

         if (taskItem.find("label").hasClass("ui-corner-bottom")) {
             $("#editRecipe #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:last").find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
         }

     }