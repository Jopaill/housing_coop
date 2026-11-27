package com.jordanpaille.housing.coop.entity;

import java.time.LocalDate;

public record AnnouncementDoc(
        LocalDate publishedAt,
        String authorName,
        String title,
        String bodyHtml
) {
}
