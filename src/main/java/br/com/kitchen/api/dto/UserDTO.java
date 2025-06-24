package br.com.kitchen.api.dto;

import br.com.kitchen.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String login;
    private String name;
    private String phone;
    private String email;
    private int profile;

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }
}
