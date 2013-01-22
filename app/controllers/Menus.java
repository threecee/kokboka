package controllers;


import models.Recipe;
import play.mvc.With;


@With(Secure.class)
public class Menus extends CRUD {
}
