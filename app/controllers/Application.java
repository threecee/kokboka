package controllers;

import models.User;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import utils.Device;

import java.util.ArrayList;

public class Application extends ParentController {

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
        if (Device.isMobile(request)) {
            redirect("Recipes.indexMobile", true);
        } else {
            render();
        }
    }

    public static void indexMobile() {
        String user = Security.connected();
        redirect("Recipes.indexMobile", true);
    }


}