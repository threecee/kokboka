
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

    var EditRecipe = (function() {

     var taskTemplateRecipe = "<div class=\"ui-checkbox\">" +
             "<input id=\"$ID$\" name=\"\" type=\"checkbox\">" +
             "<label for=\"$ID$\" data-corners=\"true\" data-shadow=\"false\" data-iconshadow=\"true\" data-wrapperels=\"span\" data-icon=\"checkbox-off\" data-theme=\"c\" class=\"ui-btn ui-btn-icon-left ui-checkbox-off ui-btn-up-c\">" +
             "<span class=\"ui-btn-inner\"><span class=\"ui-btn-text\"><span>$AMOUNT$</span> $UNIT$ $DESCRIPTION$</span>" +
             "<span class=\"ui-icon ui-icon-checkbox-off ui-icon-shadow\">&nbsp;</span></span></label></div>";

        function EditRecipe(){}

        EditRecipe.prototype.removeIngredient =  function(id, ingredientId) {
                $.ajax({
                    type: "POST",
                    url: "/recipes/removeIngredient",
                    data: "recipeId=" + id + "&ingredientId=" + ingredientId,
                    dataType: "xml"

                });
            }




EditRecipe.prototype.addIngredient = function(amount, unit, description) {
        $.ajax({
            type: "POST",
            url: "/recipes/addIngredient",
            data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&amount=" + amount + "&unit=" + unit + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                editRecipe.injectTaskFormRecipe(data.responseText, amount, unit, description);

            });
    }


EditRecipe.prototype.addTag =  function(tag) {
        $.ajax({
            type: "POST",
            url: "/recipes/addTag",
            data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&tag=" + tag,
            dataType: "xml"

        });
    }

EditRecipe.prototype.removeTag = function(tag) {
        $.ajax({
            type: "POST",
            url: "/recipes/removeTag",
            data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&tag=" + tag,
            dataType: "xml"

        });
    }

EditRecipe.prototype.injectTag = function(tag) {
    var tagTemplate = "<a data-role=\"button\" data-theme=\"c\" data-icon=\"delete\" data-iconpos=\"right\" href=\"#\">" +
                                "<span class=\"tagit-label\">$TAG$</span>"+
                            "</a>";

    var injectString = tagTemplate.replace("$TAG$", tag);

    $("<span class=\"tagButton\">").html(injectString).appendTo(".tagger").trigger("create");

}

EditRecipe.prototype.updateField = function(element) {
        $.ajax({
            type: "POST",
            url: "/recipes/update" + $(element).attr("id"),
            data: "id=" + $("#editRecipe").attr("data-recipe-id") +"&" + $(element).attr("id") + "=" + $(element).val(),
            dataType: "xml"

        });
    }

EditRecipe.prototype.injectTaskFormRecipe = function(id, amount, unit, description) {
        var injectString = taskTemplateRecipe.replace("$ID$", id).replace("$AMOUNT$", amount).replace("$UNIT$", unit).replace("$DESCRIPTION$", description);

    this.injectFormRecipe($('<div/>').html(injectString), "#ingredient-tasks");

    }


EditRecipe.prototype.injectFormRecipe = function(taskItem, anchorTo) {

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

EditRecipe.prototype.moveTaskFormRecipe = function(taskItem) {
             //if taskItem is om top
             if (taskItem.find("label").hasClass("ui-corner-top")) { //&& !($("#ingredient-tasks:not(:has(*))"))) {

                 $("#editRecipe #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:first").find("label").addClass("ui-corner-top");
             }

             if (taskItem.find("label").hasClass("ui-corner-bottom")) {
                 $("#editRecipe #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:last").find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
             }

         }

return EditRecipe;

    })();

    var editRecipe = new EditRecipe();

$(document).on('pageinit', "#editRecipe", function () {

         $(document).typing({target:"#editRecipe input#title", stop: function (event, $elem) {
             editRecipe.updateField(event.target)
         }, delay: 2000});
         $(document).typing({target:"#editRecipe textarea#description", stop: function (event, $elem) {
             editRecipe.updateField(event.target)
         }, delay: 2000});
         $(document).typing({target: "input#serves", stop: function (event, $elem) {
             editRecipe.updateField(event.target)
         }, delay: 2000});
         $(document).typing({target: "input#servesUnit", stop: function (event, $elem) {
             editRecipe.updateField(event.target)
         }, delay: 2000});
         $(document).typing({target: "textarea#steps", stop: function (event, $elem) {
             editRecipe.updateField(event.target)
         }, delay: 2000});

         $(document).on('vclick', "#editRecipe #addIngredientButton", function (e) {
             var amountInput = $("#editRecipe .addTask input#add-amount");
             var unitInput = $("#editRecipe .addTask input#add-unit");
             var descriptionInput = $("#editRecipe .addTask input#add-description");

             editRecipe.addIngredient(amountInput.val(), unitInput.val(), descriptionInput.val());
             amountInput.val("");
             unitInput.val("");
             descriptionInput.val("");
             amountInput.focus();

         });

         $(document).on('vclick', "#editRecipe .ui-icon-checkbox-off",  function () {
             editRecipe.removeIngredient($("#editRecipe").attr("data-recipe-id"), $(this).parent().parent().attr("for"));
             var taskItem = $(this).parent().parent().parent().detach();
             editRecipe.moveTaskFormRecipe(taskItem);
             return false;
         });
    $(document).on('vclick', "#editRecipe .ui-checkbox",  function () {
        editRecipe.removeIngredient($("#editRecipe").attr("data-recipe-id"), $(this).find("label").attr("for"));
        var taskItem = $(this).detach();
        editRecipe.moveTaskFormRecipe(taskItem);
        return false;
    });
    $(document).on('vclick', "#editRecipe .tagger .tagButton",  function () {
        editRecipe.removeTag($(this).find(".tagit-label").text());
        var taskItem = $(this).detach();
        return false;
    });

         $("#editRecipe #add-description").autocomplete({
      				target: $('#ingredientSuggestions'),
      				source: '/ingredients/autocompleteDescriptions',
      				link: '',
                      callback: function(e) {
                              var $a = $(e.currentTarget); // access the selected item
                              $('#editRecipe #add-description').val($a.text()); // place the value of the selection into the search box
                              $("#editRecipe #add-description").autocomplete('clear'); // clear the listview
                          },
      				minLength: 1
      			});

    $("#editRecipe .taggListe #add-tag-input").autocomplete({
 				target: $('#tagSuggestions'),
 				source: '/tags/autocompleteTags',
 				link: '',
                 callback: function(e) {
                         var $a = $(e.currentTarget); // access the selected item
                     editRecipe.addTag($a.text());
                     editRecipe.injectTag($a.text());

                        $("#editRecipe .taggListe #add-tag-input").val(""); // place the value of the selection into the search box
                         $("#editRecipe .taggListe #add-tag-input").autocomplete('clear'); // clear the listview
                     },
 				minLength: 1
 			});


});





