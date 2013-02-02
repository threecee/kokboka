package models;

import play.Logger;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Images;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Entity
@Table(name = "my_recipe")
public class Recipe extends Model {

    public String title;

    public Date postedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    public List<Ingredient> ingredients;

    @ManyToOne
    public User author;

    @ManyToMany
    public List<User> favorited;


    public String source;


    public double serves;
    public String servesUnit;

   // public Blob photo;
   // public Blob photoThumb;


    @Basic
    @Column(name = "steps", columnDefinition = "text")
    public String steps;

    @Basic
    @Column(name = "description", columnDefinition = "text")
    public String description;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;


    public String photoName;
    public String photoThumbName;


    public Recipe(User author, String title, String description, String steps, String source, double serves, String servesUnit) {
        this.ingredients = new ArrayList<Ingredient>();
        this.tags = new HashSet<Tag>();
        this.author = author;
        this.title = title;
        this.description = description;
        this.steps = steps;
        this.source = source;
        this.serves = serves;
        this.servesUnit = servesUnit;
        this.postedAt = new Date();
    }

    public Recipe(User user) {
        this.author = user;
        this.ingredients = new ArrayList<Ingredient>();
        this.tags = new HashSet<Tag>();
        this.postedAt = new Date();

    }

    public Ingredient addIngredient(String amount, String unit, String description) {
        Ingredient newIngredient = new Ingredient(this, amount, unit, description).save();
        this.ingredients.add(newIngredient);
        this.save();
        return newIngredient;
    }
/*
    public void addPhoto(Blob photo) {
        photoThumb = new Blob();
        File inputFile = new File("" + Calendar.getInstance().getTimeInMillis());
        Images.resize(photo.getFile(), inputFile, 140, 140);
        try {
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            photoThumb.set(fileInputStream, photo.type());
        } catch (FileNotFoundException e) {
            Logger.error(e, "Fant ikke bildefil");
        }
        this.photo = photo;
    }
  */
    public Recipe tagItWith(String name) {
        if (!tags.contains(Tag.hashName(name))) {
            tags.add(Tag.findOrCreateByName(name));
        }
        return this;
    }

    public static List<Recipe> findTaggedWith(String tag) {
        return Recipe.find(
                "select distinct p from Recipe p join p.tags as t where t.nameHash = '?'", tag
        ).fetch();
    }

    public static List<Recipe> findTaggedWith(String... tags) {
        return Recipe.find(
                "select distinct p from Recipe p join p.tags as t where t.nameHash in (:tags) group by p.id, p.author, p.title, p.description, p.steps, p.source, p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
}