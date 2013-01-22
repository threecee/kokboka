package models;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
public class RecipeInMenu {

    public Days usedForDay;
    public Recipe recipe;
    public Menu menu;

}
