package com.jordanpaille.housing.coop.dao.person;

import com.jordanpaille.housing.coop.entity.PersonName;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class PersonDaoImpl implements PersonDao {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<PersonName> findMemberNameByUsername(String username) {
        String sql = """
                SELECT
                    first_name,
                    last_name
                FROM
                    persons
                WHERE
                    username = :username
                    AND is_member = TRUE
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username);

        return jdbc.query(sql, params, rs -> {
            if (!rs.next()) {
                return Optional.empty();
            }

            PersonName personName = new PersonName(
                    rs.getString("first_name"),
                    rs.getString("last_name")
            );
            if (rs.next()) {
                throw new IllegalStateException("More than one member is associated with the authenticated username");
            }

            return Optional.of(personName);
        });
    }
}
