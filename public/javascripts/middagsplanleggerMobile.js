    $(document).bind('pageinit', "#tasksPage", function () {

        $(document).on('change', "#tasksPage div.ui-checkbox input:checkbox", function (event) {
            boxActivated($(this).parent());
        });


        function boxActivated(box) {
            var fromId = box.parent().parent().parent().attr("id");
            var sortering = $("#tasksPage").attr("data-sortering");

            if (!box.find("label").hasClass("ui-checkbox-on")) {
               services.checkShoppingListIngredient(box.find("input").attr("id"));
                var taskItem = box.detach();

                if (sortering != null && sortering == "oppskrift") {
                    $("#tasksPage #" + taskItem.find("input").attr("id")).parent().remove();
                }

                injectRegularTask("#completed-tasks", taskItem, "checked");

            }
            else {
                services.unCheckShoppingListIngredient(box.find("input").attr("id"));
                var toId = "#ingredient-tasks";

                if (sortering != null) {
                    if (sortering == "type") {
                        var vareId = box.find("input").attr("data-varetype");
                        if (vareId != null && vareId != "") {
                            toId = "#" + vareId + "-types";
                        }
                    }
                    else if (sortering == "oppskrift") {
                        var recipeId = box.find("input").attr("data-recipe");
                        if (recipeId != null && recipeId != "") {
                            if(recipeId.indexOf(";")>0)
                            {
                                var splitRecipes = recipeId.split(";");
                                var toId = new Array();
                                for(var i = 0; i < splitRecipes.length; i++)
                                {
                                     toId[i] = "#" + splitRecipes[i] + "-recipes";
                                }
                            }
                            else
                            {
                                toId = "#" + recipeId + "-recipes";
                            }
                        }
                    }
                }
                var taskItem = box.detach();

                if (toId instanceof Array) {
                    for(var i = 0; i < toId.length; i++)
                    {
                        injectRegularTask(toId[i], taskItem);
                    }

                }
                 else {
                    injectRegularTask(toId, taskItem);
                }

            }

        }


        $(document).on('keypress', "#tasksPage input#add-task-input", function (e) {
            if (e.which == 13) {
                 services.addOnTheFlyShoppingListIngredient($("#tasksPage").attr("data-menu-id"), $(this).val(), injectOnTheFlyTask);
                $(this).val("");

            }
        });

    });



    function inject(taskItem, anchorTo) {
      $("#tasksPage " + anchorTo + " .ui-controlgroup-controls").prepend(taskItem).parent().trigger("create")
        $("#tasksPage fieldset").controlgroup("refresh");
    }



    var groupTemplate = "<div id=\"$ID$\" data-role=\"fieldcontain\" class=\"ui-field-contain ui-body ui-br\"><fieldset data-role=\"controlgroup\" data-type=\"vertical\" class=\"ui-corner-all ui-controlgroup ui-controlgroup-vertical\"><div class=\"ui-controlgroup-controls\"><label>$DESCRIPTION$</label></div></fieldset></div>";

    var shoppingListItemOnTheFlyTemplate =
        "<input id=\"$ID$\" name=\"$DESCRIPTION$\" type=\"checkbox\""+
                   "data-varetype=\"$VARETYPE$\""   +
                   "data-recipe=\"$RECIPE$\" />"       +
            "<label for=\"$ID$\">$DESCRIPTION$</label>";

    var shoppingListItemTemplate =
        "<input id=\"$ID$\" name=\"$DESCRIPTION$\" type=\"checkbox\" $CHECKED$ "+
                   "data-varetype=\"$VARETYPE$\""   +
                   "data-recipe=\"$RECIPE$\" />"       +
            "<label for=\"$ID$\"><span>$AMOUNT$</span> $UNIT$ $DESCRIPTION$</label>";



    var taskTemplateNEW =
            "<input id=\"$ID$\" name=\"\" type=\"checkbox\">" +
            "<label for=\"$ID$\" data-corners=\"true\" data-shadow=\"false\" data-iconshadow=\"true\" data-wrapperels=\"span\" data-icon=\"checkbox-off\" data-theme=\"c\" class=\"ui-btn ui-btn-icon-left ui-checkbox-off ui-btn-up-c\">" +
            "<span class=\"ui-btn-inner\"><span class=\"ui-btn-text\">$DESCRIPTION$</span>" +
            "<span class=\"ui-icon ui-icon-checkbox-off ui-icon-shadow\">&nbsp;</span></span></label>";


    function injectRegularTask(target, taskItem, checked) {
        var inputItem = taskItem.find("input");

        var id = inputItem.attr("id");
        var amount = inputItem.attr("data-amount");
        var unit = inputItem.attr("data-unit");
        var description = inputItem.attr("data-description");
        var varetype = inputItem.attr("data-varetype");
        var recipe = inputItem.attr("data-recipe");


        if (varetype == null ) varetype = "";
        if (recipe == null ) recipe = "";

        var injectString = shoppingListItemTemplate.replace("$ID$", id).replace("$ID$", id).replace("$DESCRIPTION$", description).replace("$DESCRIPTION$", description).replace("$VARETYPE$", varetype).replace("$RECIPE$", recipe).replace("$AMOUNT$", amount).replace("$UNIT$", unit);
        if(checked != null)
        {
            injectString = injectString.replace("$CHECKED$","checked=\"true\"");
        }
        else{
            injectString = injectString.replace("$CHECKED$","");
        }

        inject(injectString, target);
    }

    function injectOnTheFlyTask(id, description) {
        var injectString = shoppingListItemOnTheFlyTemplate.replace("$ID$", id).replace("$ID$", id).replace("$DESCRIPTION$", description).replace("$DESCRIPTION$", description);
        inject(injectString, "#ingredient-tasks");
    }



    function injectGroup(id, description) {
        if (!$(id).length) {
            var injectString = groupTemplate.replace("$ID$", id).replace("$DESCRIPTION$", description);
            $('<div/>').html(injectString).insertBefore("#ingredient-tasks");
        }
    }



