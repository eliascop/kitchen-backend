package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.User;
import br.com.kitchen.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<User> getUserById(@PathVariable Long id) {
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
        try {
            Optional<User> user = service.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "User not found",
                                "code", HttpStatus.NOT_FOUND.value()
                        ));
            }

            service.deleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "code", HttpStatus.OK.value()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "message", "An error occurred while deleting the user",
                            "details", e.getMessage()
                    ));
        }
    }


}
