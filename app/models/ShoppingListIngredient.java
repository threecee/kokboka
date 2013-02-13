package models;

import play.Logger;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "my_shopping_list_ingredient")
public class ShoppingListIngredient extends Model {

    public double amount;
    public String description;
    public String unit;
    public boolean checked;


    @Enumerated(EnumType.STRING)
    public ShoppingListItemType type;


    @ManyToOne
    public ShoppingList shoppingList;


    public ShoppingListIngredient(ShoppingList shoppingList, double amount, String unit, String description, ShoppingListItemType type, boolean checked) {
        this.amount = amount;
        this.unit = unit;
        this.description = description;
        this.shoppingList = shoppingList;
        this.checked = checked;
        this.type = type;
    }

    public List<Recipe> findRecipe(Menu menu) {
        List<Recipe> recipes = new ArrayList<Recipe>();
        if(menu != null){
            recipes = RecipeInMenu.find("select rim.recipe from RecipeInMenu rim join rim.recipe.ingredients as i where i.description = ? and rim.menu = ?", description, menu).fetch();
        }

        return recipes;
    }

    public String findRecipeAsString(Menu menu) {
        List<Recipe> recipes = findRecipe(menu);

        String result = "";
        for (int i = 0; i < recipes.size(); i++) {
            result += "" + recipes.get(i).id + ";";
        }

        if (result.endsWith(";")) result = result.substring(0, result.length() - 1);

        return result.trim();
    }

}
