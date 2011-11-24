package models;

import play.db.jpa.Model;

import javax.persistence.*;
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

    public String source;

    @Lob
    public String steps;

    @Lob
    public String description;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;


    public Recipe(User author, String title, String description, String steps, String source) {
        this.ingredients = new ArrayList<Ingredient>();
        this.tags = new TreeSet<Tag>();
        this.author = author;
        this.title = title;
        this.description = description;
        this.steps = steps;
        this.source = source;
        this.postedAt = new Date();
    }

    public Recipe addIngredient(String amount, String description) {
        Ingredient newIngredient = new Ingredient(this, amount, description).save();
        this.ingredients.add(newIngredient);
        this.save();
        return this;
    }

    public Recipe tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    public static List<Recipe> findTaggedWith(String tag) {
        return Recipe.find(
                "select distinct p from Recipe p join p.tags as t where t.name = ?", tag
        ).fetch();
    }

    public static List<Recipe> findTaggedWith(String... tags) {
        return Recipe.find(
                "select distinct p from Recipe p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.title, p.description, p.steps, p.source, p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
}