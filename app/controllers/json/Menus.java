package controllers.json;


import controllers.ParentControllerCRUD;
import controllers.Secure;
import controllers.Security;
import flexjson.JSONSerializer;
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
public class Menus extends ParentControllerCRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void getUke(int uke) {

        Date startDag = DateUtil.getStartingDayForWeek(uke);

        User user = User.find("byEmail", Security.connected()).first();
        Menu menu = Menu.find("author = ? and usedFromDate = ?", user, startDag).first();
        if (menu == null) {
            menu = new Menu(user, startDag).save();
        }
        JSONSerializer serializer = new JSONSerializer().include("recipesInMenu").include("recipesInMenu.menu").include("recipesInMenu.recipe.tags").include("shoppingList");

         renderFlex(menu, serializer);


    }


    public static void getAll() {
        List<Menu> menus = Menu.find("order by usedFromDate desc").fetch();
        JSONSerializer serializer = new JSONSerializer().include("recipesInMenu").include("recipesInMenu.menu").include("recipesInMenu.recipe.tags").include("shoppingList");

         renderFlex(menus, serializer);

    }



}
