package br.com.kitchenbackend.service;

import br.com.kitchenbackend.model.User;
import br.com.kitchenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends GenericService<User, Long> {
    
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        super(repository, User.class);
        this.userRepository = repository;
    }

    public User registerUser(User user) {
        if (userRepository.findByUser(user.getUser()).isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }
}
