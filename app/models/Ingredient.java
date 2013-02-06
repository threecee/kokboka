package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
@Table(name = "my_ingredient")
public class Ingredient extends Model {

    public String amount;
    public String description;
    public String unit;

    @ManyToOne
    public Recipe recipe;


    @ManyToOne
    public IngredientType ingredientType;

    public Ingredient(Recipe recipe, String amount, String unit, String description) {
        this.recipe = recipe;
        this.amount = amount;
        this.unit = unit;
        this.description = description;
    }

}