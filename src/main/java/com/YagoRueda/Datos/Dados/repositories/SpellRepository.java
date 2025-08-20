package com.YagoRueda.Datos.Dados.repositories;

import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpellRepository extends JpaRepository<SpellEntity, Long> {

    /**
     * Función que devuelve todos los hechizos de un usuario
     *
     * @param user {@code UserEntity} que representa al usuario al que persistence los hechizos
     * @return lista de hechizos
     */
    List<SpellEntity> findByUser(UserEntity user);

    @Query("SELECT s FROM SpellEntity s JOIN s.user u WHERE u.username = 'admin' AND s.name LIKE CONCAT(:search, '%')")
    List<SpellEntity> findWOTSpells(@Param("search") String search);

    @Query("SELECT s FROM SpellEntity s JOIN s.user u WHERE u.username <> 'admin' AND s.publicVisible = true AND s.name LIKE CONCAT(:search, '%')")
    List<SpellEntity> findPublicSpells(@Param("search") String search);

    /**
     * Función que devuelve todos los hechizos de un usuario usando un filtro para el nombre, el filtro puede estar vacio
     *
     * @param user {@code UserEntity} que representa al usuario al que persistence los hechizos
     * @param name nombre de hechizo que se usara como filtro para la busqueda
     * @return hechizo correspondiente
     */
    List<SpellEntity> findByUserAndNameStartingWithIgnoreCase(UserEntity user, String name);

    /**
     * Función para contar todos los hechizos que tiene un usuario
     *
     * @param user {@code UserEntity} que representa al usuario al que pertenecen los hechizos
     * @return Número de hechizos del usuario
     */
    int countByUser(UserEntity user);

    /**
     * Función que indica si un hechizo existe para un usuario indicando el nombre del hechizo
     *
     * @param user {@code UserEntity} que representa al usuario al que persistencia los hechizos
     * @param name nombre de hechizo que se usara como filtro para la busqueda
     * @return {@code True} en caso de que exista, en otro caso {@code False}
     */
    boolean existsByUserAndName(UserEntity user, String name);


}
