package utils;


import models.Recipe;
import models.User;

public class Servings {


    public static String getPreferredServings(User user, Recipe recipe)
    {
        if(recipe.servesUnit != null && (recipe.servesUnit.compareToIgnoreCase("personer") == 0 || recipe.servesUnit.compareToIgnoreCase("porsjoner") == 0 ))
        {
            if(user.preferredServings != null)
            {
                return utils.Currency.prettyDouble(user.preferredServings);
            }
        }
        return utils.Currency.prettyDouble(recipe.serves);
    }

}
