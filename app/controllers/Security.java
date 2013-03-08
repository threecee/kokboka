package controllers;

import models.User;
import play.mvc.Before;
import utils.CORSResolver;

public class Security extends Secure.Security {

    @Before
    public static void setCORS() {
        CORSResolver.resolve(response, request);
    }


    static boolean authenticate(String username, String password) {

        return User.connect(username, password) != null;
    }

    static void onDisconnected() {
        sendTo();
    }


    static void onAuthenticated() {
       sendTo();
    }


    static void sendTo()
    {
        String url = flash.get("url");
        if(url == null) {
            Application.index();
        }
        else
        {
            redirect(url);
        }

    }
}
