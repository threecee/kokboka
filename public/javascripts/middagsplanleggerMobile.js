    $(document).bind('ready', function () {


        $("#tasksPage div.ui-checkbox").live('change', 'input:checkbox', function () {

            boxActivated($(this));
        });


        function boxActivated(box) {
            var fromId = box.parent().parent().parent().attr("id");
            var sortering = "${sortering}";

            if (!box.find("label").hasClass("ui-checkbox-on")) {
                checkIngredient(box.find("input").attr("id"));
                var taskItem = box.detach();

                if (sortering != null && sortering == "oppskrift") {

                    $("#tasksPage #" + taskItem.find("input").attr("id")).parent().remove();
                }

                move(taskItem, fromId, "#completed-tasks");
            }
            else {
                unCheckIngredient(box.find("input").attr("id"));
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
                        var newItem = taskItem.clone();
                        newItem.find("label").removeClass("ui-checkbox-on");
                        newItem.find("label").addClass("ui-checkbox-off");
                        newItem.find("span.ui-icon-checkbox-on").removeClass("ui-icon-checkbox-on").addClass("ui-icon-checkbox-off");

                        move(newItem, fromId, toId[i]);
                    }


                }
                 else {
                    move(taskItem, fromId, toId);
                }
            }

        }


        $("#tasksPage input#add-task-input").live("keypress", function (e) {
            if (e.which == 13) {
                addOnTheFlyIngredient($("#tasksPage").attr("data-menu-id"), $(this).val());
                $(this).val("");

            }
        });

    });

    function move(taskItem, anchorFrom, anchorTo) {
        //if taskItem is om top
        if (taskItem.find("label").hasClass("ui-corner-top")) { //&& !($("#ingredient-tasks:not(:has(*))"))) {

            $("#tasksPage " + anchorFrom + " div.ui-controlgroup-controls div.ui-checkbox:first").find("label").addClass("ui-corner-top");
        }

        if (taskItem.find("label").hasClass("ui-corner-bottom")) {
            $("#tasksPage " + anchorFrom + " div.ui-controlgroup-controls div.ui-checkbox:last").find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
        }
        inject(taskItem, anchorTo);

    }

    function inject(taskItem, anchorTo) {

        taskItem.find("label").addClass("ui-corner-top");
        taskItem.find("label").removeClass("ui-corner-bottom").removeClass("ui-controlgroup-last");


        if (!$("#tasksPage " + anchorTo + " div.ui-controlgroup-controls div.ui-checkbox").length) {
            taskItem.find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
        }
        else {
            $("#tasksPage " + anchorTo + " div.ui-controlgroup-controls div.ui-checkbox:first").find("label").removeClass("ui-corner-top");

        }

        taskItem.prependTo("#tasksPage " + anchorTo + " div.ui-controlgroup-controls");


    }


    function checkIngredient(id) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/check",
            data: "id=" + id,
            dataType: "xml"
        });
    }
    function unCheckIngredient(id) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/uncheck",
            data: "id=" + id,
            dataType: "xml"
        });
    }
    function addOnTheFlyIngredient(id, description) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/addOnTheFly",
            data: "id=" + id + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                    injectTask(data.responseText, description);

                });
    }


    var groupTemplate = "<div id=\"$ID$\" data-role=\"fieldcontain\" class=\"ui-field-contain ui-body ui-br\"><fieldset data-role=\"controlgroup\" data-type=\"vertical\" class=\"ui-corner-all ui-controlgroup ui-controlgroup-vertical\"><div class=\"ui-controlgroup-controls\"><label>$DESCRIPTION$</label></div></fieldset></div>";


    var taskTemplateNEW = "<div class=\"ui-checkbox\">" +
            "<input id=\"$ID$\" name=\"\" type=\"checkbox\">" +
            "<label for=\"$ID$\" data-corners=\"true\" data-shadow=\"false\" data-iconshadow=\"true\" data-wrapperels=\"span\" data-icon=\"checkbox-off\" data-theme=\"c\" class=\"ui-btn ui-btn-icon-left ui-checkbox-off ui-btn-up-c\">" +
            "<span class=\"ui-btn-inner\"><span class=\"ui-btn-text\">$DESCRIPTION$</span>" +
            "<span class=\"ui-icon ui-icon-checkbox-off ui-icon-shadow\">&nbsp;</span></span></label></div>";

    var taskTemplate = "<li class=\"task-item\" id=\"$ID$\">" +
            "<div class=\"task-body\"><a class=\"task-checkbox\"><span class=\"icon task-checkbox \"></span></a> " +
            "<a class=\"icon task-separator\"></a>" +
            "<div class=\"title-wrapper\"><span" +
            "  class=\"title\">$DESCRIPTION$</span>" +
            "</div>" +
            "</div>" +
            "</li>";
    function injectTask(id, description) {
        var injectString = taskTemplateNEW.replace("$ID$", id).replace("$DESCRIPTION$", description);
        inject($('<div/>').html(injectString), "#ingredient-tasks");
    }
    function injectGroup(id, description) {
        if (!$(id).length) {
            var injectString = groupTemplate.replace("$ID$", id).replace("$DESCRIPTION$", description);
            $('<div/>').html(injectString).insertBefore("#ingredient-tasks");
        }
    }



