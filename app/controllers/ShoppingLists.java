package controllers;


import models.*;
import play.Logger;
import play.mvc.Before;
import play.mvc.With;
import utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@With(Secure.class)
public class ShoppingLists extends CRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }


    public static void uke(int uke) {

        Date startDag = DateUtil.getStartingDayForWeek(uke);

        User user = User.find("byEmail", Security.connected()).first();
        Menu menu = Menu.find("user = ? and usedForDate = ?", user, startDag).first();
        if (menu == null) {
            menu = new Menu(user, startDag).save();
        }
        show(menu.id);
    }

    public static void showNextWeek(Long id) {
        Menu menu = Menu.findById(id);
        User user = User.find("byEmail", Security.connected()).first();
        show(menu.getNextWeeksMenu(user).id);
    }

    public static void showLastWeek(Long id) {
        Menu menu = Menu.findById(id);
        User user = User.find("byEmail", Security.connected()).first();
        show(menu.getLastWeeksMenu(user).id);
    }

    public static void show(Long id) {
        show(id, false);
    }
    public static void showMobile(Long id) {
        show(id, true);
    }

    private static void show(Long id, boolean  isMobile) {
        if (id != null) {
            Menu menu = Menu.findById(id);

            List<ShoppingListIngredient> shoppingListChecked = null;
            List<ShoppingListIngredient> shoppingListUnchecked = null;
            ShoppingList shoppingListItem = menu.shoppingList;
            if (shoppingListItem != null) {
                List<ShoppingListIngredient> shoppingList = ShoppingListIngredient.find("shoppingList is ? order by description", shoppingListItem).fetch();
                shoppingListChecked = new ArrayList<ShoppingListIngredient>();
                shoppingListUnchecked = new ArrayList<ShoppingListIngredient>();
                for (ShoppingListIngredient ingredient : shoppingList) {
                    if (ingredient.checked) {
                        shoppingListChecked.add(ingredient);
                    } else {
                        shoppingListUnchecked.add(ingredient);

                    }
                }
            }

            if(isMobile){
                render("ShoppingLists/showMobile.html", shoppingListChecked, shoppingListUnchecked, menu);

            }
            else {
            render("ShoppingLists/show.html", shoppingListChecked, shoppingListUnchecked, menu);
            }
        }

    }


    public static void showCurrent() {
        showCurrent(false);
    }
    public static void showCurrentMobile() {
        showCurrent(true);
    }

    private static void showCurrent(boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();
        Date startingDay = DateUtil.getStartingDay();

        Menu menu = Menu.find("usedFromDate = ?", startingDay).first();
        if (menu == null) {
            menu = new Menu(user, startingDay).save();
        }
        show(menu.id, isMobile);
    }


    public static void list(Long menuId) {
        list(menuId, false);
    }
    public static void listMobile(Long menuId) {
        list(menuId, true);
    }

    private static void list(Long menuId, boolean isMobile) {
        Menu menu = Menu.findById(menuId);
        User user = User.find("byEmail", Security.connected()).first();

        ShoppingList shoppingList;

        if (menu.shoppingList == null) {
            menu.shoppingList = new ShoppingList().save();
            menu.save();
        }

        shoppingList = menu.shoppingList;

        if(isMobile)
        {
            render("ShoppingLists/listMobile.html", menu, shoppingList, user);
        }   else {
        render(menu, shoppingList, user);
        }
    }

    public static void addOnTheFly(Long id, String description) {
        ShoppingList shoppingList = ShoppingList.findById(id);
        ShoppingListIngredient ingredient = shoppingList.addOnTheFly(description);
        shoppingList.save();
        response.print(ingredient.id);
    }

    public static void check(Long id) {
        toggleIngredient(id, true);
    }

    public static void uncheck(Long id) {
        toggleIngredient(id, false);
    }

    private static void toggleIngredient(Long id, boolean toggle) {
        ShoppingListIngredient shoppingListIngredient = ShoppingListIngredient.findById(id);
        shoppingListIngredient.checked = toggle;
        shoppingListIngredient.save();
    }

    public static void save(Long id, Long[] includeRecipes, String includePreferredRecipes) {
        User user = User.find("byEmail", Security.connected()).first();
        Menu menu = Menu.findById(id);

        HashMap<String, Object[]> ingredientMap = new HashMap<String, Object[]>();

        if (includeRecipes != null) {
            for (Long recipeId : includeRecipes) {
                if (recipeId != null) {
                    Recipe recipe = Recipe.findById(recipeId);
                    String amountString = params.get("recipeAmounts-" + recipeId);
                    int amount = 2;
                    try {
                        amount = Integer.parseInt(amountString);
                    } catch (NumberFormatException e) {
                        Logger.error("Greide ikke å parse recipeAmounts-felt", e);
                    }

                    RecipeInMenu recipeInMenu = RecipeInMenu.find("menu = ? and recipe = ?", menu, recipe).first();
                    recipeInMenu.amount = (double) amount;
                    recipeInMenu.save();



                    for (Ingredient ingredient : recipe.ingredients) {
                        String key = ingredient.unit.toLowerCase() + ingredient.description.toLowerCase();
                        if (ingredientMap.containsKey(key)) {
                            double currentAmount = (Double) ingredientMap.get(key)[0];
                            ingredientMap.get(key)[0] = ingredient.getScaledAmount(menu) + currentAmount;
                        } else {
                            ingredientMap.put(key, new Object[]{ ingredient.getScaledAmount(menu), ingredient.unit, ingredient.description});
                        }
                    }
                }
            }
        }
        List<ShoppingListIngredient> nonIngredientsInShoppingList = ShoppingListIngredient.find("shoppingList = ? and type != ?", menu.shoppingList, ShoppingListItemType.ingredient).fetch();


        menu.shoppingList.shoppingListIngredients = new ArrayList<ShoppingListIngredient>();
        menu.shoppingList.save();
        ShoppingListIngredient.delete("shoppingList = ? and type = ?", menu.shoppingList, ShoppingListItemType.ingredient);


        for (Object[] objects : ingredientMap.values()) {
            menu.shoppingList.addIngredient((Double) objects[0], (String) objects[1], (String) objects[2]);
        }
        menu.shoppingList.shoppingListIngredients.addAll(nonIngredientsInShoppingList);
        if (includePreferredRecipes != null && includePreferredRecipes.compareToIgnoreCase("faste") == 0) {
            for (ShoppingListIngredient preferredIngredient : user.favoriteIngredients) {
                menu.shoppingList.addIngredient(preferredIngredient.amount, preferredIngredient.unit, preferredIngredient.description);
            }

        }

        menu.shoppingList.save();

        show(menu.id);
    }
}
