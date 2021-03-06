package controllers.json;


import com.google.gson.Gson;
import controllers.CRUD;
import controllers.ParentControllerCRUD;
import controllers.Secure;
import controllers.Security;
import flexjson.JSONSerializer;
import models.*;
import play.Logger;
import play.mvc.Before;
import play.mvc.Http;
import play.mvc.With;
import utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@With(Secure.class)
public class ShoppingLists extends ParentControllerCRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }


    public static void getShoppingListJSON() {
        User user = User.find("byEmail", Security.connected()).first();
        Date startingDay = DateUtil.getStartingDayForWeek(8);

        Menu menu = Menu.find("usedFromDate = ?", startingDay).first();
        if (menu == null) {
            menu = new Menu(user, startingDay).save();
        }
        ShoppingList shoppingList = ShoppingList.findById(menu.shoppingList.id);
        List<ShoppingListIngredient> shoppingListIngredients = ShoppingListIngredient.find("shoppingList is ? order by description", shoppingList).fetch();

        shoppingList.shoppingListIngredients = shoppingListIngredients;
        JSONSerializer serializer = new JSONSerializer().include("shoppingListIngredients");

        renderFlex(shoppingList, serializer);
    }



    public static void shoppinglistitem()
    {
        Logger.info(params.toString());
        ok();
    }

    public static void getShoppinglistItem(Long id) {
        ShoppingListIngredient ingredient = ShoppingListIngredient.findById(id);

        JSONSerializer serializer = new JSONSerializer();//.include("title").include("id").exclude("*");

        renderFlex(ingredient, serializer);

    }

    public static void saveShoppinglistItem(String body) {

        Gson gson = new Gson();

        ShoppingListIngredient item = gson.fromJson(body, ShoppingListIngredient.class);

        ShoppingListIngredient ingredient = ShoppingListIngredient.findById(item.id);

        ingredient.description = item.description;
        ingredient.amount = item.amount;
        ingredient.unit = item.unit;
        ingredient.checked = item.checked;

        ingredient.save();
    }

    public static void createShoppinglistItem(String body) {
        Gson gson = new Gson();

       // String shoppingListId =  body.indexOf("\"shoppingList\"")

        ShoppingListIngredient item = gson.fromJson(body, ShoppingListIngredient.class);

        ShoppingListIngredient ingredient = new ShoppingListIngredient(item.shoppingList, item.amount, item.unit, item.description, item.type, item.checked );

        ingredient.description = item.description;
        ingredient.amount = item.amount;
        ingredient.unit = item.unit;
        ingredient.checked = item.checked;

        ingredient.save();

        JSONSerializer serializer = new JSONSerializer();//.include("title").include("id").exclude("*");

        renderFlex(ingredient, serializer);

    }

    public static void uncheck(Long id) {
        toggleIngredient(id, false);
    }

    private static void toggleIngredient(Long id, boolean toggle) {
        ShoppingListIngredient shoppingListIngredient = ShoppingListIngredient.findById(id);
        shoppingListIngredient.checked = toggle;
        shoppingListIngredient.save();
    }

}
