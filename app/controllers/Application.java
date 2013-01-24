package controllers;

import models.Ingredient;
import models.Recipe;
import models.Tag;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    public static void index() {
        List<Recipe> recipes = Recipe.find("order by title desc").fetch();
        render(recipes);
    }

    public static void index(List<Tag> selectedTags, Long id) {
        List<Recipe> recipes;
        if (selectedTags == null) {
            selectedTags = new ArrayList<Tag>();

        }


        if (id != null) {
            Tag tag = Tag.findById(id);
            selectedTags.add(tag);
        }

        if (selectedTags != null && selectedTags.size() > 0) {
            recipes = Recipe.findTaggedWith(selectedTags.toArray(new String[]{}));
        } else {
            recipes = Recipe.find("order by title desc").fetch();

        }
        render(selectedTags, recipes);
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