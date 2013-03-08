package controllers;


import models.Ingredient;
import models.Tag;
import play.*;
import play.mvc.*;

import java.util.List;

@With(Secure.class)
public class Tags extends ParentControllerCRUD {

    public static void autocompleteTags(String term){

        List<String> tags = Tag.find("select distinct(i.name) from Tag i where lower(i.name) like ? order by i.name", term.toLowerCase() + "%").fetch();


        renderJSON(tags);
    }



}
