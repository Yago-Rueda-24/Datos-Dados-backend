package com.YagoRueda.Datos.Dados.controlles;

import com.YagoRueda.Datos.Dados.Dtos.SpellDto;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.services.SpellService;
import com.YagoRueda.Datos.Dados.services.TokenService;
import com.YagoRueda.Datos.Dados.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/spell")
public class SpellController {

    private final SpellService spellService;
    private final TokenService tokenService;

    public SpellController(SpellService spellService, TokenService tokenService) {
        this.spellService = spellService;
        this.tokenService = tokenService;
    }

    /**
     * Enpoint de creación de hechizos
     * @param token Token que representa la sesión de un usuario
     * @param dto {@code SpellDto} que representa el hechizo creado
     * @return Respuesta HTTP con el status code correspondiente
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSpell(@RequestParam String token, @RequestBody SpellDto dto) {

        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            SpellEntity entity = spellService.createSpell(user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(entity.toDTO());
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }

    }
}
