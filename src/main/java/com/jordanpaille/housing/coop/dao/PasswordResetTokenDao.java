package com.jordanpaille.housing.coop.dao;

import com.jordanpaille.housing.coop.entity.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class PasswordResetTokenDao {

    private final JdbcTemplate jdbc;

    public PasswordResetTokenDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(PasswordResetToken token) {
        jdbc.update(
                "INSERT INTO password_reset_tokens (token_hash, user_id, expires_at, used) VALUES (?, ?, ?, ?)",
                token.getTokenHash(),
                token.getUserId(),
                Timestamp.from(token.getExpiresAt()),
                token.isUsed()
        );
    }

    public PasswordResetToken findByTokenHash(String tokenHash) {
        return jdbc.query(
                "SELECT * FROM password_reset_tokens WHERE token_hash = ?",
                rs -> {
                    if (!rs.next()) return null;
                    PasswordResetToken t = new PasswordResetToken();
                    t.setId(rs.getLong("id"));
                    t.setTokenHash(rs.getString("token_hash"));
                    t.setUserId(rs.getString("user_id"));
                    t.setExpiresAt(rs.getTimestamp("expires_at").toInstant());
                    t.setUsed(rs.getBoolean("used"));
                    return t;
                },
                tokenHash
        );
    }

    public void markUsed(Long id) {
        jdbc.update("UPDATE password_reset_tokens SET used = TRUE WHERE id = ?", id);
    }
}
