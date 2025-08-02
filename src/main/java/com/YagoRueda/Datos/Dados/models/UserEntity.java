package com.YagoRueda.Datos.Dados.models;

import com.YagoRueda.Datos.Dados.Dtos.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;
    @Getter
    @Setter
    @Column(unique = true)
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private Instant signup_date;

    public UserDto toDTO() {
        UserDto dto = new UserDto();
        dto.setId(this.getId());
        dto.setUsername(this.getUsername());
        dto.setSignup_date(this.getSignup_date());
        return dto;
    }


}
