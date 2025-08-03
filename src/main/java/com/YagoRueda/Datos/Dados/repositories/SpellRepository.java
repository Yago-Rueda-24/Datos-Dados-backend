package com.YagoRueda.Datos.Dados.repositories;

import com.YagoRueda.Datos.Dados.models.SpellEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpellRepository extends JpaRepository<SpellEntity,Long> {
}
