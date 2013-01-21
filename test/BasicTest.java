import controllers.Parsers;
import models.Ingredient;
import models.Recipe;
import models.Tag;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicTest extends UnitTest {

    @Before
    public void init() {
        //Fixtures.loadModels("data.yml");

        Fixtures.deleteAllModels();
    }

    @Test
    public void createAndRetrieveUser() {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save();

        // Retrieve the user with e-mail address bob@gmail.com
        User bob = User.find("byEmail", "bob@gmail.com").first();

        // Test
        assertNotNull(bob);
        assertEquals("Bob", bob.fullname);
    }


    @Test
    public void parseRecipe(){
        new User("bob2@gmail.com", "secret", "Bob").save();

        // Retrieve the user with e-mail address bob@gmail.com
        User bob = User.find("byEmail", "bob2@gmail.com").first();
        // new Parsers().parseRecipe(bob, "http://www.rema.no/under100/Kampanjer/Wok_Bangkok_style/article66229.ece");

    }

    @Test
    public void tryConnectAsUser() {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save();

        // Test
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }

    @Test
    public void createAndRetrieveRecipe() {

        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Create a new recipe and save it

        ArrayList<String> ingredients = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        new Recipe(bob, "Tofu", "info om tofu", "bake kake søte", "http://www.dinmat.no", 2, "liter").save();

        Recipe rep = Recipe.find("byTitle", "Tofu").first();

        // Test
        assertNotNull(rep);
        // assertEquals("Bob", bob.fullname);
    }


    @Test
    public void createRecipe() {

        User bob = new User("bob@gmail.com", "secret", "Bob").save();
        Recipe rep = new Recipe(bob, "Kake", "info om kake", "bake smake kake", "http://www.dinmat.no", 20, "stk").save();

        rep.addIngredient("1", "stk", "agurk").save();
        rep.addIngredient("2", "stk", "sitroner").save();

        // Retrieve all comments
        List<Ingredient> tofuIngredients = Ingredient.find("byRecipe", rep).fetch();

        // Tests
        assertEquals(2, tofuIngredients.size());

        Ingredient firstIngredient = tofuIngredients.get(0);
        assertNotNull(firstIngredient);
        assertEquals("1", firstIngredient.amount);
        assertEquals("agurk", firstIngredient.description);

        Ingredient secondIngredient = tofuIngredients.get(1);
        assertNotNull(secondIngredient);
        assertEquals("2", secondIngredient.amount);
        assertEquals("sitroner", secondIngredient.description);
    }


    @Test
    public void testTags() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Create a new post
        Recipe rep = new Recipe(bob, "Kake", "info om kake", "bake smake kake", "http://www.dinmat.no", 2, "stk").save();
        Recipe rep2 = new Recipe(bob, "Saft", "info om saft", "drikke saft", "http://www.dinmat.no", 10, "liter").save();

        // Well
        assertEquals(0, Recipe.findTaggedWith("Red").size());

        // Tag it now
        rep.tagItWith("Red").tagItWith("Blue").save();
        rep2.tagItWith("Red").tagItWith("Green").save();

        // Check
        assertEquals(2, Recipe.findTaggedWith("Red").size());
        assertEquals(1, Recipe.findTaggedWith("Blue").size());
        assertEquals(1, Recipe.findTaggedWith("Green").size());

        assertEquals(1, Recipe.findTaggedWith("Red", "Blue").size());
        assertEquals(1, Recipe.findTaggedWith("Red", "Green").size());
        assertEquals(0, Recipe.findTaggedWith("Red", "Green", "Blue").size());
        assertEquals(0, Recipe.findTaggedWith("Green", "Blue").size());

        List<Map> cloud = Tag.getCloud();
        assertEquals(
                "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]",
                cloud.toString()
        );

    }

}
