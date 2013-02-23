var Services = (function () {


    function Services() {
    }

    Services.prototype.removeUserPreferredIngredient = function (id) {
        $.ajax({
            type: "POST",
            url: "/users/removeIngredient",
            data: "id=" + id,
            dataType: "xml"

        });

    }


    Services.prototype.addUserPreferredIngredient = function (amount, unit, description, callback) {
        $.ajax({
            type: "POST",
            url: "/users/addIngredient",
            data: "amount=" + amount + "&unit=" + unit + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                callback(data.responseText, amount, unit, description);

            });
    }


    Services.prototype.changeUserPreferredServings = function (amount) {
        $.ajax({
            type: "POST",
            url: "/users/preferredservings",
            data: "amount=" + amount,
            dataType: "xml"
        });
    }


    Services.prototype.checkShoppingListIngredient = function (id) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/check",
            data: "id=" + id,
            dataType: "xml"
        });
    }

    Services.prototype.unCheckShoppingListIngredient = function (id) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/uncheck",
            data: "id=" + id,
            dataType: "xml"
        });
    }
    Services.prototype.addOnTheFlyShoppingListIngredient = function (id, description, callback) {
        $.ajax({
            type: "POST",
            url: "/shoppinglists/addOnTheFly",
            data: "id=" + id + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                callback(data.responseText, description);

            });
    }


    Services.prototype.addRecipeAsFavorite = function (id) {
        $.ajax({
            type: "POST",
            url: "/recipes/addFavorite",
            data: "id=" + id,
            dataType: "xml"
        });
    }

    Services.prototype.removeRecipeAsFavorite = function (id) {
        $.ajax({
            type: "POST",
            url: "/recipes/removeFavorite",
            data: "id=" + id,
            dataType: "xml"
        });
    }

    Services.prototype.removeRecipeFromMenu = function (menuId, recipeId) {
        $.ajax({
            type: "POST",
            url: "/menus/unplanrecipefrommenu",
            data: "menuId=" + menuId + "&recipeId=" + recipeId,
            dataType: "xml"

        });
    }


    Services.prototype.addRecipeToMenu = function (recipeId, day) {
        $.ajax({

            type: "POST",
            url: "/menus/planrecipe",
            data: "recipeId=" + recipeId + "&day=" + day,
            dataType: "xml"

        });
    }


    Services.prototype.removeIngredientFromRecipe = function (id, ingredientId) {
        $.ajax({
            type: "POST",
            url: "/recipes/removeIngredient",
            data: "recipeId=" + id + "&ingredientId=" + ingredientId,
            dataType: "xml"

        });
    }


    Services.prototype.addIngredientToRecipe = function (id, amount, unit, description, callback) {
        $.ajax({
            type: "POST",
            url: "/recipes/addIngredient",
            data: "id=" + id + "&amount=" + amount + "&unit=" + unit + "&description=" + description,
            dataType: "xml"

        }).always(function (data) {
                callback(data.responseText, amount, unit, description);

            });
    }


    Services.prototype.addTagToRecipe = function (id, tag) {
        $.ajax({
            type: "POST",
            url: "/recipes/addTag",
            data: "id=" + id + "&tag=" + tag,
            dataType: "xml"

        });
    }

    Services.prototype.removeTagFromRecipe = function (id, tag) {
        $.ajax({
            type: "POST",
            url: "/recipes/removeTag",
            data: "id=" + id + "&tag=" + tag,
            dataType: "xml"

        });
    }

    Services.prototype.updateRecipeField = function (recipeId, elementId, elementValue) {
        $.ajax({
            type: "POST",
            url: "/recipes/update" + elementId,
            data: "id=" + recipeId + "&" + elementId + "=" + elementValue,
            dataType: "xml"

        });
    }


    return Services;

})();

var services = new Services();


