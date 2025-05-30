package br.com.kitchen.backend.repository;

import br.com.kitchen.backend.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends GenericRepository<User, Long> {
    Optional<User> findByUser(String user);
}
