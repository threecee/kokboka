package controllers;


import models.*;
import play.mvc.Before;
import play.mvc.With;
import utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@With(Secure.class)
public class Menus extends CRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void planrecipe(Long recipeId, String day) throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        initMenu(user);
        Menu menu = user.activeMenu; //getMenu(user);

        Date useDate = dateFormat.parse(day);
        int distance = DateUtil.distance(menu.usedFromDate, useDate);

        Recipe recipe = Recipe.findById(recipeId);

        menu.addRecipe(recipe, MenuDay.values()[distance]);

        menu.save();
    }
    public static void unplanrecipe(String day) throws ParseException {
         User user = User.find("byEmail", Security.connected()).first();

         initMenu(user);
         Menu menu = user.activeMenu; //getMenu(user);

        Date useDate = dateFormat.parse(day);
         int distance = DateUtil.distance(menu.usedFromDate, useDate);

         menu.deleteRecipeForDay(distance);
     }

    public static void dinnerplan() {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu;
        initMenu(user);
        //menu = new Menu(user, getStartingDay()).save();

        //  menu = getMenu(user);
        menu = Menu.findById(user.activeMenu.getId());
        render(menu);
    }

    private static void initMenu(User user) {
        Menu menu;

        if (user.activeMenu == null) {
            menu = new Menu(user, getStartingDay()).save();
            user.activeMenu = menu;
            user.save();
        }
    }


    private static Date getStartingDay() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.WEEK_OF_YEAR, 1);

        return cal.getTime();
    }

}
