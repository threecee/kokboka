package controllers;

import models.Ingredient;
import models.Recipe;
import models.Tag;
import models.User;
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
        render(recipes);
    }


    public static void form(Long id) {
        if (id != null) {
            Recipe recipe = Recipe.findById(id);
            render(recipe);
        }
        render();
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