package sk.eea.ambrosia.arttag;


import sk.eea.ambrosia.arttag.model.User;
import sk.eea.ambrosia.arttag.model.UserDAO;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class MyResource {

    UserDAO userDAO = new UserDAO();

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User findAll() {
        return userDAO.findAll();
    }

//    @GET @Path("search/{query}")
//    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//    public List<User> findByName(@PathParam("query") String name) {
//        return userDAO.findByName(name);
//    }

//    @GET @Path("{id}")
//    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//    public User findById(@PathParam("id") int id) {
//       return userDAO.findById(id);
//    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User create(User user) {
        return userDAO.create(user);
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User update(User user) {
        return userDAO.update(user);
    }

//    @DELETE @Path("{id}")
//    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//    public void remove(@PathParam("id") int id) {
//        userDAO.remove(id);
//    }
}
