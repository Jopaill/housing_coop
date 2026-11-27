package com.jordanpaille.housing.coop.dao.announcement;

import com.jordanpaille.housing.coop.entity.Announcement;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementDao {

    List<Announcement> getAnnouncements();

    void insertAnnouncement(
            LocalDate publishedAt,
            String authorName,
            String username,
            String title,
            String bodyMarkdown);
}
