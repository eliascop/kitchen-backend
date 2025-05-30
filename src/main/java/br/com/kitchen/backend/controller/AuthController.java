package br.com.kitchen.backend.controller;

import br.com.kitchen.backend.dto.AuthRequestDTO;
import br.com.kitchen.backend.dto.AuthResponseDTO;
import br.com.kitchen.backend.model.User;
import br.com.kitchen.backend.security.CustomUserDetails;
import br.com.kitchen.backend.service.UserService;
import br.com.kitchen.backend.util.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUser(), authRequest.getPassword())
            );

            var customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            var user = customUserDetails.getUser();

            final String jwt = jwtTokenProvider.generateToken(user);

            return ResponseEntity.ok(new AuthResponseDTO(jwt));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(401)
                    .body("Falha na autenticação: " + ex.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createAccount(@Valid @RequestBody User authUserRequest) {
        try {
            User userCreated = userService.registerUser(authUserRequest);
            return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
