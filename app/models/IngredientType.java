package models;


import play.db.jpa.Model;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "my_ingredientype")
public class IngredientType extends Model {

    public String name;

    @ElementCollection
    @CollectionTable(name = "descriptions")
    public List<String> descriptions;

    public IngredientType(String name) {
        this.name = name;
        descriptions = new ArrayList<String>();
    }

    public void addIngredientDescription(String description) {
        if (description != null && !descriptions.contains(description)) {
            descriptions.add(description);
            save();
        }
    }

    public static IngredientType findForDescription(String description) {
        return find("select distinct p from IngredientType p join p.descriptions as t where t = ?", description).first();
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (String description : descriptions) {
            List<Ingredient> newIngredients = Ingredient.find("description = ?", description).fetch();
            ingredients.addAll(newIngredients);
        }
        return ingredients;
    }

    public static String asCommaSeparatedList() {
        List<String> tags = find("select DISTINCT(i.name) from IngredientType i order by i.name desc").fetch();

        String result = "[";
        for (String tag : tags) {
            result += "\"" + tag + "\", ";
        }
        if (tags.size() > 0) {
            result = result.substring(0, result.length() - 2);
        }

        result += "]";

        return result;
    }

    public static void addIngredient(String description, String typeName) {
        IngredientType ingredientType = find("name = ?", typeName.toLowerCase()).first();

        if (ingredientType == null) {
            ingredientType = new IngredientType(typeName).save();
        }

        if (!ingredientType.descriptions.contains(description)) {
            ingredientType.descriptions.add(description);
            ingredientType.save();
        }

    }

    public static void removeIngredient(String description, String typeName) {
        IngredientType ingredientType = find("name = ?", typeName.toLowerCase()).first();

        if (ingredientType != null) {

            if (ingredientType.descriptions.contains(description)) {
                ingredientType.descriptions.remove(description);
                ingredientType.save();
            }
        }
    }

    public String toArrayString()
    {
        return id + "";

    }
}
