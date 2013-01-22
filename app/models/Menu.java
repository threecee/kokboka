package models;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "my_menu")
public class Menu {

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    public List<RecipeInMenu> recipesInMenu;

    public String title;
    public Date createdAt;
    public List<Date> usedFromDates;


}
