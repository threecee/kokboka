package controllers;


import models.ShoppingListIngredient;
import models.User;
import play.mvc.Before;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class Users extends CRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    private static void preferencesRender(boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();

        List<ShoppingListIngredient> shoppingList = user.favoriteIngredients;

        if (isMobile) {
            render("Users/preferencesMobile.html", user, shoppingList);

        } else {
            render(user, shoppingList);
        }
    }

    public static void preferences() {
       preferencesRender(false);

    }

    public static void preferencesMobile() {
        preferencesRender(true);

    }

    public static void addIngredient(double amount, String unit, String description) {
        User user = User.find("byEmail", Security.connected()).first();

        Long id = user.addIngredient(amount, unit, description).id;
        user.save();
        response.print(id);
    }

    public static void removeIngredient(Long id) {
        User user = User.find("byEmail", Security.connected()).first();

        user.removeIngredient(id);

    }

    public static void preferredServings(double amount) {
        User user = User.find("byEmail", Security.connected()).first();

        user.preferredServings = amount;
        user.save();

    }

}
