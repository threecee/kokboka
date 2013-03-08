package utils;


import play.mvc.Http;

public class CORSResolver {
    public static void resolve(Http.Response response, Http.Request request) {

        if(request.headers.get("origin") != null)
        {
         response.accessControl(request.headers.get("origin").value(), "GET, POST, OPTIONS, DELETE, PUT" , true);
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        }
    }

}
