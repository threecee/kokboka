package models;


import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "my_menu")
public class Menu extends Model {

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    public List<RecipeInMenu> recipesInMenu;

    @ManyToOne
    public User author;

    public String title;
    public Date createdAt;

    @ElementCollection
    public List<Date> usedFromDates;


    public Menu(User author, String title) {
        this.author = author;
        this.title = title;
        this.createdAt = new Date();
        usedFromDates = new ArrayList<Date>();

    }

    public Menu addRecipe(Recipe recipe, MenuDay usedForDay) {
        RecipeInMenu newRecipeInMenu = new RecipeInMenu(this, recipe, usedForDay).save();
        this.recipesInMenu.add(newRecipeInMenu);
        this.save();
        return this;

    }

    public Menu addUsedFromDate(Date usedFromDate) {
        this.usedFromDates.add(usedFromDate);
        this.save();
        return this;

    }
}
