package controllers;


import models.Ingredient;
import models.IngredientType;
import play.*;
import play.mvc.*;

import java.util.List;


public class Ingredients extends CRUD {

    public static void index()
    {
        List<String> descriptions = Ingredient.find("select DISTINCT(i.description) from Ingredient i").fetch();

        List<IngredientType> ingredientTypes = IngredientType.findAll();

        render("Ingredients/grouping.html", descriptions, ingredientTypes);

    }

}
