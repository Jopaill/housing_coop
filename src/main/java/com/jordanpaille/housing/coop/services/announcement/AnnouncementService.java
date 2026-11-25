package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.entity.AnnouncementDoc;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDoc> getAnnouncements();
}
