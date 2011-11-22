package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="my_unit")
public class Recipe extends Model {

    public String amount;
    public String description;

    public Recipe(String amount, String description) {
        this.amount = amount;
        this.description = description;
    }

}