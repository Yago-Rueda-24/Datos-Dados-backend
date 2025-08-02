package com.YagoRueda.Datos.Dados.Dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class UserDto {

    @Setter
    @Getter
    private long id;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String passwordRepeat;
    @Getter
    @Setter
    private Instant signup_date;
}
