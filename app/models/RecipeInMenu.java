package models;


import play.db.jpa.Model;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "my_recipe_in_menu")
public class RecipeInMenu extends Model {

    public MenuDay usedForDay;

    @ManyToOne
    public Recipe recipe;

    @ManyToOne
    public Menu menu;

    public Double amount;

    public RecipeInMenu(Menu menu, Recipe recipe, MenuDay usedForDay) {
        this.menu = menu;
        this.recipe = recipe;
        this.usedForDay = usedForDay;
    }


    public static List<Recipe> findUsedInMenu(User author) {
        return RecipeInMenu.find(
                "select distinct(p.recipe) from RecipeInMenu p join p.menu as m where m.author = (:author)"
        ).bind("author", author).fetch();
    }

}
