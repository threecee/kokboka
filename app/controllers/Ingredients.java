package controllers;


import models.Ingredient;
import models.IngredientType;
import models.Recipe;
import models.Tag;
import play.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;


public class Ingredients extends CRUD {

    public static void index()
    {
        List<String> descriptions = Ingredient.find("select DISTINCT(i.description) from Ingredient i where i.recipe != null order by i.description").fetch();

        List<IngredientType> ingredientTypes = IngredientType.findAll();

        render("Ingredients/grouping.html", descriptions, ingredientTypes);

    }
    public static void housekeeping()
    {
        List<Ingredient> ingredients = Ingredient.find("order by description").fetch();

        render("Ingredients/housekeeping.html", ingredients);

    }


    public static void autocompleteDescriptions(String term){

        List<String> ingredients = Ingredient.find("select distinct(i.description) from Ingredient i where i.description like ? order by i.description", term + "%").fetch();


        renderJSON(ingredients);
    }

    public static void addType(String description, String type) {
        IngredientType.addIngredient(description.trim(), type.trim());
        response.print("<xml/>");
    }

    public static void removeType(String description, String type) {
        IngredientType.removeIngredient(description.trim(), type.trim());
        response.print("<xml/>");
    }

}
