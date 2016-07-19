package sk.eea.arttag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import sk.eea.arttag.model.User;

//public interface UserRepository extends CrudRepository<User, String> {
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
