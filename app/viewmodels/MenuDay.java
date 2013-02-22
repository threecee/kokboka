package viewmodels;

import models.Recipe;
import utils.DateUtil;

import java.util.Date;

public class MenuDay {
    String dayDate;
    String dayName;
    boolean hasRecipe;

    public MenuDay(Date day, Recipe recipe) {
        dayDate = DateUtil.dateFormatDash.format(day);
        dayName = utils.DateUtil.dayOfWeek(day);
        hasRecipe = recipe != null;
    }


}
