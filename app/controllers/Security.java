package controllers;

import models.User;
import play.libs.Crypto;
import play.mvc.Http;
import utils.Device;

public class Security extends Secure.Security {

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
