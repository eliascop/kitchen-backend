package br.com.kitchenbackend.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders", schema = "master")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("creation")
    private Date creation;

    @JsonProperty("status")
    private String status;

    @JsonProperty("cart")
    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;

    @JsonProperty("user")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if (creation == null) {
            creation = new Date();
        }
    }
}
