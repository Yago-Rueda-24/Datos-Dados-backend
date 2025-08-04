package com.YagoRueda.Datos.Dados.services;

import com.YagoRueda.Datos.Dados.Dtos.SpellDto;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.repositories.SpellRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpellService {

    private final SpellRepository spellRepository;

    public SpellService(SpellRepository spellRepository) {
        this.spellRepository = spellRepository;
    }

    /**
     * Crea un spell personalizado y lo almacena en base de datos
     *
     * @param user {@code UserEntity} que representa al usuario que crea el hechizo
     * @param dto  {@code SpellDto} que representa el hechizo creado
     * @return {@code SpellEntity } que representa el hechizo creado
     */
    public SpellEntity createSpell(UserEntity user, SpellDto dto) {
        try {
            SpellEntity spell = dto.toEntity(user);
            return spellRepository.save(spell);
        } catch (InvalidInputDataException e) {
            throw new InvalidInputDataException(e.getMessage());
        }

    }


    /**
     * Devuelve una lista de hechizos con todos los hechizos de un usuario, se puede filtrar los hechizos devueltos por nombre utilizando el parametro search
     * @param user El usuario al que le pertenecen los hechizos
     * @param search El nombre del hechizo buscado, si se deja vacio o nulo devolvera todos los hechizos
     * @return lista de DTO's con la información de los hechizos
     */
    public List<SpellDto> getListSpells(UserEntity user, String search) {
        if (search == null || search.isEmpty()) {
            return spellRepository.findByUser(user).stream().map(SpellEntity::toDTO).toList();
        } else {
            return spellRepository.findByUserAndNameStartingWithIgnoreCase(user, search).stream().map(SpellEntity::toDTO).toList();
        }
    }

    /**
     * Devuelve un hechizo específico con base en al ID que se le pase por parametro
     * @param id Id del hechizo que devolvera
     * @return DTO con la información del hechizo
     */
    public SpellDto getSpell(long id) {
        Optional<SpellEntity> opt = spellRepository.findById(id);
        return opt.map(SpellEntity::toDTO).orElse(null);
    }

}
