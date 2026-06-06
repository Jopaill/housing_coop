package com.jordanpaille.housing.coop.configs;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
public class SecurityConfigs {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @DependsOn("dataSourceScriptDatabaseInitializer") // Required for database initialization
    UserDetailsManager users(DataSource dataSource, PasswordEncoder passwordEncoder) throws IOException {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        // Read emails from classpath
        List<String> emails;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("users/emails.txt").getInputStream()))) {
            emails = reader.lines().toList();
        }
        for (String email : emails) {
            String username = email.trim();
            if (!username.isEmpty() && !manager.userExists(username)) {
                UserDetails user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString())) // random password
                        .disabled(true)
                        .roles("USER")
                        .build();
                manager.createUser(user);
            }
        }
        String username = "testuser";
        if (!manager.userExists(username)) {
            UserDetails user = User.builder()
                    .username("username")
                    .password(passwordEncoder.encode("test"))
                    .roles("USER")
                    .build();
            manager.createUser(user);
        }
        return manager;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Environment env) {
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");
        log.info("Spring Active Profile(s): {}. Dev mode: {}", Arrays.toString(env.getActiveProfiles()), isDev);

        http
                .securityMatcher("/**")
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(Customizer.withDefaults())
                        .bearerTokenResolver(request -> {
                            String uri = request.getRequestURI();
                            if (
                                    uri.equals("/login")
                                    || uri.equals("/forgot-password")
                                    || uri.equals("/reset-password")
                                    || uri.startsWith("/css/")
                                    || uri.startsWith("/js/")
                                    || uri.startsWith("/images/")) {
                                return null;
                            }
                            if (isDev && (uri.equals("/h2-console") || uri.startsWith("/h2-console/"))) return null;
                            if (request.getCookies() != null) {
                                for (Cookie c : request.getCookies()) {
                                    if ("jwt".equals(c.getName())) return c.getValue();
                                }
                            }
                            return new DefaultBearerTokenResolver().resolve(request);
                        })
                        .authenticationEntryPoint((req, res, ex) -> res.sendRedirect("/login"))
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/forgot-password", "/reset-password").permitAll();
                    if (isDev) auth.requestMatchers("/h2-console/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (isDev) {
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        }

        return http.build();
    }




    // -------------- Below are the JWT configs --------------------------

    @Bean
    public KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        return NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
