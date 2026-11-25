package com.jordanpaille.housing.coop.dao.announcement;

import com.jordanpaille.housing.coop.entity.Announcement;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
                    author,
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
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("body_markdown")
                ));
            }

            return announcements;
        });
    }
}
