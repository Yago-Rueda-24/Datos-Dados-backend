package com.YagoRueda.Datos.Dados.controlles;

import com.YagoRueda.Datos.Dados.Dtos.SpellDto;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.exceptions.UnauthorizedException;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.services.SpellService;
import com.YagoRueda.Datos.Dados.services.TokenService;
import com.YagoRueda.Datos.Dados.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     *
     * @param token Token que representa la sesión de un usuario
     * @param dto   {@code SpellDto} que representa el hechizo creado
     * @return Respuesta HTTP con el status code correspondiente
     */
    @PostMapping()
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

    /**
     * Enpoint de modificación de hechizos
     *
     * @param token Token que representa la sesión de un usuario
     * @param dto   {@code SpellDto} que representa el hechizo modificado
     * @return Respuesta HTTP con el status code correspondiente
     */
    @PutMapping()
    public ResponseEntity<?> modifySpell(@RequestParam String token, @RequestBody SpellDto dto) {
        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            SpellEntity entity = spellService.modifySpell(user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(entity.toDTO());
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Enpoint de eliminación de hechizos
     *
     * @param token Token que representa la sesión de un usuario
     * @param id    id del hechizo a eliminar
     * @return Respuesta HTTP con el status code correspondiente
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteSpell(@RequestParam String token, @RequestParam long id) {
        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión ha expirado"));
        }
        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            spellService.deleteSpell(id);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Entidad borrada con exito"));
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }


    /**
     * Endpoint que devuelve la lista de hechizos de un usuario
     *
     * @param token  token de la sesión del usuario al que pertenecen los hechizos
     * @param search nombre del hechizo que se usara para el filtro la búsqueda, puede estar vació
     * @return Respuesta HTTP con el status code y la información correspondiente
     */
    @GetMapping("/list")
    public ResponseEntity<?> getSpells(@RequestParam String token, @RequestParam(required = false) String search) {

        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            List<SpellDto> spellDtoList = spellService.getListSpells(user, search);
            return ResponseEntity.status(HttpStatus.OK).body(spellDtoList);
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }

    }

    /**
     * Endpoint que devuelve un hechizo de un usuario
     *
     * @param token token de la sesión del usuario al que pertenecen los hechizos
     * @param id    id del hechizo que se busca
     * @return Respuesta HTTP con el status code y la información correspondiente
     */
    @GetMapping()
    public ResponseEntity<?> getSpell(@RequestParam String token, @RequestParam long id) {
        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            SpellDto spellDto = spellService.getSpell(id);
            if (spellDto == null) {
                throw new InvalidInputDataException("Hechizo no encontrado");
            }
            return ResponseEntity.status(HttpStatus.OK).body(spellDto);
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/wotspells")
    public ResponseEntity<?> getWOTSpell(@RequestParam String token, @RequestParam(required = false) String search) {
        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            List<SpellDto> dtos = spellService.getWOTSpells(search);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicSpell(@RequestParam String token, @RequestParam(required = false) String search) {
        if (!tokenService.validateAndRenewToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "la sesión ha expirado"));
        }

        UserEntity user = tokenService.getUser(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "la sesión no existe"));
        }
        try {
            List<SpellDto> dtos = spellService.getPublicSpells(search);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (InvalidInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

}
