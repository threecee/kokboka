package controllers;


import models.Menu;
import models.MenuDay;
import models.Recipe;
import models.User;
import play.mvc.Before;
import play.mvc.With;
import utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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

    public static void uke(int uke) {

        Date startDag = DateUtil.getStartingDayForWeek(uke);

        User user = User.find("byEmail", Security.connected()).first();
        Menu menu = Menu.find("user = ? and usedForDate = ?", user, startDag).first();
        if (menu == null) {
            menu = new Menu(user, startDag).save();
        }
        show(menu.id);
    }

    public static void showInMenu(Long menuId, int index) {
        renderShowInMenu(menuId, index, false);
    }

    public static void showInMenuMobile(Long menuId, int index) {
        renderShowInMenu(menuId, index, true);
    }

    private static void renderShowInMenu(Long menuId, int index, boolean isMobile) {
        User user = User.find("byEmail", Security.connected()).first();
        Menu menu = Menu.findById(menuId);
        Recipe recipe = menu.getRecipeForDay(index);
        boolean favorite = user.favorites.contains(recipe);
        if (isMobile) {
            render("Recipes/showMobile.html", user, recipe, favorite, menu);

        } else {
            render("Recipes/show.html", user, recipe, favorite, menu);
        }

    }

    public static void showNextWeek(Long id) {
        renderShowNextWeek(id, false);
    }

    public static void showNextWeekMobile(Long id) {
        if(id == null)
        {
           int uke = DateUtil.weekOfYear(new Date());
            Date startDag = DateUtil.getStartingDayForWeek(uke);

            User user = User.find("byEmail", Security.connected()).first();
            Menu menu = Menu.find("author = ? and usedFromDate = ?", user, startDag).first();
            if (menu == null) {
                menu = new Menu(user, startDag).save();
            }
           id = menu.id;
        }

        renderShowNextWeek(id, true);
    }

    public static void showThisWeekMobile() {
           int uke = DateUtil.weekOfYear(new Date());
            Date startDag = DateUtil.getStartingDayForWeek(uke);

            User user = User.find("byEmail", Security.connected()).first();
            Menu menu = Menu.find("author = ? and usedFromDate = ?", user, startDag).first();
            if (menu == null) {
                menu = new Menu(user, startDag).save();
            }

        render("Menus/showMobile.html", menu);
    }


    private static void renderShowNextWeek(Long id, boolean isMobile) {
        Menu thismenu = Menu.findById(id);
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.findById(thismenu.getNextWeeksMenu(user).id);

        if (isMobile) {
            render("Menus/showMobile.html", menu);

        } else {
            render("Menus/show.html", menu);
        }
    }

    public static void showLastWeek(Long id) {
        renderShowLastWeek(id, false);
    }

    public static void showLastWeekMobile(Long id) {
        renderShowLastWeek(id, true);
    }

    private static void renderShowLastWeek(Long id, boolean isMobile) {
        Menu thismenu = Menu.findById(id);
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.findById(thismenu.getLastWeeksMenu(user).id);

        if (isMobile) {
            render("Menus/showMobile.html", menu);

        } else {
            render("Menus/show.html", menu);
        }

    }


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static void unplanrecipefrommenu(Long menuId, Long recipeId) throws ParseException {
        Menu menu = Menu.findById(menuId);
        Recipe recipe = Recipe.findById(recipeId);

        menu.deleteRecipe(recipe);
    }


    public static void planrecipe(Long recipeId, String day) throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.findById(user.activeMenu.getId());

        Date useDate = dateFormat.parse(day);
        int distance = DateUtil.distance(menu.usedFromDate, useDate);

        Recipe recipe = Recipe.findById(recipeId);

        menu.deleteRecipeForDay(distance);
        menu.addRecipe(recipe, MenuDay.values()[distance]);

        menu.save();

    }

    public static void planrecipeByDay(Long recipeId, String day) throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        int dayInt = Integer.parseInt(day);

        Menu menu = Menu.findById(user.activeMenu.getId());

        Recipe recipe = Recipe.findById(recipeId);

        menu.deleteRecipeForDay(dayInt);
        menu.addRecipe(recipe, MenuDay.values()[dayInt]);

        menu.save();

    }

    public static void unplanrecipe(String day) throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = Menu.findById(user.activeMenu.getId());

        Date useDate = dateFormat.parse(day);
        int distance = DateUtil.distance(menu.usedFromDate, useDate);

        menu.deleteRecipeForDay(distance);
    }

    public static void dinnerplan(String fromDate) throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        Menu menu = null;

        initMenu(user, fromDate);

        if (user != null && user.activeMenu != null) {
            menu = Menu.findById(user.activeMenu.getId());
        }

        render(menu);
    }

    public static void dinnerplanMobile() throws ParseException {
        User user = User.find("byEmail", Security.connected()).first();

        //   Recipe newrecipe = Recipe.findById(recipeId);

        Menu menu = null;

        initMenu(user, null);

        if (user != null && user.activeMenu != null) {
            menu = Menu.findById(user.activeMenu.getId());
        }

        render(menu);
    }


    private static void initMenu(User user, String startingDayString) throws ParseException {
        Menu menu = null;

        if (startingDayString != null) {
            Date startingDay = dateFormat.parse(startingDayString);
            menu = Menu.find("author = ? and usedFromDate = ?", user, startingDay).first();

            if (menu == null)
                menu = new Menu(user, startingDay).save();
        } else {
            if (user.activeMenu != null) {
                menu = Menu.findById(user.activeMenu.getId());
            } else {
                //if (menu == null || (DateUtil.distance(DateUtil.getStartingDay(), menu.usedFromDate) < 0)) {

                menu = Menu.find("author = ? and usedFromDate = ?", user, DateUtil.getStartingDay()).first();
                if (menu == null) {
                    menu = new Menu(user, DateUtil.getStartingDay()).save();
                }
            }
        }
        user.activeMenu = menu;
        user.save();
    }

    public static void delete(Long id) {
        Menu.findById(id)._delete();
    }

    public static void show(Long id) {
        Menu menu = Menu.findById(id);
        render(menu);
    }


    public static void index() {
        indexRender(false);
    }

    public static void indexMobile() {
        indexRender(false);
    }

    public static void historikkMobile() {
        historikkRender(true);
    }

    private static void historikkRender(boolean isMobile) {
        List<Menu> menus = Menu.find("order by usedFromDate desc").fetch();
        if (isMobile) {
            render("Menus/historikkMobile.html", menus);
        }
    }


    private static void indexRender(boolean isMobile) {
        List<Menu> menus = Menu.find("order by usedFromDate desc").fetch();

        if (isMobile) {
            render("Menus/indexMobile.html", menus);
        } else {
            render(menus);
        }
    }


}
