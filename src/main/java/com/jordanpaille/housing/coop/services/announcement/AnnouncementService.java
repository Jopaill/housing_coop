package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.entity.AnnouncementDoc;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDoc> getAnnouncements();

    String renderMarkdownPreview(String bodyMarkdown);

    void publishPost(String title, String bodyMarkdown, String username);

    boolean deletePostById(long id);

    boolean canPublishPost(String username);
}
