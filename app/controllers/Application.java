package controllers;

import models.Ingredient;
import models.Recipe;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        List<Recipe> recipes = Recipe.find(
                "order by postedAt desc"
        ).fetch();
        render(recipes);
    }
    public static void index(String[] tags) {
        List<Recipe> recipes = Recipe.findTaggedWith(tags);
        render(tags, recipes);
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