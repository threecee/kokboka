package controllers;


import models.Recipe;
import play.mvc.*;


@With(Secure.class)
public class Menus extends CRUD {
    public static void recipePhoto(long id) {
       final Recipe recipe = Recipe.findById(id);
       notFoundIfNull(recipe);
        notFoundIfNull(recipe.photo);
        response.setContentTypeIfNotSet(recipe.photo.type());
       renderBinary(recipe.photo.get());
    }
    public static void recipePhotoThumb(long id) {
           final Recipe recipe = Recipe.findById(id);
           notFoundIfNull(recipe);
           notFoundIfNull(recipe.photoThumb);
           response.setContentTypeIfNotSet(recipe.photoThumb.type());
           renderBinary(recipe.photoThumb.get());
        }

    public static boolean recipeHasPhoto(long id) {
       final Recipe recipe = Recipe.findById(id);
        return(recipe != null && recipe.photo != null);

    }
}
