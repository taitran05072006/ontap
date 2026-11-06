package com.example.demo.service;


import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.RefreshTokenRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomerUserDetails;
import com.example.demo.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refresh_ms;

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))  throw new BusinessException("Email da ton tai");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        return mapper(user);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        CustomerUserDetails principal = (CustomerUserDetails) authentication.getPrincipal();
        return mapper(principal.getUser());
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken()).orElseThrow(()->new RuntimeException("Invalid refresh token"));
        if (refreshToken.getExpired().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            throw new RuntimeException("Refresh token expired");
        }
        return LoginResponse.builder()
                .accessToken(jwtService.generateToken(new CustomerUserDetails(refreshToken.getUser())))
                .refreshToken(refreshToken.getToken())
                .typeToken("Bearer")
                .email(refreshToken.getUser().getEmail())
                .role(refreshToken.getUser().getRole().name())
                .userId(refreshToken.getUser().getId())
                .build();
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken()).ifPresent(token-> token.setRevoked(true));
    }

    private LoginResponse mapper(User user) {
        RefreshToken refreshToken = refreshTokenRepository.save(
               RefreshToken.builder()
                       .user(user)
                       .token(UUID.randomUUID().toString())
                       .expired(Instant.now().plusMillis(refresh_ms))
                       .build()
        );
        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtService.generateToken(new CustomerUserDetails(user)))
                .refreshToken(refreshToken.getToken())
                .typeToken("Bearer")
                .role(user.getRole().name())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
        return response;
    }

}
