package com.jordanpaille.housing.coop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtEncoder jwtEncoder,
                           JwtDecoder jwtDecoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("jwt".equals(c.getName())) {
                    try {
                        Jwt jwt = jwtDecoder.decode(c.getValue());
                        // If decode succeeds and token isn't expired, redirect
                        if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isAfter(Instant.now())) {
                            return "redirect:/index";
                        }
                    } catch (JwtException e) {
                        // Token invalid or expired — fall through to show login page
                    }
                }
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public void handleLogin(@RequestParam String username,
                            @RequestParam String password,
                            HttpServletResponse response) throws IOException {

        // 1. Authenticate
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // 2. Generate JWT (reuse your existing token logic)
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))        // ToDo: Properly setup expiresAt here
                .subject(auth.getName())
//                .claim("scope", auth.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.joining(" ")))           // ToDo: Properly setup the authorities here!
                .claim("scope", List.of("Hello", "World"))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // 3. Set HttpOnly cookie
        String cookie = String.format("jwt=%s; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600", token);
        response.addHeader("Set-Cookie", cookie);

        // 4. Redirect
        response.sendRedirect("/index");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String badCredentials() {
        return "redirect:/login?error";
    }
}
