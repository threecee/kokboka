package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class ShoppingList extends Model {


    @OneToOne
    public Menu menu;

    @OneToMany
    public List<ShoppingListIngredient> shoppingListIngredients;

    public ShoppingList(Menu menu) {
        this.menu = menu;
    }

    public ShoppingList addIngredient(double amount, String unit, String description) {
        ShoppingListIngredient newIngredient = new ShoppingListIngredient(this, amount, unit, description, false).save();
        this.shoppingListIngredients.add(newIngredient);
        this.save();
        return this;
    }

}
