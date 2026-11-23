package com.jordanpaille.housing.coop.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public record ConstantsConfigurations(
        boolean isDevEnvironment
) {

    @Autowired
    public ConstantsConfigurations(Environment environment) {
        this(resolveCookiesSecured(environment));
        log.info("ConstantsConfigurations initialized with isDevEnvironment = {} because the active profiles are {}", isDevEnvironment, Arrays.toString(environment.getActiveProfiles()));
    }

    private static boolean resolveCookiesSecured(Environment environment) {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> profile.equalsIgnoreCase("dev"));
    }
}