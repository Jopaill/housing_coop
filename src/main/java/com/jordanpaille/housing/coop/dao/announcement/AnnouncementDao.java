package com.jordanpaille.housing.coop.dao.announcement;

import com.jordanpaille.housing.coop.entity.Announcement;

import java.util.List;

public interface AnnouncementDao {

    List<Announcement> getAnnouncements();
}
