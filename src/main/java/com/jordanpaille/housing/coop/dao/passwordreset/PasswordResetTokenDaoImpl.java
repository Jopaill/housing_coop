package com.jordanpaille.housing.coop.dao.passwordreset;

import com.jordanpaille.housing.coop.entity.PasswordResetToken;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;

@AllArgsConstructor
@Repository
public class PasswordResetTokenDaoImpl implements PasswordResetTokenDao {

    private final NamedParameterJdbcTemplate jdbc;

    public void save(PasswordResetToken token) {
        String sql = """
                INSERT INTO
                    password_reset_tokens
                (token_hash, username, expires_at, used)
                VALUES
                (:token_hash, :username, :expires_at, :used)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("token_hash", token.tokenHash())
                .addValue("username", token.userId())
                .addValue("expires_at", Timestamp.from(token.expiresAt()))
                .addValue("used", token.used());

        jdbc.update(sql, params);
    }


    public PasswordResetToken findByTokenHash(String tokenHash) {
        String sql = """
                SELECT
                     *
                FROM
                    password_reset_tokens
                WHERE
                    token_hash = :token_hash
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("token_hash", tokenHash);

        return jdbc.query(
                sql,
                params,
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }

                    return new PasswordResetToken(
                            rs.getLong("id"),
                            rs.getString("token_hash"),
                            rs.getTimestamp("expires_at").toInstant(),
                            rs.getBoolean("used"),
                            rs.getString("username"));
                }
        );
    }

    public boolean lockUserForReset(String userId) {
        String sql = """
                SELECT
                    username
                FROM
                    users
                WHERE
                    username = :username
                FOR UPDATE
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", userId);

        return jdbc.query(sql, params, ResultSet::next);
    }


    public long countCreatedSince(String userId, Instant since) {
        String sql = """
                SELECT
                    COUNT(*)
                FROM
                    password_reset_tokens
                WHERE
                    username = :username
                    AND created_at >= :since
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", userId)
                .addValue("since", Timestamp.from(since));

        Long count = jdbc.queryForObject(sql, params, Long.class);
        if (count == null) {
            throw new IllegalStateException("Password reset token count query returned no result");
        }
        return count;
    }


    public void markUsed(Long id) {
        String sql = """
                UPDATE
                    password_reset_tokens
                SET
                    used = TRUE
                WHERE
                    id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        jdbc.update(sql, params);
    }
}
