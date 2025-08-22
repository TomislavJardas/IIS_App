package com.tjardas.iisapi.controller;

import com.tjardas.iisapi.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public TokenResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
        return new TokenResponse(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody RefreshRequest request) {
        if (!jwtUtils.validateToken(request.getRefreshToken())) {
            throw new JwtException("Invalid refresh token");
        }
        String username = jwtUtils.getUsernameFromToken(request.getRefreshToken());
        String accessToken = jwtUtils.generateAccessToken(username);
        String refreshToken = jwtUtils.generateRefreshToken(username);
        return new TokenResponse(accessToken, refreshToken);
    }
}

@Getter
class LoginRequest {
    private String username;
    private String password;
}

@Getter
class RefreshRequest {
    private String refreshToken;
}

@Getter
@AllArgsConstructor
class TokenResponse {
    private String accessToken;
    private String refreshToken;
}

