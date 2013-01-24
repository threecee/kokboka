package controllers;


import models.*;
import play.*;
import play.mvc.*;
import utils.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;


@With(Secure.class)
public class Recipes extends CRUD {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void showCurrent()
     {
         User user = User.find("byEmail", Security.connected()).first();

         Menu menu = Menu.find("usedFromDate = ?", DateUtil.getStartingDayThisWeek(new Date())).first();

         if(menu != null)
         {
         int distance = DateUtil.distance(menu.usedFromDate, new Date());

         Recipe recipe = menu.getRecipeForDay(distance);

         render("Application/show.html", recipe);
         }
         render("Application/show.html");
     }


    public static void recipePhoto(long id) {
       final Recipe recipe = Recipe.findById(id);
       try{
        response.setContentTypeIfNotSet(recipe.photo.type());
        renderBinary(recipe.photo.getFile());
       }
        catch (NullPointerException e){
          renderBinary(new File("public/images/transparent.gif"));
       }

    }
    public static void recipePhotoThumb(long id) {
           final Recipe recipe = Recipe.findById(id);
        try{
           response.setContentTypeIfNotSet(recipe.photoThumb.type());
           renderBinary(recipe.photoThumb.getFile());
        }
         catch (NullPointerException e){
           renderBinary(new File("public/images/food-plate-icons.jpg"));
        }
        }

    public static boolean recipeHasPhoto(long id) {
       final Recipe recipe = Recipe.findById(id);
        return(recipe != null && recipe.photo != null);

    }
}
