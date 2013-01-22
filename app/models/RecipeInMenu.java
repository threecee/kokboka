package models;


import play.db.jpa.Model;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class RecipeInMenu extends Model {

    public MenuDay usedForDay;

    @ManyToOne
    public Recipe recipe;

    @ManyToOne
    public Menu menu;

    public RecipeInMenu(Menu menu, Recipe recipe, MenuDay usedForDay) {
        this.menu = menu;
        this.recipe = recipe;
        this.usedForDay = usedForDay;
    }
}