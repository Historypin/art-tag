package sk.eea.ambrosia.arttag;

import sk.eea.ambrosia.arttag.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silvia on 11/02/16.
 */
public class Singleton {

    private static Singleton singleton = new Singleton( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */

    User user;

    public User getUser() {
        return user;
    }


    protected Singleton(){
        user = new User() ;
    }


    /**
     * A handle to the unique Singleton instance.
     */
    static private Singleton instance = null;


    /* Static 'instance' method */
    public static Singleton getInstance( ) {
        if(null == instance) {
            instance = new Singleton();
        }
        return instance;
    }
    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }

}
