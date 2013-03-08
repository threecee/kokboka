package controllers;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import utils.CORSResolver;

import java.util.ArrayList;

public class ParentController extends Controller {
    @Before
    public static void setCORS() {
        CORSResolver.resolve(response, request);


    }


}
