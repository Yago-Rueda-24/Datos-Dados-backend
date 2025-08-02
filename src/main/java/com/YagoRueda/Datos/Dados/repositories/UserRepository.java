package com.YagoRueda.Datos.Dados.repositories;

import com.YagoRueda.Datos.Dados.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);

    UserEntity findByUsername(String username);
}
