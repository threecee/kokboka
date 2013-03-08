package controllers;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import utils.CORSResolver;

public class ParentControllerCRUD extends CRUD {
    @Before
    public static void setCORS() {
        CORSResolver.resolve(response, request);
    }



}
