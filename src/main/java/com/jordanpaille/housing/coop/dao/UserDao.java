package com.jordanpaille.housing.coop.dao;

import com.jordanpaille.housing.coop.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserDao {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    // Constructor
    public UserDao(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
        log.info("UserDao instantiated");
//        List<Person> persons = findAll();
//        persons.forEach(p -> log.info(p.toString()));
        log.info("All users were supposed to be printed");
    }

    private List<Person> findAll() {
        return jdbc.query("SELECT * FROM PERSON", personRowMapper());
    }

    private RowMapper<Person> personRowMapper() {
        return (rs, _) -> {
            Person p = new Person();
            p.setPersonId(rs.getLong("person_id"));
            p.setFirstName(rs.getString("first_name"));
            p.setLastName(rs.getString("last_name"));
            p.setPhone(rs.getString("phone"));
            p.setEmail(rs.getString("email"));
            return p;
        };
    }

    public List<UserDetails> getAllUsers() {
        UserDetails user = User.builder()
                .username("testuser")
                .password(encoder.encode("testpassword"))
                .build();
        return List.of(user);
    }
}
