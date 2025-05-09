package br.com.kitchenbackend.controller;

import br.com.kitchenbackend.model.User;
import br.com.kitchenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users/v1")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> showAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<User> findByName(@RequestParam String name) {
        return service.findByField("user", name);
    }

    @PostMapping
    public User createUser(@RequestBody User user) { return service.registerUser(user);}
}
