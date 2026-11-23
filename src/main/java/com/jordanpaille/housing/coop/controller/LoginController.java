package com.jordanpaille.housing.coop.controller;

import com.jordanpaille.housing.coop.configs.ConstantsConfigurations;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/public")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final boolean areCookiesSecured;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtEncoder jwtEncoder,
                           JwtDecoder jwtDecoder,
                           ConstantsConfigurations constants) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        areCookiesSecured = !constants.isDevEnvironment();
        log.info("LoginController created with areCookiesSecured = {}", areCookiesSecured);
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
                            return "redirect:/home";
                        }
                    } catch (JwtException e) {
                        // Token invalid or expired — fall through to show login page
                    }
                }
            }
        }
        return "public/login";
    }

    @PostMapping("/login")
    public void handleLogin(@RequestParam String username,
                            @RequestParam String password,
                            HttpServletResponse response) throws IOException {

        // 0. Lowercase the username
        username = username.toLowerCase();

        // 1. Authenticate
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // 2. Generate JWT (reuse your existing token logic)
        Instant now = Instant.now();
        int validityPeriodOfCookie = 2628000;
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validityPeriodOfCookie))        // ToDo: Properly setup expiresAt here
                .subject(auth.getName())
//                .claim("scope", auth.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.joining(" ")))           // ToDo: Properly setup the authorities here!
                .claim("scope", List.of("Hello", "World"))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // 3. Set HttpOnly cookie
        ResponseCookie cookie = ResponseCookie
                .from("jwt", token)
                .httpOnly(true)
                .secure(areCookiesSecured)
                .path("/")
                .sameSite("Lax")
                .maxAge(validityPeriodOfCookie)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // 4. Redirect
        response.sendRedirect("/home");
    }

    @ExceptionHandler(AuthenticationException.class)
    public String authenticationException(AuthenticationException e,
                                          HttpServletRequest request,
                                          RedirectAttributes attributes) {
        log.error("AuthenticationException was thrown: ", e);
        String username = request.getParameter("username");
        if (username != null) {
            attributes.addFlashAttribute("username", username);
        }
        return "redirect:/public/login?error";
    }
}
