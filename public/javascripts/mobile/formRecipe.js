
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

        function EditRecipe(){}


EditRecipe.prototype.injectTag = function(tag) {
    var tagTemplate = "<a data-role=\"button\" data-theme=\"c\" data-icon=\"delete\" data-iconpos=\"right\" href=\"#\">" +
                                "<span class=\"tagit-label\">$TAG$</span>"+
                            "</a>";

    var injectString = tagTemplate.replace("$TAG$", tag);

    $("<span class=\"tagButton\">").html(injectString).appendTo(".tagger").trigger("create");

}
EditRecipe.prototype.injectTaskFormRecipe = function(id, amount, unit, description) {
    views.injectCheckbox("#editRecipe", "#ingredient-tasks", id, amount, unit, description);

    }



return EditRecipe;

    })();

    var editRecipe = new EditRecipe();

$(document).on('pageinit', "#editRecipe", function () {

         $(document).typing({target:"#editRecipe input#title", stop: function (event, $elem) {
             services.updateRecipeField($("#editRecipe").attr("data-recipe-id"), $(event.target).attr("id"), $(event.target).val() )
         }, delay: 2000});
         $(document).typing({target:"#editRecipe textarea#description", stop: function (event, $elem) {
             services.updateRecipeField($("#editRecipe").attr("data-recipe-id"), $(event.target).attr("id"), $(event.target).val() )
         }, delay: 2000});
         $(document).typing({target: "input#serves", stop: function (event, $elem) {
             services.updateRecipeField($("#editRecipe").attr("data-recipe-id"), $(event.target).attr("id"), $(event.target).val() )
         }, delay: 2000});
         $(document).typing({target: "input#servesUnit", stop: function (event, $elem) {
             services.updateRecipeField($("#editRecipe").attr("data-recipe-id"), $(event.target).attr("id"), $(event.target).val() )
         }, delay: 2000});
         $(document).typing({target: "textarea#steps", stop: function (event, $elem) {
             services.updateRecipeField($("#editRecipe").attr("data-recipe-id"), $(event.target).attr("id"), $(event.target).val() )
         }, delay: 2000});

         $(document).on('vclick', "#editRecipe #addIngredientButton", function (e) {
             var amountInput = $("#editRecipe .addTask input#add-amount");
             var unitInput = $("#editRecipe .addTask input#add-unit");
             var descriptionInput = $("#editRecipe .addTask input#add-description");

             services.addIngredientToRecipe($("#editRecipe").attr("data-recipe-id"), amountInput.val(), unitInput.val(), descriptionInput.val(), editRecipe.injectTaskFormRecipe);
             amountInput.val("");
             unitInput.val("");
             descriptionInput.val("");
             amountInput.focus();

         });

         $(document).on('click', "#editRecipe .ui-icon-checkbox-off",  function () {
             services.removeIngredientFromRecipe($("#editRecipe").attr("data-recipe-id"), $(this).parent().parent().attr("for"));
             var taskItem = $(this).parent().parent().parent().detach();
             $("#editRecipe fieldset").controlgroup("refresh");
             return false;
         });
    $(document).on('click', "#editRecipe .ui-checkbox",  function () {
        services.removeIngredientFromRecipe($("#editRecipe").attr("data-recipe-id"), $(this).find("label").attr("for"));
        var taskItem = $(this).detach();
        $("#editRecipe fieldset").controlgroup("refresh");
        return false;
    });
    $(document).on('vclick', "#editRecipe .tagger .tagButton",  function () {
        services.removeTagFromRecipe($("#editRecipe").attr("data-recipe-id"), $(this).find(".tagit-label").text());
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
                     services.addTagToRecipe($("#editRecipe").attr("data-recipe-id"), $a.text());
                     editRecipe.injectTag($a.text());

                        $("#editRecipe .taggListe #add-tag-input").val(""); // place the value of the selection into the search box
                         $("#editRecipe .taggListe #add-tag-input").autocomplete('clear'); // clear the listview
                     },
 				minLength: 1
 			});


});





