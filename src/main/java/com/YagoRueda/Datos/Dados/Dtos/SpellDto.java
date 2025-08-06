package com.YagoRueda.Datos.Dados.Dtos;

import com.YagoRueda.Datos.Dados.enums.SpellDamageType;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class SpellDto {

    private Long id;
    private Long userId;
    private boolean publicVisible;
    private String name;
    private Integer level;
    private String school;
    private String castTime;
    private String castRange;
    private String components;
    private String duration;
    private String description;
    private boolean concentration;
    private boolean ritual;
    private SpellDamageType damageType;
    private Map<Integer, String> damageByLevel = new TreeMap<>();


    public SpellEntity toEntity(UserEntity user) throws InvalidInputDataException {
        if (level != null && level < 0) {
            throw new InvalidInputDataException("El nivel no puede ser menor que 0");
        }

        SpellEntity entity = new SpellEntity();
        entity.setUser(user);
        entity.setPublicVisible(publicVisible);
        entity.setName(name);
        entity.setLevel(level);
        entity.setSchool(school);
        entity.setCastTime(castTime);
        entity.setCastRange(castRange);
        entity.setComponents(components);
        entity.setDuration(duration);
        entity.setDescription(description);
        entity.setConcentration(concentration);
        entity.setRitual(ritual);
        entity.setDamageType(damageType);
        entity.setDamageByLevel(damageByLevel);

        return entity;
    }
}
