package models;

import flexjson.JSON;
import org.hibernate.annotations.ManyToAny;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "my_user")
public class User extends Model {

    public String email;

    public String password;

    public String fullname;
    public boolean isAdmin;
    public Double preferredServings;

    @ManyToOne(cascade = CascadeType.ALL)
    public Menu activeMenu;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Recipe> favorites;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<ShoppingListIngredient> favoriteIngredients;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        favorites = new ArrayList<Recipe>();
        favoriteIngredients = new ArrayList<ShoppingListIngredient>();
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

    public User addFavorite(Recipe recipe)
    {
        if(!favorites.contains(recipe))
        {
            favorites.add(recipe);
            save();
        }
        return this;
    }
    public User removeFavorite(Recipe recipe)
    {
        if(favorites.contains(recipe))
        {
            favorites.remove(recipe);
            save();
        }
        return this;
    }
    public ShoppingListIngredient addIngredient(double amount, String unit, String description) {
        ShoppingListIngredient newIngredient = new ShoppingListIngredient(null, amount, unit, description, ShoppingListItemType.userpreference, false).save();
        this.favoriteIngredients.add(newIngredient);
        this.save();
        return newIngredient;
    }
    public User removeIngredient(Long id) {
        ShoppingListIngredient ingredient = ShoppingListIngredient.findById(id);
        this.favoriteIngredients.remove(ingredient);
        this.save();
        ingredient.delete();
        return this;
    }

}
