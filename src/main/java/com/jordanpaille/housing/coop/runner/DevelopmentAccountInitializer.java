package com.jordanpaille.housing.coop.runner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@Profile("dev") // Keep this account out of production
public class DevelopmentAccountInitializer implements ApplicationRunner {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        String username = "jordan1592@gmail.com";
        String encodedPassword = passwordEncoder.encode("123");

        if (jdbcUserDetailsManager.userExists(username)) {
            log.info("Updating the password for user {}", username);
            jdbcUserDetailsManager.updatePassword(jdbcUserDetailsManager.loadUserByUsername(username), encodedPassword);
        } else {
            log.info("Creating the password for user {}", username);
            jdbcUserDetailsManager.createUser(User.withUsername(username)
                    .password(encodedPassword)
                    .authorities("ROLE_USER")
                    .build());
        }

        MapSqlParameterSource params = new MapSqlParameterSource("username", username);
        int updatedRows = jdbc.update("""
                UPDATE persons
                SET is_member = TRUE
                WHERE username = :username
                """, params);
        if (updatedRows == 0) {
            jdbc.update("""
                    INSERT INTO persons (username, first_name, last_name, is_member)
                    VALUES (:username, 'Jordan', 'Paille', TRUE)
                    """, params);
            log.info("Created member profile for user {}", username);
        } else {
            log.info("Ensured member profile for user {}", username);
        }
    }

}