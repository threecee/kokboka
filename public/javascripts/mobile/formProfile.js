var EditProfile = (function () {
    function EditProfile() {
    }

    EditProfile.prototype.injectTaskFormRecipe = function (id, amount, unit, description) {
        views.injectCheckbox("#profil", "#ingredient-tasks", id, amount, unit, description);
    }


    return EditProfile;

})();

var editProfile = new EditProfile();


<!-- PROFIL START -->
$(document).on('pageinit', "#profil", function () {

    $(document).on('vclick', "#profil span.plussknapp", function () {
        var mengde = $(this).parent().find(".mengde");
        var number = parseInt($(mengde).text());
        var newNumber = number + 1;
        services.changeUserPreferredServings(newNumber);
        $(this).parent().parent().parent().find(".mengde").each(function () {
            $(this).text(newNumber);
        });
        return false;
    });

    $(document).on('vclick', "#profil span.minusknapp", function () {
        var mengde = $(this).parent().find(".mengde");
        var number = parseInt($(mengde).text());
        if (number > 0) {
            var newNumber = number - 1;
            services.changeUserPreferredServings(newNumber);
            $(this).parent().parent().parent().find(".mengde").each(function () {
                $(this).text(newNumber);
            });
        }
        return false;
    });


    $(document).on('vclick', "#profil #addIngredientButton", function (e) {
        var amountInput = $("#profil .addTask input#add-amount");
        var unitInput = $("#profil .addTask input#add-unit");
        var descriptionInput = $("#profil .addTask input#add-description");

        services.addUserPreferredIngredient(amountInput.val(), unitInput.val(), descriptionInput.val(), editProfile.injectTaskFormRecipe);
        amountInput.val("");
        unitInput.val("");
        descriptionInput.val("");
        amountInput.focus();

    });

    $(document).on('click', "#profil .ui-icon-checkbox-off", function () {
        services.removeUserPreferredIngredient($(this).parent().parent().attr("for"));
        var taskItem = $(this).parent().parent().parent().detach();
        $("#profil fieldset").controlgroup("refresh");
        return false;
    });
    $(document).on('click', "#profil .ui-checkbox", function () {
        services.removeUserPreferredIngredient($(this).find("label").attr("for"));
        var taskItem = $(this).detach();
        $("#profil fieldset").controlgroup("refresh");
        return false;
    });

    $("#profil #add-description").autocomplete({
        target: $('#profil #ingredientSuggestions'),
        source: '/ingredients/autocompleteDescriptions',
        link: '',
        callback: function (e) {
            var $a = $(e.currentTarget); // access the selected item
            $('#profil #add-description').val($a.text()); // place the value of the selection into the search box
            $("#profil #add-description").autocomplete('clear'); // clear the listview
        },
        minLength: 1
    });


});


<!-- PROFIL END -->







