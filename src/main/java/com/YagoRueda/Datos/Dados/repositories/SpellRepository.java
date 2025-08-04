package com.YagoRueda.Datos.Dados.repositories;

import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpellRepository extends JpaRepository<SpellEntity,Long> {

    /**
     * Función que devuelve todos los hechizos de un usuario
     * @param user {@code UserEntity} que representa al usuario al que persistence los hechizos
     * @return lista de hechizos
     */
    List<SpellEntity> findByUser(UserEntity user);

    /**
     * Función que devuelve todos los hechizos de un usuario usando un filtro para el nombre, el filtro puede estar vacio
     * @param user {@code UserEntity} que representa al usuario al que persistence los hechizos
     * @param name nombre de hechizo que se usara como filtro para la busqueda
     * @return hechizo correspondiente
     */
    List<SpellEntity> findByUserAndNameStartingWithIgnoreCase(UserEntity user, String name);




}
