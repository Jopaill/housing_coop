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
}
