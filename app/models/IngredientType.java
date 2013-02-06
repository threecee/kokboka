package models;


import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "my_ingredientype")
public class IngredientType extends Model {

    public String name;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Ingredient> ingredients;

    public static IngredientType getType(String description)
    {
        return Ingredient.find("where description = ?", description).first();
    }
}
