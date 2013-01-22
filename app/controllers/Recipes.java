package controllers;


import models.Recipe;
import play.*;
import play.mvc.*;

import java.io.File;


@With(Secure.class)
public class Recipes extends CRUD {
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
