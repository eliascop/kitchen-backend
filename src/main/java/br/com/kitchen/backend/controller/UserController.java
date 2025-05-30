package br.com.kitchen.backend.controller;

import br.com.kitchen.backend.model.User;
import br.com.kitchen.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try{
            User user = service.findById(id);
            service.deleteUser(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User deleted successfully",
                            "code", HttpStatus.OK
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "errorCode", 500,
                            "message", "An error occurred when delete user",
                            "details", e.getMessage()
                    ));
        }

    }

}
