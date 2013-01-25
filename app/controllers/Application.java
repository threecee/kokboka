package controllers;

import models.Ingredient;
import models.Recipe;
import models.Tag;
import play.mvc.Controller;
import utils.TagUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    public static void index() {
        List<Recipe> recipes = Recipe.find("order by title desc").fetch();
        render(recipes);
    }

    public static void index(String[] tags) {
        List<Recipe> recipes;
        List<Tag> selectedTags = new ArrayList<Tag>();
        Collection<Tag> availableTags = new ArrayList<Tag>();

        if (tags != null) {
            for (String tag : tags) {
                Tag newTag = Tag.find("nameHash = ?", tag).first();
                if (newTag != null) {
                    selectedTags.add(newTag);
                }
            }
        }

        if (selectedTags != null && selectedTags.size() > 0) {
            recipes = Recipe.findTaggedWith(TagUtil.createArray(selectedTags));
        } else {
            recipes = Recipe.find("order by title desc").fetch();

        }

        Collection<Tag> availableTagsFromDb = Tag.findTagsFromRecipes(recipes);

        for(Tag availableTagFromDb:availableTagsFromDb)
        {
            if(!selectedTags.contains(availableTagFromDb))
            {
                availableTags.add(availableTagFromDb);
            }
        }

        List<Map> tagCloud = Tag.getCloud();


        render(selectedTags, availableTags, tagCloud, recipes);
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