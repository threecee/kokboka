package controllers;

import models.*;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Admin extends Controller {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void index() {
        String user = Security.connected();
        List<Recipe> recipes = Recipe.find("author.email", user).fetch();
        List<Menu> menus = Menu.find("author.email", user).fetch();
        render(recipes, menus);
    }


    public static void form(Long id) {
        if (id != null) {
            Recipe recipe = Recipe.findById(id);
            render(recipe);
        }
        render();
    }

    public static void menuform(Long id) {
        if (id != null) {
            Menu menu = Menu.findById(id);
            render(menu);
        }
        render();
    }


    public static void saveMenu(Long id, String title, String[] usedForDays, Long[] recipes) {
        Menu menu;

        if (id == null) {
            User author = User.find("byEmail", Security.connected()).first();
            menu = new Menu(author, title);
            menu.save();
        } else {
            // Retrieve menu
            menu = Menu.findById(id);
            // Edit
            menu.title = title;
        }

        List<RecipeInMenu> recipeInMenuList = RecipeInMenu.find("byMenu", menu).fetch();
        for (int i = 0; i < usedForDays.length; i++) {

            RecipeInMenu currentRecipeInMenu = null;
            if (recipeInMenuList.size() > i) {
                currentRecipeInMenu = recipeInMenuList.get(i);
                if (isValid(usedForDays[i], recipes[i])
                        ) {

                    currentRecipeInMenu.usedForDay = MenuDay.valueOf(usedForDays[i]);
                    currentRecipeInMenu.recipe = Recipe.findById(recipes[i]);

                    currentRecipeInMenu.save();
                } else {
                    currentRecipeInMenu.delete();
                }

            } else if(isValid(recipes[i])) {
                Recipe recipe = Recipe.findById(recipes[i]);
                menu.save();
                currentRecipeInMenu = new RecipeInMenu(menu, recipe, MenuDay.valueOf(usedForDays[i]));
                currentRecipeInMenu.save();
            }
        }


        // Validate
        validation.valid(menu);
        if (validation.hasErrors()) {
            render("@menuform", menu);
        }
        // Save
        menu.save();
        index();
    }

    private static boolean isValid(Long recipe) {
        return recipe != null && recipe >= 0;
    }

    private static boolean isValid(String usedForDay, Long recipe) {
        return usedForDay != null && usedForDay.length() > 0 && recipe != null && recipe >= 0;
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
        index();
    }

    private static boolean isValid(String amount, String ingredient) {
        return amount != null && amount.length() > 0 && ingredient != null && ingredient.length() > 0;
    }
}