<!-- RECIPES SHOWMOBILE START -->
    $(document).on('pageinit', "#showRecipe", function () {

        $(document).on('popupbeforeposition', "#showRecipe #popupPanel", function() {
                  var h = $( window ).height();
                  $( "#popupPanel" ).css( "height", h );

          });

        $(document).on('vclick', "#showRecipe a#favoritt", function () {
            handleFavoritt($(this));
            return false;
        });


        $(document).on('vclick', "#showRecipe span.plussknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            var newNumber = number + 1;
            $(this).parent().parent().parent().find(".mengde").each( function(){ $(this).text(newNumber); });
            scaleRecipe(number, newNumber);
            return false;
        });
        $(document).on('vclick', "#showRecipe span.minusknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            if (number > 0) {
                var newNumber = number - 1;
                $(this).parent().parent().parent().find(".mengde").each( function(){ $(this).text(newNumber); });
                scaleRecipe(number, newNumber);
            }
        });


        if (typeof String.prototype.endsWith !== 'function') {
            String.prototype.endsWith = function (suffix) {
                return this.indexOf(suffix, this.length - suffix.length) !== -1;
            };
        }

        function scaleRecipe(number, newNumber) {
            $("#ingredient-tasks .ui-btn-text span").each(function () {
                        $(this).text(scaleIngredient($(this).text().trim(), number, newNumber));
                    }
            );
        }


        function scaleIngredient(amount, serving, scaledServing) {
            var multiplier = scaledServing / serving;

            var roundedNumber = Number(amount * multiplier).toFixed(1);
            if ((roundedNumber + "").endsWith(".0")) {
                roundedNumber = Number(roundedNumber).toFixed(0);
            }
            return roundedNumber;
        }

        function handleFavoritt(favoritt) {
            if (favoritt.attr("data-theme") == "b") {
                services.removeRecipeAsFavorite($("#showRecipe").attr("data-id"));
                favoritt.attr("data-theme", "c");

                favoritt.removeClass("ui-btn-up-b");
                favoritt.removeClass("ui-btn-hover-b");
                favoritt.removeClass("ui-btn-down-b");
                favoritt.addClass("ui-btn-up-c");
            }
            else {
                services.addRecipeAsFavorite($("#showRecipe").attr("data-id"));
                favoritt.attr("data-theme", "b");

                favoritt.removeClass("ui-btn-up-c");
                favoritt.removeClass("ui-btn-hover-c");
                favoritt.removeClass("ui-btn-down-c");
                favoritt.addClass("ui-btn-up-b");
            }
        }


    });

    <!-- RECIPES SHOWMOBILE STOP -->

<!-- SHOPINGLISTS LIST_MOBILE -->
    $(document).on("pageinit", "#velgDager", function () {

        $(document).on('vclick', "#velgDager span.plussknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            number = number + 1;
            $(mengde).text(number);
            $(this).parent().parent().parent().find("input.recipeAmounts").val(number);
        });
        $(document).on('vclick', "#velgDager span.minusknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            if (number > 0) {
                number = number - 1;
                $(mengde).text(number);
                $(this).parent().parent().parent().find("input.recipeAmounts").val(number);
            }
        });


        $(document).on('vclick', "#velgDager a.task-checkbox", function () {
            if (!$(this).hasClass("task-checked")) {
                checkRecipe($(this).parent().parent());
                $(this).addClass("task-checked");
                $(this).find("span.task-checkbox").addClass("task-checked");
            }
            else {
                unCheckRecipe($(this).parent().parent());
                $(this).removeClass("task-checked");
                $(this).find("span.task-checkbox").removeClass("task-checked");
            }
        });

    });


    function checkRecipe(element) {
        $(element).find("input").val($(element).find("input").attr("id"));
    }
    function unCheckRecipe(element) {
        $(element).find("input").val("");
    }

    <!-- SHOPINGLISTS LIST_MOBILE END -->


<!-- MENUS SHOW_MOBILE -->
    $(document).on("pageinit", "#visMeny", function () {

        $(document).on('click', "#visMeny a.check-trash", function () {
            if(confirm("Fjerne oppskrift fra meny?")){
            services.removeRecipeFromMenu($("#visMeny").attr("data-menu-id"), $(this).parent().parent().attr("id"));
            var link = $(this).parent().find(".title a");
            link.html("&nbsp;");
            link.attr("href", "");
            }
            return false;

        });
    });



<!-- MENUS SHOW_MOBILE END -->

<!-- MENUMOBILE TAG -->
    $(document).on('pageinit', "#showRecipe", function () {

        $(document).on('vclick', "#showRecipe button.addRecipeToMenuButton", function () {

            if(($(this).attr("data-hasrecipe") == "true" && confirm("Denne dagen inneholder allerede en oppskrift. Erstatte?") || $(this).attr("data-hasrecipe") != "true" ))
            {
                services.addRecipeToMenu($("#showRecipe").attr("data-id"), $(this).attr("data-day"));
                $( "#popupPanel" ).popup('close');
            }

             return false;
         });

        function confirmRecipe(day){

            $('#replaceRecipeButton').click();
        }

 });
<!-- MENUMOBILE TAG END -->

