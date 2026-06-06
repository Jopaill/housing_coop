package com.jordanpaille.housing.coop.dao;

import com.jordanpaille.housing.coop.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserDao {

    private final JdbcTemplate jdbc;

    // Constructor
    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        log.info("UserDao instantiated");
    }
}
