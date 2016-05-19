package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * User entity managed by Ebean
 */
@Entity 
@Table(name="account")
public class User extends Model {

    private static final long serialVersionUID = 1L;


    @Constraints.Required
    public String name;

	@Id
    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;
    @Constraints.Required
    public String password;

    @Constraints.Required
    public String level;

   public Timestamp doneAt = new Timestamp(new Date().getTime());

    // -- Queries
    
    public static Model.Finder<String,User> find = new Model.Finder<String,User>(String.class, User.class);
    
    /**
     * Retrieve all users.
     */
    public static List<User> findAll() {
        return find.all();
    }

    /**
     * Retrieve a User from email.
     */
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }
    
    /**
     * Authenticate a User.
     */
    public static User authenticate(String email, String password) {
        return find.where()
            .eq("email", email)
            .eq("password", password)
            .findUnique();
    }
    
    // --
    
    public String toString() {
        return "User(" + email + ")";
    }



    // account creation

    public User() {}

    public User(String name, String email, String password, String level) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.level = level;
    }
    public static void saveInfo (User info) {

        info.save();
    }


}

