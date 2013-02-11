package controllers;

import models.*;
import play.mvc.Before;
import play.mvc.Controller;
import utils.TagUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void index() {
        String user = Security.connected();
        render();
    }
    public static void indexMobile() {
        String user = Security.connected();
        redirect("Recipes.indexMobile", true);
    }



}