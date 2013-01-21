package controllers;

import models.User;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
    /*    if (User.connect(username, password) == null)
        {
            new User(username, password, "Bob").save();

        }
    */
        return User.connect(username, password) != null;
    }

    static void onDisconnected() {
        Application.index();
    }

    static void onAuthenticated() {
        Admin.index();
    }
}
