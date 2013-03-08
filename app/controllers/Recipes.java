package controllers;


import flexjson.JSONSerializer;
import models.*;
import play.Logger;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.With;
import utils.DateUtil;
import utils.TagUtil;

import java.util.*;


@With(Secure.class)
public class Recipes extends ParentControllerCRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void showCurrent() {
        renderShowCurrent(false);
    }

    public static void showCurrentMobile() {
        renderShowCurrent(true);
    }



    public static void indexMobile() {
        boolean hasCurrent = hasCurrentRecipe();
        render("Recipes/indexMobile.html", hasCurrent);
    }
    public static void indexMobileAll() {
        indexRender(null, true);
    }
    public static void indexMobileTags(String[] tags) {
        indexRender(tags, true);
    }
    public static void indexMobileFromMenus(String[] tags) {
        User user = User.find("byEmail", Security.connected()).first();

        List<Recipe> recipes = RecipeInMenu.findUsedInMenu(user);


        render("Recipes/indexMobile.html", recipes);

    }



    public static void index(String[] tags) {
        indexRender(tags, false);
    }

    private static void indexRender(String[] tags, boolean isMobile) {
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

        if (isMobile) {
            render("Recipes/indexMobile.html", selectedTags, availableTags, tagCloud, recipes);
        } else {
            render(selectedTags, availableTags, tagCloud, recipes);
        }
    }

    public static void show(Long id) {
        renderShow(id, false);
    }

    public static void showMobile(Long id) {
        renderShow(id, false);
    }

    public static void update(Long id) {
        Recipe recipe = Recipe.findById(id);
        render("Recipes/form.html", recipe);
    }

    public static void ingredients() {
        List<Ingredient> ingredients = Ingredient.findAll();
        renderJSON(ingredients);
    }

    public static void getRecipesJSON(String callback) {
        List<Recipe> recipes = Recipe.findAll();
        JSONSerializer serializer = new JSONSerializer().include("title").include("id").exclude("*");

         response.setContentTypeIfNotSet("application/json");
        response.print(callback + "(" + serializer.serialize(recipes) + ");");
    }
    public static void getRecipeJSON(String callback, Long id) {
        Recipe recipe = Recipe.findById(id);
        JSONSerializer serializer = new JSONSerializer().exclude("author");

         response.setContentTypeIfNotSet("application/json");
        response.print(callback + "(" + serializer.serialize(recipe) + ");");
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


    public static void addIngredient(Long id, Double amount, String unit, String description) {
        Recipe recipe = Recipe.findById(id);
        Ingredient ingredient = recipe.addIngredient(amount, unit, description);
        recipe.save();
        response.print(ingredient.id);
    }

    public static void updateIngredient(Long id, Double amount, String unit, String description) {
        Ingredient ingredient = Ingredient.findById(id);
        ingredient.amount = amount;
        ingredient.unit = unit;
        ingredient.description = description;
        ingredient.save();
        response.print("<xml/>");

    }

    public static void removeIngredient(Long recipeId, Long ingredientId) {
        Recipe recipe = Recipe.findById(recipeId);
        Ingredient ingredient = Ingredient.findById(ingredientId);
        recipe.ingredients.remove(ingredient);
        recipe.save();
        ingredient.delete();
        response.print("<xml/>");
    }

    public static void updateTitle(Long id, String title) {
        Recipe recipe = Recipe.findById(id);
        recipe.title = title;
        recipe.save();
        response.print("<xml/>");

    }

    public static void updateDescription(Long id, String description) {
        Recipe recipe = Recipe.findById(id);
        recipe.description = description;
        recipe.save();
        response.print("<xml/>");

    }

    public static void updateServes(Long id, double serves) {
        Recipe recipe = Recipe.findById(id);
        recipe.serves = serves;
        recipe.save();
        response.print("<xml/>");

    }

    public static void updateServesUnit(Long id, String servesUnit) {
        Recipe recipe = Recipe.findById(id);
        recipe.servesUnit = servesUnit;
        recipe.save();
        response.print("<xml/>");

    }

    public static void updateSteps(Long id, String steps) {
        Recipe recipe = Recipe.findById(id);
        recipe.steps = steps;
        recipe.save();
        response.print("<xml/>");

    }

    public static void updateSource(Long id, String source) {
        Recipe recipe = Recipe.findById(id);
        recipe.steps = source;
        recipe.save();
        response.print("<xml/>");
    }

    public static void addTag(Long id, String tag) {
        Recipe recipe = Recipe.findById(id);
        recipe.tagItWith(tag);
        recipe.save();
        response.print("<xml/>");

    }

    public static void removeTag(Long id, String tag) {
        Recipe recipe = Recipe.findById(id);

        String hashedTag = Tag.hashName(tag);

        for(Tag existingTag: recipe.tags)
        {
            if(hashedTag.compareToIgnoreCase(existingTag.nameHash) == 0)
            {
                recipe.tags.remove(existingTag);
                break;
            }

        }



        recipe.save();
        response.print("<xml/>");

    }

    public static void updatePhoto(Long id, Blob photo) {
        Recipe recipe = Recipe.findById(id);

    }

    private static void renderShow(Long id, boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();
        Recipe recipe = Recipe.findById(id);
        boolean favorite = user.favorites.contains(recipe);
        if (isMobile) {
            render("Recipes/showMobile.html", user, recipe, favorite);
        } else {
            render(user, recipe, favorite);
        }
    }

    private static boolean hasCurrentRecipe() {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.find("author = ? and usedFromDate = ?", user, DateUtil.getStartingDayThisWeek(new Date())).first();

        if (menu != null) {
            int distance = DateUtil.distance(menu.usedFromDate, new Date());

            Recipe recipe = menu.getRecipeForDay(distance);

            return recipe != null;

        }
        return false;
    }

    private static void renderShowCurrent(boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.find("author = ? and usedFromDate = ?", user, DateUtil.getStartingDayThisWeek(new Date())).first();

        if (menu != null) {
            int distance = DateUtil.distance(menu.usedFromDate, new Date());

            Recipe recipe = menu.getRecipeForDay(distance);
            boolean favorite = user.favorites.contains(recipe);

            if (isMobile) {

                render("Recipes/showMobile.html", user, recipe, favorite, menu);
            } else {
                render("Recipes/show.html", user, recipe, favorite, menu);

            }
        }
        if (isMobile) {
            render("Recipes/showMobile.html");
        } else {
            render("Recipes/show.html");
        }

    }

    private static void renderFavorites(boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();
        List<Recipe> recipes = user.favorites;
        if (isMobile) {
            render("Recipes/indexMobile.html", recipes);
        } else {
            render("Recipes/index.html", recipes);
        }

    }

    private static void renderForm(Long id, boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();
        Recipe recipe;
        if (id != null) {
            recipe = Recipe.findById(id);
            Logger.info("Hentet oppskrift nr" + recipe.id);
        } else {
            recipe = Recipe.find("title = null").first();
            if (recipe == null) {
                recipe = new Recipe(user);
                recipe.save();
            } else {
                Logger.info("Hentet første tomme oppskrift - nr " + recipe.id);

            }
        }
        if (isMobile) {
            render("Recipes/formMobile.html", recipe);
        }
        {
            render(recipe);
        }
    }


    public static void favorites() {
        renderFavorites(false);
    }

    public static void favoritesMobile() {
        renderFavorites(true);
    }

    public static void save(Long id, String title, String description, double serves, String servesUnits, Double[] amounts, String[] units, String[] ingredients, String steps, String source, String tags, Blob photo) {
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

    private static boolean isValid(Double amount, String ingredient) {
        return amount != null && ingredient != null && ingredient.length() > 0;
    }

    public static void form(Long id) {
        renderForm(id, false);
    }

    public static void formMobile(Long id) {
        renderForm(id, true);
    }

}
