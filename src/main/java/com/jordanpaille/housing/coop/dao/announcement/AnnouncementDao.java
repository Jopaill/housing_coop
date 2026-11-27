package com.jordanpaille.housing.coop.dao.announcement;

import com.jordanpaille.housing.coop.entity.Announcement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnnouncementDao {

    List<Announcement> getAnnouncements();

    void insertAnnouncement(
            LocalDate publishedAt,
            String authorName,
            String username,
            String title,
            String bodyMarkdown);

    Optional<Announcement> findAnnouncementById(long id);

    boolean updateAnnouncementById(long id, String title, String bodyMarkdown);

    boolean deleteAnnouncementById(long id);
}