<!-- RECIPES SHOWMOBILE START -->
    $(document).bind('ready', function () {

        $( "#showRecipe #popupPanel" ).live({
              popupbeforeposition: function() {
                  var h = $( window ).height();

                  $( "#popupPanel" ).css( "height", h );
              }
          });


        $("#showRecipe a.task-checkbox").live("click", function () {
            if (!$(this).hasClass("task-checked")) {
                $(this).addClass("task-checked");
                $(this).find("span.task-checkbox").addClass("task-checked");
                $(this).parent().parent().addClass("done");
            }
            else {
                $(this).removeClass("task-checked");
                $(this).find("span.task-checkbox").removeClass("task-checked");
                $(this).parent().parent().removeClass("done");
            }
        });


        $("#showRecipe a#favoritt").live("tap", function () {
            handleFavoritt($(this));
            return false;
        });


        $("#showRecipe span.plussknapp").live("click", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            var newNumber = number + 1;
            $(mengde).text(newNumber);
            scaleRecipe(number, newNumber);
        });
        $("#showRecipe span.minusknapp").live("click", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            if (number > 0) {
                var newNumber = number - 1;
                $(mengde).text(newNumber);
                scaleRecipe(number, newNumber);
            }
        });


        if (typeof String.prototype.endsWith !== 'function') {
            String.prototype.endsWith = function (suffix) {
                return this.indexOf(suffix, this.length - suffix.length) !== -1;
            };
        }

        function scaleRecipe(number, newNumber) {
            $("#ingredient-tasks span.mengde").each(function () {
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
                removeFavorite($("#showRecipe").attr("data-id"));
                favoritt.attr("data-theme", "c");
                favoritt.removeClass("ui-btn-up-b");
                favoritt.removeClass("ui-btn-hover-b");
                favoritt.removeClass("ui-btn-down-b");
                favoritt.addClass("ui-btn-up-c");
            }
            else {
                addFavorite($("#showRecipe").attr("data-id"));
                favoritt.attr("data-theme", "b");
                favoritt.removeClass("ui-btn-up-c");
                favoritt.removeClass("ui-btn-hover-c");
                favoritt.removeClass("ui-btn-down-c");
                favoritt.addClass("ui-btn-up-b");
            }
        }

        function addFavorite(id) {
            $.ajax({
                type: "POST",
                url: "/recipes/addFavorite",
                data: "id=" + id,
                dataType: "xml"
            });
        }

        function removeFavorite(id) {
            $.ajax({
                type: "POST",
                url: "/recipes/removeFavorite",
                data: "id=" + id,
                dataType: "xml"
            });
        }
    });

    <!-- RECIPES SHOWMOBILE STOP -->

<!-- SHOPINGLISTS LIST_MOBILE -->
    $(document).bind("ready", function () {

        $("#velgDager span.plussknapp").click(function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            number = number + 1;
            $(mengde).text(number);
            $(this).parent().parent().parent().find("input.recipeAmounts").val(number);
        });
        $("#velgDager span.minusknapp").click(function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            if (number > 0) {
                number = number - 1;
                $(mengde).text(number);
                $(this).parent().parent().parent().find("input.recipeAmounts").val(number);
            }
        });


        $("#velgDager a.task-checkbox").click(function () {
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
    $(document).bind("ready", function () {

        $("a.check-trash").tap(function () {
            removeRecipe($(this).parent().parent().attr("id"));
            $(this).parent().parent().detach();
        });
    });

    function removeRecipe(recipeId) {
        $.ajax({
            type: "POST",
            url: "/menus/unplanrecipefrommenu",
            data: "menuId=${menu.id}" + "&recipeId=" + recipeId ,
            dataType: "xml"

        });
    }

<!-- MENUS SHOW_MOBILE END -->

<!-- MENUMOBILE TAG -->
    $(document).bind('ready', function () {

        $("#showRecipe button.addRecipeToMenuButton").live("click", function () {

            //if($(this).attr("data-hasrecipe") == "true" )
           // {
           //   confirmRecipe($(this).attr("data-day"));
          //  }
          //  else {
            addRecipe($(this).attr("data-day"));
          //  }

             return false;
         });

        function confirmRecipe(day){

            $('#replaceRecipeButton').click();
        }

        function addRecipe(day){
            var recipeId = $("#showRecipe").attr("data-id");
            $.ajax({

                   type: "POST",
                   url: "/menus/planrecipe",
                   data: "recipeId=" + recipeId + "&day="+day,
                   dataType: "xml"

                });
            $( "#popupPanel" ).popup('close');
        }
 });
<!-- MENUMOBILE TAG END -->
