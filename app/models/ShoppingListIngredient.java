package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ShoppingListIngredient extends Model {

    public double amount;
    public String description;
    public String unit;
    public boolean checked;

    @ManyToOne
    public ShoppingList shoppingList;


    public ShoppingListIngredient(ShoppingList shoppingList, double amount, String unit, String description, boolean checked) {
        this.amount = amount;
        this.unit = unit;
        this.description = description;
        this.shoppingList = shoppingList;
        this.checked = checked;
    }

}
