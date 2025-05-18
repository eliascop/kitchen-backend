package br.com.kitchenbackend.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users", schema = "master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("user")
    @NotBlank(message = "O usuário não pode estar em branco")
    private String user;

    @NotBlank(message = "A senha não pode estar em branco")
    private String password;

    @JsonProperty("name")
    @NotBlank(message = "O nome não pode estar em branco")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @JsonProperty("profile")
    private int profile;

    private String paypalPayerId;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;


}