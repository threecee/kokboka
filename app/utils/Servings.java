package utils;


import models.Menu;
import models.Recipe;
import models.RecipeInMenu;
import models.User;

public class Servings {

    public static String getPreferredServings(User user, Menu menu, int day) {
        Recipe recipe = menu.getRecipeForDay(day);
        return getPreferredServings(user, menu, recipe);
    }

    public static String getPreferredServings(User user, Recipe recipe) {
        return getPreferredServings(user, null, recipe);
    }

    public static String getPreferredServings(User user, Menu menu, Recipe recipe) {


        if (recipe.servesUnit != null && (recipe.servesUnit.compareToIgnoreCase("personer") == 0 || recipe.servesUnit.compareToIgnoreCase("porsjoner") == 0)) {
            if (menu != null) {
                RecipeInMenu recipeInMenu = RecipeInMenu.find("menu = ? and recipe = ?", menu, recipe).first();
                if (recipeInMenu.amount != null) {
                    return utils.Currency.prettyDouble(recipeInMenu.amount);
                }
            }
            if (user.preferredServings != null) {
                return utils.Currency.prettyDouble(user.preferredServings);
            }
        }
        return utils.Currency.prettyDouble(recipe.serves);
    }

}
