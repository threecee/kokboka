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

    public Date createdAt;

    public Date usedFromDate;

    @OneToOne(cascade = CascadeType.ALL)
    public ShoppingList shoppingList;


    public Menu(User author, Date usedFromDate) {
        this.author = author;
        this.createdAt = new Date();
        this.usedFromDate = usedFromDate;
        this.recipesInMenu = new ArrayList<RecipeInMenu>();
        this.shoppingList = new ShoppingList();

    }

    public Menu addRecipe(Recipe recipe, MenuDay usedForDay) {
        RecipeInMenu newRecipeInMenu = new RecipeInMenu(this, recipe, usedForDay).save();
        this.recipesInMenu.add(newRecipeInMenu);
        this.save();
        return this;

    }

    public Recipe getRecipeForDay(int day) {
        MenuDay menuDay = MenuDay.values()[day];
        for (RecipeInMenu recipeInMenu : recipesInMenu) {
            if (recipeInMenu.usedForDay.compareTo(menuDay) == 0) {
                return recipeInMenu.recipe;
            }
        }
        return null;
    }

    public int getDayOfRecipe(Recipe recipe) {
        for (int i = 0; i < recipesInMenu.size(); i++) {
            if (recipesInMenu.get(i).recipe.id == recipe.id) {
                return i;
            }
        }
        return -1;
    }

    public void deleteRecipeForDay(int day) {
        deleteRecipeForDay(MenuDay.values()[day]);
    }

    public void deleteRecipeForDay(MenuDay menuDay) {
        for (RecipeInMenu recipeInMenu : recipesInMenu) {
            if (recipeInMenu.usedForDay.compareTo(menuDay) == 0) {
                recipesInMenu.remove(recipeInMenu);
                recipeInMenu.delete();
                save();
                break;
                //recipeInMenu.delete();

            }
        }
    }


    public Menu getNextWeeksMenu(User user) {
        return getNextWeeksMenu(user, this);
    }

    public Menu getLastWeeksMenu(User user) {
        return getLastWeeksMenu(user, this);
    }


    public static Menu getNextWeeksMenu(User user, Menu thisWeeksMenu) {
        Date newDate = utils.DateUtil.addDays(thisWeeksMenu.usedFromDate, 7);
        return getMenu(user, newDate);
    }

    public static Menu getLastWeeksMenu(User user, Menu thisWeeksMenu) {
        Date newDate = utils.DateUtil.addDays(thisWeeksMenu.usedFromDate, -7);
        return getMenu(user, newDate);
    }

    public static Menu getMenu(User user, Date startingDay) {
        Menu menu = null;

        if (startingDay != null) {
            menu = Menu.find("author = ? and usedFromDate = ?", user, startingDay).first();

            if (menu == null)
                menu = new Menu(user, startingDay).save();
        }
        return menu;
    }

    public void deleteRecipe(Recipe recipe) {
        for (RecipeInMenu recipeInMenu : recipesInMenu) {
            if (recipeInMenu.recipe.id == recipe.id) {
                recipesInMenu.remove(recipeInMenu);
                recipeInMenu.delete();
                save();
                break;

            }
        }
    }
}
