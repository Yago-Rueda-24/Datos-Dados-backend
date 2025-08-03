package com.YagoRueda.Datos.Dados.services;

import com.YagoRueda.Datos.Dados.Dtos.SpellDto;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.repositories.SpellRepository;
import org.springframework.stereotype.Service;

@Service
public class SpellService {

    private final SpellRepository spellRepository;

    public SpellService(SpellRepository spellRepository) {
        this.spellRepository = spellRepository;
    }

    /**
     * Crea un spell personalizado y lo almacena en base de datos
     * @param user {@code UserEntity} que representa al usuario que crea el hechizo
     * @param dto {@code SpellDto} que representa el hechizo creado
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
}
