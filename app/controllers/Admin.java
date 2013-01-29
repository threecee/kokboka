package controllers;

import models.*;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;
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




    public static void menuform(Long id) {
        if (id != null) {
            Menu menu = Menu.findById(id);
            render(menu);
        }
        render();
    }


    public static void saveMenu(Long id, Date usedFromDate, String[] usedForDays, Long[] recipes) {
        Menu menu;

        if (id == null) {
            User author = User.find("byEmail", Security.connected()).first();
            menu = new Menu(author, usedFromDate);
            menu.save();
        } else {
            // Retrieve menu
            menu = Menu.findById(id);
            // Edit
            menu.usedFromDate = usedFromDate;
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


}