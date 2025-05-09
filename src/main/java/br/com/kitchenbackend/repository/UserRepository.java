package br.com.kitchenbackend.repository;

import br.com.kitchenbackend.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByUser(String user);
}
