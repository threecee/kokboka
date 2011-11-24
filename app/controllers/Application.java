package controllers;

import models.Ingredient;
import models.Recipe;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        Recipe frontRecipe = Recipe.find("order by postedAt desc").first();
        List<Recipe> olderRecipes = Recipe.find(
                "order by postedAt desc"
        ).from(1).fetch(10);
        render(frontRecipe, olderRecipes);
    }

    public static void show(Long id) {
        Recipe recipe = Recipe.findById(id);
        render(recipe);
    }

    public static void ingredients() {
        List<Ingredient> ingredients = Ingredient.findAll();
        renderJSON(ingredients);
    }

    public static void recipes_json() {
        List<Recipe> recipes = Recipe.findAll();
        List<Ingredient> ingredients = Ingredient.findAll();
        // renderJSON(recipes);
        List<Object> allEntities = new ArrayList<Object>();
        allEntities.addAll(recipes);
        allEntities.addAll(ingredients);
        renderXml(allEntities);
    }

    public static void listTagged(String tag) {
        List<Recipe> recipes = Recipe.findTaggedWith(tag);
        render(tag, recipes);
    }
}