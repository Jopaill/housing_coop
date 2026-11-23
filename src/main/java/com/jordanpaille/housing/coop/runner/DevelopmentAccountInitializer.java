package com.jordanpaille.housing.coop.runner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@Profile("dev") // Keep this account out of production
public class DevelopmentAccountInitializer implements ApplicationRunner {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final PasswordEncoder passwordEncoder;

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
    }

}