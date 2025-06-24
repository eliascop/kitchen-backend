package br.com.kitchen.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank(message = "O usuário não pode estar em branco")
    private String login;

    @NotBlank(message = "A senha não pode estar em branco")
    private String password;

}
