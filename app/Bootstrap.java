import controllers.Parsers;
import org.apache.log4j.Level;
import org.h2.jdbc.JdbcCallableStatement;
import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if(Recipe.count() == 0) {
            //Fixtures.loadModels("initial-data.yml");
            setupDb();
        }
    }

    private void setupDb()
    {
        User calle = null;

        String email = "carl.christian.christensen@gmail.com";

        try{
        calle = User.find("byEmail", email).first();
        } catch (Exception e)
        {
            Logger.error("Feil ved henting av bruker under bootstrap",e);
        }

        if(calle == null)
        {
          calle  = new User(email, "secret", "Calle").save();
                  calle.isAdmin = true;
         calle.save();


        }

    //    new Parsers().parseRecipe(calle, "http://www.rema.no/under100/Kampanjer/Wok_Bangkok_style/article66229.ece");

    }

}