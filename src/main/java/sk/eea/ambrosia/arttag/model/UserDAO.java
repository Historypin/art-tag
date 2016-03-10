package sk.eea.ambrosia.arttag.model;

import sk.eea.ambrosia.arttag.model.User;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by silvia on 11/02/16.
 */
public class UserDAO implements sk.eea.ambrosia.arttag.ServiceInterface {

    sk.eea.ambrosia.arttag.Singleton instance;

    public UserDAO() {
        this.instance = instance.getInstance();
    }

    public User findAll() {
        System.out.println("findAll "+instance.getUser().getName());
        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"test");
        return instance.getUser();
    }


    public User create(User user) {
        System.out.println("creating user " + user.getName());
        return user;
    }

    public User update(User user) {
        System.out.println("Updating user: " + user.getName());
        return user;
    }


}
