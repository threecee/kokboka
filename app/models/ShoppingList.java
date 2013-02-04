package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "my_shopping_list")
public class ShoppingList extends Model {



    @OneToMany
    public List<ShoppingListIngredient> shoppingListIngredients;

    public ShoppingList() {
    }

    public ShoppingList addIngredient(double amount, String unit, String description) {
        ShoppingListIngredient newIngredient = new ShoppingListIngredient(this, amount, unit, description, ShoppingListItemType.ingredient, false).save();
        this.shoppingListIngredients.add(newIngredient);
        this.save();
        return this;
    }
    public ShoppingListIngredient addOnTheFly(String description) {
        ShoppingListIngredient newIngredient = new ShoppingListIngredient(this, 0, null, description, ShoppingListItemType.onthefly, false).save();
        this.shoppingListIngredients.add(newIngredient);
        this.save();
        return newIngredient;
    }


}
