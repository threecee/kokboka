package controllers;


import models.*;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import play.Logger;
import play.libs.WS;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@With(Secure.class)
public class Parsers extends Controller {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            user.save();
            if (user != null)
                renderArgs.put("user", user.fullname);
        }
    }

    public static void index() {
        String user = Security.connected();
        render();
    }


    private static Document getDocument(String url) {

        String documentAsString = "<body/>";
        try {
            CleanerProperties props = new CleanerProperties();

            TagNode tagNode = htmlCleaner.clean(new URL(url));

            // serialize to xml file
            documentAsString = htmlSerializer.getAsString(tagNode);

        } catch (Exception e) {
            Logger.error("Kunne ikke hente og xml-balansere side " + url);
        }
        return XML.getDocument(documentAsString);
    }

    final static CleanerProperties props = new CleanerProperties();
    final static HtmlCleaner htmlCleaner = new HtmlCleaner(props);
    final static PrettyXmlSerializer htmlSerializer =
            new PrettyXmlSerializer(props);


    private static void reporter(String report) {

        response.writeChunk(report + "<br/>");
        Logger.info(report);
    }


    public static void ryddIFremgangsmate() {
        List<Recipe> recipes = Recipe.find("order by title desc").fetch();

        for (Recipe recipe : recipes) {
            recipe.steps = cleanSteps(recipe.steps);
            recipe.save();
        }
    }

    static Pattern cleanLonelyNumbers = Pattern.compile("^(\\s*[0-9]+)");
    static Pattern cleanLeadingSpace = Pattern.compile("^\\s*");
    static Pattern cleanDoubleLineBreaks = Pattern.compile("\\s*\\n\\s*\\n\\s*\\n", Pattern.MULTILINE);
    private static String cleanSteps(String steps) {
        String original = steps;

        steps = runMatcherPerLine(cleanLeadingSpace, steps, "");
        steps = runMatcherPerLine(cleanLonelyNumbers, steps, "\n");
        steps = runMatcherPerLine(cleanLeadingSpace, steps, "");
        steps = runMatcher(cleanDoubleLineBreaks, steps, "\n\n");
        steps = runMatcherPerLine(cleanLeadingSpace, steps, "");


        reporter("Byttet ut: ------------------\n" +  original);
        reporter("med : ------------------\n" +  steps);
        reporter("------------------\n");

        return steps;
    }


    private static String runMatcherPerLine(Pattern pattern, String source, String replace)
    {
        String[] sourceLines = source.split("\n");
        StringBuffer result = new StringBuffer();

        for(String sourceLine: sourceLines)
        {
             String resultLine = runMatcher(pattern, sourceLine, replace);
            result.append(resultLine + "\n");
        }

         return result.toString();
    }

    private static String runMatcher(Pattern pattern, String source, String replace)
    {

        Matcher matcher = pattern.matcher(source);
        StringBuffer myStringBuffer = new StringBuffer();
        while (matcher.find())
        {
                matcher.appendReplacement(myStringBuffer, replace);
        }
            matcher.appendTail(myStringBuffer);
        return myStringBuffer.toString();
    }


    public static void ryddIIngredienser() {
        List<Ingredient> ingredients = Ingredient.find("order by description desc").fetch();

        for (Ingredient ingredient : ingredients) {
            ingredient.description = cleanProductNames(ingredient.description);
            ingredient.save();
            convertPackageToWeight(ingredient);
        }
    }

    public static void ryddIIngredienserMaksi() {
        List<Ingredient> ingredients = Ingredient.find("order by description desc").fetch();

        for (Ingredient ingredient : ingredients) {
            ingredient.description = cleanProductNames(ingredient.description);
            ingredient.description = ingredient.description.toLowerCase();
            ingredient.save();
            convertPackageToWeight(ingredient);
            convertPackageToWeightRydding(ingredient);
        }
    }
    public static void ryddIIngrediensTyper() {
        List<IngredientType> ingredienttypes = IngredientType.findAll();

        for (IngredientType ingredientType : ingredienttypes) {
            ingredientType.name = ingredientType.name.toLowerCase();

            for(int i = 0; i< ingredientType.descriptions.size(); i++)
            {
                ingredientType.descriptions.set(i, ingredientType.descriptions.get(i).toLowerCase());
            }
            ingredientType.save();
        }
    }

    public static void importRema1000(boolean overskriving) {
        String urlTemplate = "http://www.rema.no/under100/?service=oppskrifter&allRecipes=true&page=";

        response.setContentTypeIfNotSet("text/html");
        response.writeChunk("<html><body>");

        int page = 1;
        boolean morePages = true;
        while (morePages) {

            Document recipeDocument = getDocument(urlTemplate + page);

            ArrayList<String> recipeUrls = (findRecipeUrls(recipeDocument));
            morePages = recipeUrls.size() > 0;
            page++;

            for (final String url : recipeUrls) {
                Recipe recipe = Recipe.find("bySource", url).first();

                if (recipe == null) {
                    String result = parseRema1000Recipe(url);
                    reporter(result);
                } else if (overskriving) {
                    String result = parseRema1000Recipe(url, recipe);
                    reporter(result);
                }

            }


        }


        importRema1000Tags();
        index();
    }

    private static ArrayList<String> findRecipeUrls(Document recipeDocument) {
        ArrayList<String> recipeUrls = new ArrayList<String>();
        for (Node event : XPath.selectNodes("//div[@class='recipeListItem']/div/div/div/a", recipeDocument)) {
            String url = XPath.selectText("@href", event);
            if (url != null && !url.isEmpty()) {
                recipeUrls.add(url);
                reporter("Added " + url + " to recipe queue");
            }
        }
        return recipeUrls;
    }

    private static void importRema1000Tags() {
        String urlTemplate = "http://www.rema.no/under100/?service=oppskrifter&allRecipes=true&page=1";

        Document recipeDocument = getDocument(urlTemplate);

        for (Node event : XPath.selectNodes("//div[@id='tags']/div/ul/li", recipeDocument)) {
            tagRecipes(event);
        }

        for (Node event : XPath.selectNodes("//div[@id='campaign_archive_container']/ul/li", recipeDocument)) {
            tagRecipes(event);
        }
    }

    private static void tagRecipes(Node event) {
        String urlTemplate = "&page=";
        String url = XPath.selectText("a/@href", event);
        String name = XPath.selectText("a/span", event);
        List<Node> nameNodes = XPath.selectNodes("a/span", event);
        if (name != null && name.trim().isEmpty()) {
            name = nameNodes.get(0).getChildNodes().item(2).getTextContent();
        }
        ArrayList<String> tagUrls;
        if (url != null && name != null && !url.isEmpty() && !name.isEmpty()) {
            url = url.replaceAll(" ", "%20");
            name = name.trim();

            if (isApproved(name)) {

                int page = 1;
                boolean morePages = true;
                while (morePages) {

                    Document tagDocument = getDocument(url + urlTemplate + page);

                    tagUrls = findRecipeUrls(tagDocument);
                    morePages = tagUrls.size() > 0;
                    page++;

                    for (String tagUrl : tagUrls) {
                        Recipe recipe = Recipe.find("bySource", tagUrl).first();
                        if (recipe != null) {
                            recipe.tagItWith(name);
                            recipe.save();
                            reporter("Tagget '" + recipe.title + "' med '" + name + "'");
                        }
                    }
                }
            }

        }
    }

    private static String[] noGoNames = new String[]{"5 på topp", "Fri for gluten og melk", "Litt over hundrelappen", "Fri for melk", "10 på topp", "Raske middager i julestria", "Fri for gluten"};

    private static boolean isApproved(String name) {
        for (String noGoName : noGoNames) {
            if (name.compareToIgnoreCase(noGoName) == 0) return false;
        }
        return true;

    }

    private static String parseRema1000Recipe(String url) {
        return parseRema1000Recipe(url, null);

    }

    private static String parseRema1000Recipe(String url, Recipe recipe) {

        //determine support

        // get recipe
        reporter("Retrieveing url: " + url);
        Document recipeDocument = getDocument(url);  //WS.url(url).get().getString();

        if (recipe == null) {
            recipe = parseRema1000(url, recipeDocument);
        } else {
            parseRema1000(url, recipeDocument, recipe);
        }

        if (recipe != null) {
            recipe.save();

            return "Successfully parsed " + recipe.title + " from Rema 1000";
        }
        return "Could not parse " + recipe.title + " from Rema 1000";
    }

    private static Recipe parseRema1000(String url, Document recipeDocument) {
        return parseRema1000(url, recipeDocument, null);
    }

    private static Recipe parseRema1000(String url, Document recipeDocument, Recipe recipe) {


        String title = XPath.selectText("//div[@id='recipe']/div[@class='rightColumn']/h2", recipeDocument);
        title = title.replaceAll(" +\t+ +\n+ +", " ");


        String description = XPath.selectText("//div[@id='recipe']/div[@class='leftColumn']/div[@id='ingress']/p", recipeDocument);

        String steps = "";

        for (Node event : XPath.selectNodes("//div[@id='recipe']/div[@class='leftColumn']/div[@id='fremgangsmote']/div[@class='stepByStepItem']", recipeDocument)) {

            steps = steps + getStep(event);
        }
        for (Node event : XPath.selectNodes("//div[@id='recipe']/div[@class='leftColumn']/div[@id='fremgangsmote']/div[@class='stepByStepItem_noImage']", recipeDocument)) {

            steps = steps + getStep(event);
        }


        String source = url;

        double serves = 2;
        try {

            String adultRangeString = XPath.selectText("//div[@id='recipe']/div[@class='rightColumn']/div/span[@id='adultRange']", recipeDocument);
            String childRangeString = XPath.selectText("//div[@id='recipe']/div[@class='rightColumn']/div/span[@id='childRange']", recipeDocument);

            serves = Integer.parseInt(adultRangeString) + Integer.parseInt(childRangeString) / 2;

        } catch (NumberFormatException e) {
            //
        }

        String servesUnit = "personer";


        User user = User.find("byEmail", Security.connected()).first();
        if (recipe == null) {
            recipe = new Recipe(user, title, description, steps, source, serves, servesUnit).save();
        } else {
            recipe.author = user;
            recipe.title = title;
            recipe.description = description;
            recipe.steps = steps;
            recipe.source = source;
            recipe.serves = serves;
            recipe.servesUnit = servesUnit;

            recipe.photoName = null;
            recipe.tags = new HashSet<Tag>();
            recipe.ingredients = new ArrayList<Ingredient>();
            recipe.save();
            Ingredient.delete("recipe = ?", recipe);

        }


        for (Node event : XPath.selectNodes("//div[@id='recipe']/div[@class='rightColumn']/div/div[@class='ingredient_parent']", recipeDocument)) {

            Double amount = 0.0;
            String unit = "";
            String ingredientName = "";

            String mengdeEnhet = XPath.selectText("div[@class='mengdeEnhet']", event);
            if (mengdeEnhet != null) {
                String[] mengdeEnhetArray = mengdeEnhet.trim().split(" ");
                String amountString = mengdeEnhetArray[0];
                unit = mengdeEnhetArray[1];
                if (amountString != null) {
                    amount = Double.parseDouble(amountString.replace(",", "."));
                }
            }

            String produktnavn = XPath.selectText("div[@class='produktnavn']", event);
            if (produktnavn != null) {
                ingredientName = cleanProductNames(produktnavn);
            }

            recipe.addIngredient(amount, unit, ingredientName);
            convertPackageToWeight(recipe.ingredients.get(recipe.ingredients.size() - 1));
        }

        String tidsbruk = XPath.selectText("//div[@class='tidsbruk']/strong", recipeDocument);
        //   recipe.tagItWith(tidsbruk + "min");
        //   recipe.tagItWith("rema1000");

        String photoUrl;
        photoUrl = XPath.selectText("//div[@id='recipe']/div[@class='leftColumn']/div[@class='recipeImage recipeImageDraggable']/img/@src", recipeDocument);

        WS.HttpResponse response = WS.url(photoUrl).get();
        InputStream fileStream = response.getStream();
//        String name = Calendar.getInstance().getTimeInMillis() + "" + ((int) (Math.random() * 100000)) + photoUrl.substring(photoUrl.length() - 4);
        String name = recipe.id + photoUrl.substring(photoUrl.length() - 4);
        Image.save(name, fileStream);
        recipe.photoName = name;

        return recipe;
    }

    private static Pattern packagePattern = Pattern.compile("c?a? ?([0-9]+,?[0-9]*) ?(k?g)");
    private static Pattern packagePatternFlerPack = Pattern.compile("([0-9]+)-? ?(pk)");


    private static void processMatchesInIngredients(Matcher matcher, Ingredient originalIngredient) {
        if (matcher.find()) {
            String amount = matcher.group(1);
            String newDesc = matcher.replaceFirst("");
            String unit = matcher.group(2);
            Double amountDouble = Double.parseDouble(amount.replace(",", "."));
            Double totalAmount = amountDouble * originalIngredient.amount;

            reporter("Replacing " + originalIngredient.amount + " " + originalIngredient.unit + " " + originalIngredient.description + " with: " + totalAmount + " " + unit + " " + newDesc);
            originalIngredient.amount = amountDouble;
            originalIngredient.unit = unit;
            originalIngredient.description = newDesc;
            originalIngredient.save();
        }
    }

    private static void convertPackageToWeight(Ingredient originalIngredient) {
        if (originalIngredient.unit.matches("pk|stk|pose|poser|boks|bokser")) {
            Matcher matcher = packagePattern.matcher(originalIngredient.description);
            processMatchesInIngredients(matcher, originalIngredient);
            matcher = packagePatternFlerPack.matcher(originalIngredient.description);
            processMatchesInIngredients(matcher, originalIngredient);
        }
    }

    private static void convertPackageToWeightRydding(Ingredient originalIngredient) {
        if (originalIngredient.unit.matches("g|kg")) {
            Matcher matcher = packagePattern.matcher(originalIngredient.description);
            if (matcher.find()) {
                String newDesc = matcher.replaceFirst("");

                reporter("Replacing " + originalIngredient.amount + " " + originalIngredient.unit + " " + originalIngredient.description + " with: " + originalIngredient.amount + " " + originalIngredient.unit + " " + newDesc);
                originalIngredient.description = newDesc;
                originalIngredient.save();
            }

        }
    }


    static String[] kjenteProdukter = new String[]{"Ybarra", "Apetina", "classic", "premium", "Gaea", "Austin Lamb Chops", "5%", "9%", "14%", "18%", "10%", "Toro", "Barilla", "Gilde", "Idun", "Mills", "Grovt og godt", "Godehav", "Solvinge", "REMA 1000", "Tine", "Bama", "Kikkoman", "Nordfjord", "Blue Dragon", "Taga", "Finsbråten", "Hatting", "Mesterbakeren", "Grilstad", "Ideal", "Staur", "MaxMat", "Viddas", "Gourmet", "Pampas", "frossen", "frosne", "naturell", "M/L,", "- NB! Sesongvare"};

    private static String cleanProductNames(String beskrivelse) {
        String produktnavn = beskrivelse;

        for (String kjentProdukt : kjenteProdukter) {
            produktnavn = produktnavn.replaceAll(" *" + kjentProdukt + " *", " ");
        }


        produktnavn = produktnavn.trim();
        if (produktnavn.endsWith(",")) {
            produktnavn = produktnavn.substring(0, produktnavn.length() - 1);
            produktnavn = produktnavn.trim();

        }
        if (produktnavn.compareToIgnoreCase("Stjernebacon skive") == 0) {
            produktnavn = "Bacon";
        }
        if (produktnavn.compareToIgnoreCase("Kyllingfile") == 0) {
            produktnavn = "Kyllingfilet";
        }
        if (produktnavn.compareToIgnoreCase("Spanske kjøttbolle") == 0) {
            produktnavn = "Spanske kjøttboller";
        }
        if (produktnavn.compareToIgnoreCase("Greske kjøttbolle") == 0) {
            produktnavn = "Greske kjøttboller";
        }


        if (produktnavn.compareToIgnoreCase(beskrivelse) != 0) {
            reporter("Erstattet ingrediensbeskrivelse '" + beskrivelse + "' med '" + produktnavn + "'");
        }
        return produktnavn;
    }

    private static String getStep(Node event) {
        String steps = "";
        String overskrift = XPath.selectText("h4", event);
        if (overskrift != null) {
            steps = steps + overskrift + "\n\n";
        }

        String tekst = XPath.selectText("p", event);
        if (tekst != null) {
            steps = steps + tekst + "\n\n";
        }
        return steps;
    }

    private static Document cleanRema1000Html(String content) {
        String bodyContent = content.substring(content.indexOf("<body>"), content.indexOf("</body>") + 7);

        bodyContent = bodyContent.replaceAll("<input[^>]*>", "");

        bodyContent = bodyContent.replaceAll("^\\t* *\\t* *$\\n", "");

        bodyContent = bodyContent.substring(0, bodyContent.indexOf("<script")) + bodyContent.substring(bodyContent.indexOf("</script>") + 9);

        bodyContent = bodyContent.replaceAll("&amp;", "&");
        bodyContent = bodyContent.replaceAll("&", "&amp;");

        return XML.getDocument(bodyContent);
    }


}
