
  
    var EditProfile = (function() {

     var taskTemplateRecipe = "<div class=\"ui-checkbox\">" +
             "<input id=\"$ID$\" name=\"\" type=\"checkbox\">" +
             "<label for=\"$ID$\" data-corners=\"true\" data-shadow=\"false\" data-iconshadow=\"true\" data-wrapperels=\"span\" data-icon=\"checkbox-off\" data-theme=\"c\" class=\"ui-btn ui-btn-icon-left ui-checkbox-off ui-btn-up-c\">" +
             "<span class=\"ui-btn-inner\"><span class=\"ui-btn-text\"><span>$AMOUNT$</span> $UNIT$ $DESCRIPTION$</span>" +
             "<span class=\"ui-icon ui-icon-checkbox-off ui-icon-shadow\">&nbsp;</span></span></label></div>";

        function EditProfile(){}

 EditProfile.prototype.removeIngredient =  function(id) {
     $.ajax({
         type: "POST",
         url: "/users/removeIngredient",
         data: "id=" + id,
         dataType: "xml"

     });

            }



EditProfile.prototype.addIngredient = function(amount, unit, description) {
        $.ajax({
            type: "POST",
            url: "/users/addIngredient",
            data: "amount=" + amount + "&unit=" + unit + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                editProfile.injectTaskFormRecipe(data.responseText, amount, unit, description);

            });
    }


EditProfile.prototype.injectTaskFormRecipe = function(id, amount, unit, description) {
        var injectString = taskTemplateRecipe.replace("$ID$", id).replace("$AMOUNT$", amount).replace("$UNIT$", unit).replace("$DESCRIPTION$", description);

    this.injectFormRecipe($('<div/>').html(injectString), "#ingredient-tasks");

    }

EditProfile.prototype.injectFormRecipe = function(taskItem, anchorTo) {

            var page = "#profil";

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

EditProfile.prototype.moveTaskFormRecipe = function(taskItem) {
             //if taskItem is om top
             if (taskItem.find("label").hasClass("ui-corner-top")) { //&& !($("#ingredient-tasks:not(:has(*))"))) {

                 $("#profil #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:first").find("label").addClass("ui-corner-top");
             }

             if (taskItem.find("label").hasClass("ui-corner-bottom")) {
                 $("#profil #ingredient-tasks div.ui-controlgroup-controls div.ui-checkbox:last").find("label").addClass("ui-corner-bottom").addClass("ui-controlgroup-last");
             }

         }

return EditProfile;

    })();

    var editProfile = new EditProfile();


    <!-- PROFIL START -->
    $(document).on('pageinit', "#profil", function(){

        $(document).on('vclick', "#profil span.plussknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            var newNumber = number + 1;
            editProfile.changePreferredServings(newNumber);
            $(this).parent().parent().parent().find(".mengde").each( function(){ $(this).text(newNumber); });
            return false;
        });

        $(document).on('vclick', "#profil span.minusknapp", function () {
            var mengde = $(this).parent().find(".mengde");
            var number = parseInt($(mengde).text());
            if (number > 0) {
                var newNumber = number - 1;
                editProfile.changePreferredServings(newNumber);
                $(this).parent().parent().parent().find(".mengde").each( function(){ $(this).text(newNumber); });
            }
            return false;
        });


        $(document).on('vclick', "#profil #addIngredientButton", function (e) {
            var amountInput = $("#profil .addTask input#add-amount");
            var unitInput = $("#profil .addTask input#add-unit");
            var descriptionInput = $("#profil .addTask input#add-description");

            editProfile.addIngredient(amountInput.val(), unitInput.val(), descriptionInput.val());
            amountInput.val("");
            unitInput.val("");
            descriptionInput.val("");
            amountInput.focus();

        });

        $(document).on('click', "#profil .ui-icon-checkbox-off",  function () {
            editProfile.removeIngredient($(this).parent().parent().attr("for"));
            var taskItem = $(this).parent().parent().parent().detach();
            editProfile.moveTaskFormRecipe(taskItem);
            return false;
        });
   $(document).on('click', "#profil .ui-checkbox",  function () {
       editProfile.removeIngredient($(this).find("label").attr("for"));
       var taskItem = $(this).detach();
       editProfile.moveTaskFormRecipe(taskItem);
       return false;
   });

        $("#profil #add-description").autocomplete({
     				target: $('#suggestions'),
     				source: '/ingredients/autocompleteDescriptions',
     				link: '',
                     callback: function(e) {
                             var $a = $(e.currentTarget); // access the selected item
                             $('#profil #add-description').val($a.text()); // place the value of the selection into the search box
                             $("#profil #add-description").autocomplete('clear'); // clear the listview
                         },
     				minLength: 1
     			});


    });




    function changePreferredServings(amount) {
        $.ajax({
            type: "POST",
            url: "/users/preferredservings",
            data: "amount=" + amount,
            dataType: "xml"
        });
    }
    <!-- PROFIL END -->







