package viewmodels;


import models.Recipe;
import utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menu {
    Long id;
    Date usedFromDate;
    String usedFromDateString;
    String heading;
    List<MenuDay> days;


    public Menu(models.Menu modelMenu) {
        id = modelMenu.id;
        usedFromDate = modelMenu.usedFromDate;
        usedFromDateString = DateUtil.dateFormatDash.format(modelMenu.usedFromDate);
        heading = "Uke " + utils.DateUtil.weekOfYear(modelMenu.usedFromDate);

        days = new ArrayList<MenuDay>();
        for (int i = 0; i < 7; i++) {
            days.add(new MenuDay(utils.DateUtil.addDays(modelMenu.usedFromDate, i), modelMenu.getRecipeForDay(i)));
        }
    }
}

