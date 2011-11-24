package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Ingredient extends Model {

    public String amount;
    public String description;

    @ManyToOne
    public Recipe recipe;

    public Ingredient(Recipe recipe, String amount, String description) {
        this.recipe = recipe;
        this.amount = amount;
        this.description = description;
    }

}