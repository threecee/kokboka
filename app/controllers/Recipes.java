package controllers;


import models.*;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.With;
import utils.DateUtil;
import utils.TagUtil;

import java.io.File;
import java.util.*;


@With(Secure.class)
public class Recipes extends CRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void showCurrent() {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.find("usedFromDate = ?", DateUtil.getStartingDayThisWeek(new Date())).first();

        if (menu != null) {
            int distance = DateUtil.distance(menu.usedFromDate, new Date());

            Recipe recipe = menu.getRecipeForDay(distance);
            boolean favorite = user.favorites.contains(recipe);

            render("Recipes/show.html", recipe, favorite);
        }
        render("Recipes/show.html");
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

        for (Tag availableTagFromDb : availableTagsFromDb) {
            if (!selectedTags.contains(availableTagFromDb)) {
                availableTags.add(availableTagFromDb);
            }
        }

        List<Map> tagCloud = Tag.getCloud();


        render(selectedTags, availableTags, tagCloud, recipes);
    }

    public static void show(Long id) {
        User user = User.find("byEmail", Security.connected()).first();
        Recipe recipe = Recipe.findById(id);
        boolean favorite = user.favorites.contains(recipe);
        render(recipe, favorite);
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

    public static void addFavorite(Long id) {
        User user = User.find("byEmail", Security.connected()).first();
        Recipe recipe = Recipe.findById(id);
        user.addFavorite(recipe).save();
        response.print("<xml/>");
    }

    public static void removeFavorite(Long id) {
        User user = User.find("byEmail", Security.connected()).first();
        Recipe recipe = Recipe.findById(id);
        user.removeFavorite(recipe).save();
        response.print("<xml/>");
    }
    public static void favorites() {
        User user = User.find("byEmail", Security.connected()).first();
         List<Recipe> recipes = user.favorites;
        render("Recipes/index.html", recipes);
     }

    public static void save(Long id, String title, String description, double serves, String servesUnits, String[] amounts, String[] units, String[] ingredients, String steps, String source, String tags, Blob photo) {
        Recipe recipe;

        if (id == null) {
            // Create recipe
            User author = User.find("byEmail", Security.connected()).first();
            recipe = new Recipe(author, title, description, steps, source, serves, servesUnits);
        } else {
            // Retrieve recipe
            recipe = Recipe.findById(id);
            // Edit
            recipe.title = title;
            recipe.description = description;
            recipe.steps = steps;
            recipe.source = source;
            recipe.serves = serves;
            recipe.servesUnit = servesUnits;
            recipe.tags.clear();
        }

        recipe.save();
        List<Ingredient> ingredientList = Ingredient.find("byRecipe", recipe).fetch();
        for (int i = 0; i < amounts.length; i++) {
            System.out.println("position " + i);

            Ingredient currentIngredient = null;
            if (ingredientList.size() > i) {
                currentIngredient = ingredientList.get(i);
                if (isValid(amounts[i], ingredients[i])
                        ) {

                    currentIngredient.amount = amounts[i];
                    currentIngredient.description = ingredients[i];
                    currentIngredient.unit = units[i];

                    currentIngredient.save();
                } else {
                    currentIngredient.delete();
                }

            } else {
                currentIngredient = new Ingredient(recipe, amounts[i], units[i], ingredients[i]).save();
            }
        }

        // Set tags list
        for (String tag : tags.split("\\s+")) {
            if (tag.trim().length() > 0) {
                recipe.tags.add(Tag.findOrCreateByName(tag));
            }
        }
        // Validate
        validation.valid(recipe);
        if (validation.hasErrors()) {
            render("@form", recipe);
        }
        // Save
        recipe.save();
        recipe.author.addFavorite(recipe);
        recipe.author.save();

        index();
    }

    private static boolean isValid(String amount, String ingredient) {
        return amount != null && amount.length() > 0 && ingredient != null && ingredient.length() > 0;
    }

    public static void form(Long id) {
         if (id != null) {
             Recipe recipe = Recipe.findById(id);
             render(recipe);
         }
         render();
     }

}
