package br.com.kitchen.api.service;

import br.com.kitchen.api.model.User;
import br.com.kitchen.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends GenericService<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        super(userRepository, User.class);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        userRepository.findByUser(user.getUser())
                .ifPresent(u -> { throw new RuntimeException("Usuário já existe"); });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfile(2);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
