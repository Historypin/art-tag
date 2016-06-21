package sk.eea.arttag.repository;

import org.springframework.data.repository.CrudRepository;
import sk.eea.arttag.model.User;

public interface UserRepository extends CrudRepository<User, String> {
}
