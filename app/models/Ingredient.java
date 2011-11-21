package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
@Table(name="my_ingredient")
public class Ingredient extends Model {

    public String amount;
    public String description;

    public Ingredient(String amount, String description) {
        this.amount = amount;
        this.description = description;
    }

}