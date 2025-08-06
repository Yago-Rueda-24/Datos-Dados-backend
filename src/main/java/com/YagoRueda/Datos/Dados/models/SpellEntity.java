package com.YagoRueda.Datos.Dados.models;

import com.YagoRueda.Datos.Dados.Dtos.SpellDto;
import com.YagoRueda.Datos.Dados.enums.SpellDamageType;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
public class SpellEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Setter
    @Getter
    private boolean publicVisible;
    @Setter
    @Getter
    private String name;

    @Getter
    private Integer level;
    @Setter
    @Getter
    private String school;
    @Setter
    @Getter
    private String castTime;
    @Setter
    @Getter
    private String castRange;
    @Setter
    @Getter
    private String components;
    @Setter
    @Getter
    private String duration;
    @Setter
    @Getter
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    @Setter
    @Getter
    private boolean concentration;
    @Setter
    @Getter
    private boolean ritual;
    @Setter
    @Getter
    private SpellDamageType damageType;

    @ElementCollection
    @CollectionTable(
            name = "spell_damage_by_level",
            joinColumns = @JoinColumn(name = "spell_id")
    )
    @MapKeyColumn(name = "nivel")
    @Column(name = "dado_de_ataque")
    @Getter
    @Setter
    private Map<Integer, String> damageByLevel = new TreeMap<>();


    public void setLevel(Integer newLevel) {
        if (newLevel < 0) {
            throw new InvalidInputDataException("El nivel no puede ser menor que 0");
        }
        this.level = newLevel;
    }

    public SpellDto toDTO() {
        SpellDto dto = new SpellDto();
        dto.setId(this.id);
        dto.setUserId(this.user != null ? this.user.getId() : null);
        dto.setPublicVisible(this.publicVisible);
        dto.setName(this.name);
        dto.setLevel(this.level);
        dto.setSchool(this.school);
        dto.setCastTime(this.castTime);
        dto.setCastRange(this.castRange);
        dto.setComponents(this.components);
        dto.setDuration(this.duration);
        dto.setDescription(this.description);
        dto.setConcentration(this.concentration);
        dto.setRitual(this.ritual);
        dto.setDamageType(this.damageType);
        dto.setDamageByLevel(this.damageByLevel);
        return dto;
    }

}
