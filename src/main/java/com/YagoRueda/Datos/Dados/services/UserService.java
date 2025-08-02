package com.YagoRueda.Datos.Dados.services;


import com.YagoRueda.Datos.Dados.Dtos.UserDto;
import com.YagoRueda.Datos.Dados.exceptions.InvalidInputDataException;
import com.YagoRueda.Datos.Dados.exceptions.UnauthorizedException;
import com.YagoRueda.Datos.Dados.models.TokenEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.repositories.TokenRepository;
import com.YagoRueda.Datos.Dados.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private MessageDigest hash;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenRepository tokenRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        try {
            this.hash = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String hashString(String prehash) {
        hash.reset();
        hash.update(prehash.getBytes());
        byte[] hashed_password = this.hash.digest();
        return new String(hashed_password, StandardCharsets.UTF_8);
    }

    public UserEntity signup(UserDto dto) throws InvalidInputDataException {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new InvalidInputDataException("El nombre de usuario ya existe, utiliza otro nombre porfavor.");
        }
        if (!dto.getPassword().equals(dto.getPasswordRepeat())) {
            throw new InvalidInputDataException("las contraseñas no coinciden");
        }
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        entity.setPassword(hashString(dto.getPassword()));
        entity.setSignup_date(Instant.now());
        return userRepository.save(entity);
    }

    public String login(UserDto dto) throws InvalidInputDataException{
        if (!userRepository.existsByUsername(dto.getUsername())) {
            throw new InvalidInputDataException("El usuario no existe");
        }

        UserEntity user = userRepository.findByUsername(dto.getUsername());
        String hashedPassword = hashString(dto.getPassword());
        if (!user.getPassword().equals(hashedPassword)) {
            throw new InvalidInputDataException("La contraseña es incorrecta");
        }

        List<TokenEntity> livetokens = tokenRepository.getLiveTokens(user);
        livetokens.forEach(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });

        TokenEntity token = tokenService.createToken(user);
        return token.getTokenId();

    }

    public UserEntity logout(String token) throws InvalidInputDataException, UnauthorizedException {
        Optional<TokenEntity> optionalToken = tokenRepository.findByTokenId(token);
        if (optionalToken.isEmpty()) {
            throw new InvalidInputDataException("El token no existe");
        }
        TokenEntity userToken = optionalToken.get();
        if (!tokenService.validateAndRenewToken(userToken.getTokenId())) {
            throw new UnauthorizedException("Token caducado o revocado");
        }
        UserEntity user = userToken.getUser();
        userToken.setRevoked(true);
        tokenRepository.save(userToken);
        return user;
    }
}
