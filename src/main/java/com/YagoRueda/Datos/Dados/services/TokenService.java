package com.YagoRueda.Datos.Dados.services;

import com.YagoRueda.Datos.Dados.models.TokenEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.repositories.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Crear un token y lo asigna a un usuario
     * @param user El usuario al que se le asignara el token
     * @return El tokenEntity generado
     */
    public TokenEntity createToken(UserEntity user) {
        TokenEntity token = new TokenEntity();
        token.setTokenId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        token.setExpired(false);
        token.setRevoked(false);
        return tokenRepository.save(token);
    }


    // Validar y renovar token si aún no ha caducado

    /**
     * Válida el token, en caso de que sea válido lo renueva, en caso de que haya caducado se marca como caducado
     * @param tokenId El token
     * @return {@code True} en caso de que el token sea válido {@code False} en caso contrario
     */
    public boolean validateAndRenewToken(String tokenId) {
        Optional<TokenEntity> optionalToken = tokenRepository.findByTokenId(tokenId);
        if (optionalToken.isPresent()) {
            TokenEntity token = optionalToken.get();

            if (token.isRevoked()) {
                return false;
            }
            if (token.getExpiresAt().isAfter(Instant.now())) {
                token.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES)); // Renovación
                tokenRepository.save(token);
                return true;
            } else {
                token.setExpired(true);
                tokenRepository.save(token);
                return false;
            }
        }
        return false;
    }

    /**
     * Devuelve el usuario asociado a un token
     * @param tokenId El token
     * @return {@code UserEntity} asociada a ese usuario
     */
    public UserEntity getUser(String tokenId) {
        Optional<TokenEntity> optionalToken = tokenRepository.findByTokenId(tokenId);
        if (optionalToken.isPresent()) {
            TokenEntity token = optionalToken.get();
            return token.getUser();

        } else {
            return null;
        }
    }

}
