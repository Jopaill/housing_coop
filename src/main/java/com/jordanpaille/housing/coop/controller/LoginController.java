package com.jordanpaille.housing.coop.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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
    private final JwtEncoder jwtEncoder; // your existing encoder

    public LoginController(AuthenticationManager authenticationManager,
                           JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
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
