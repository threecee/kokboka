import controllers.Parsers;
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
        User calle;

        String email = "carl.christian.christensen@gmail.com";
        calle = User.find("byEmail", email).first();

        if(calle == null)
        {
          calle  = new User(email, "secret", "Calle").save();

        }


    //    new Parsers().parseRecipe(calle, "http://www.rema.no/under100/Kampanjer/Wok_Bangkok_style/article66229.ece");

    }

}