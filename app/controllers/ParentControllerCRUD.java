package controllers;

import flexjson.JSONSerializer;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import utils.CORSResolver;

public class ParentControllerCRUD extends CRUD {
    @Before
    public static void setCORS() {
        CORSResolver.resolve(response, request);
    }

    protected static void renderFlex(Object renderable, JSONSerializer serializer) {
        response.setContentTypeIfNotSet("application/json");
        response.print(serializer.serialize(renderable));

    }


}
