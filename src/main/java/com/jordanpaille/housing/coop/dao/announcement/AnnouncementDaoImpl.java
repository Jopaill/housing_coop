package com.jordanpaille.housing.coop.dao.announcement;

import com.jordanpaille.housing.coop.entity.Announcement;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class AnnouncementDaoImpl implements AnnouncementDao {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Announcement> getAnnouncements() {
        String sql = """
                SELECT
                    announcement_id,
                    published_at,
                    author_name,
                    author_email,
                    title,
                    body_markdown
                FROM
                    announcements
                ORDER BY
                    published_at DESC,
                    announcement_id DESC
                """;

        return jdbc.query(sql, rs -> {
            List<Announcement> announcements = new ArrayList<>();

            while (rs.next()) {
                announcements.add(new Announcement(
                        rs.getLong("announcement_id"),
                        rs.getDate("published_at").toLocalDate(),
                        rs.getString("author_name"),
                        rs.getString("author_email"),
                        rs.getString("title"),
                        rs.getString("body_markdown")
                ));
            }

            return announcements;
        });
    }

    @Override
    public void insertAnnouncement(
            LocalDate publishedAt,
            String authorName,
            String username,
            String title,
            String bodyMarkdown) {
        String sql = """
                INSERT INTO announcements
                    (published_at, author_name, author_email, title, body_markdown)
                VALUES
                    (:published_at, :author_name, :author_email, :title, :body_markdown)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("published_at", Date.valueOf(publishedAt))
                .addValue("author_name", authorName)
                .addValue("author_email", username)
                .addValue("title", title)
                .addValue("body_markdown", bodyMarkdown);

        jdbc.update(sql, params);
    }

    @Override
    public Optional<Announcement> findAnnouncementById(long id) {
        String sql = """
                SELECT
                    announcement_id,
                    published_at,
                    author_name,
                    author_email,
                    title,
                    body_markdown
                FROM announcements
                WHERE announcement_id = :announcement_id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("announcement_id", id);

        List<Announcement> matches = jdbc.query(sql, params, rs -> {
            List<Announcement> announcements = new ArrayList<>();
            if (rs.next()) {
                announcements.add(new Announcement(
                        rs.getLong("announcement_id"),
                        rs.getDate("published_at").toLocalDate(),
                        rs.getString("author_name"),
                        rs.getString("author_email"),
                        rs.getString("title"),
                        rs.getString("body_markdown")
                ));
            }
            return announcements;
        });

        return matches.stream().findFirst();
    }

    @Override
    public boolean updateAnnouncementById(long id, String title, String bodyMarkdown) {
        String sql = """
                UPDATE announcements
                SET title = :title,
                    body_markdown = :body_markdown
                WHERE announcement_id = :announcement_id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", title)
                .addValue("body_markdown", bodyMarkdown)
                .addValue("announcement_id", id);

        return jdbc.update(sql, params) > 0;
    }

    @Override
    public boolean deleteAnnouncementById(long id) {
        String sql = "DELETE FROM announcements WHERE announcement_id = :announcement_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("announcement_id", id);

        return jdbc.update(sql, params) > 0;
    }
}
