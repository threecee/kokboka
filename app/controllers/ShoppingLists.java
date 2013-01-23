package controllers;


import models.*;
import play.Logger;
import play.mvc.With;

import java.util.HashMap;
import java.util.List;

@With(Secure.class)
public class ShoppingLists extends CRUD{

    public static void show(Long listId)
    {
        List<ShoppingListIngredient> shoppingList = ShoppingListIngredient.find("shoppingList is ? order by description", ShoppingList.findById(listId)).fetch();
        render(shoppingList);
    }

    public static void list(Long menuId)
    {
        Menu menu = Menu.findById(menuId);

        ShoppingList shoppingList;

        if(menu.shoppingList == null){
            menu.shoppingList = new ShoppingList(menu).save();
            menu.save();
        }

        shoppingList = menu.shoppingList;

        render(menu, shoppingList);
    }


    public static void save(Long id, Long[] includeRecipes)
    {
        ShoppingList shoppingList = ShoppingList.findById(id);

        HashMap<String, Object[]> ingredientMap = new HashMap<String, Object[]>();

        for(Long recipeId:includeRecipes)
        {
            Recipe recipe = Recipe.findById(recipeId);
            String amountString = params.get("recipeAmounts-"+ recipeId);
            int amount = 2;
            try{
               amount = Integer.parseInt(amountString);
            }catch (NumberFormatException e)
            {
                Logger.error("Greide ikke å parse recipeAmounts-felt",e);
            }

            double multiplier = amount/recipe.serves;

            for(Ingredient ingredient:recipe.ingredients)
            {
                String key = ingredient.unit.toLowerCase()+ingredient.description.toLowerCase();
                if(ingredientMap.containsKey(key))
                {
                    double currentAmount = (Double) ingredientMap.get(key)[0];
                    ingredientMap.get(key)[0] =  Double.parseDouble(ingredient.amount)*multiplier + currentAmount;
                }
                else
                {
                    ingredientMap.put(key, new Object[]{Double.parseDouble(ingredient.amount)*multiplier, ingredient.unit, ingredient.description});
                }
                }
        }


        for(Object[] objects:ingredientMap.values())
        {
            shoppingList.addIngredient((Double) objects[0], (String) objects[1], (String) objects[2]);
        }

        shoppingList.save();
    }
}
