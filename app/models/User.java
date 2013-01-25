package models;

import org.hibernate.annotations.ManyToAny;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "my_user")
public class User extends Model {

    public String email;
    public String password;
    public String fullname;
    public boolean isAdmin;

    @ManyToOne
    public Menu activeMenu;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
}
