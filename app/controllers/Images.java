package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Image;
import models.Recipe;
import models.UploadedFile;
import play.Logger;
import play.db.jpa.Blob;
import play.mvc.Controller;


import java.util.ArrayList;
import java.util.List;


public class Images extends Controller {


	public String form() {
		return "form";
	}

	public static void message(Object message) {
		// Do custom steps here
		// i.e. Persist the message to the database
		Logger.debug("Service processing...done");

		renderJSON(new StatusResponse(true, "Message received"));
	}

	public static void file(Long id,
			Blob file) {
		// Do custom steps here
		// i.e. Save the file to a temporary location or database
		Logger.debug("Writing file to disk...done");

        Recipe recipe = Recipe.findById(id);

        recipe.photoName = recipe.id + "";

        Image.save(recipe.photoName, file.get());

        recipe.save();

        ArrayList<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
        UploadedFile file1 = new UploadedFile(recipe.photoName, (int) file.getFile().length(), Image.get(recipe));
        file1.setThumbnail_url(Image.get(recipe));
        uploadedFiles.add(file1);

        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(uploadedFiles);

        renderJSON("{\"files\": " + jsonString + " }");
	}
}