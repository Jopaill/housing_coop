package com.jordanpaille.housing.coop.entity;

import java.time.LocalDate;

public record Announcement(
        long id,
        LocalDate publishedAt,
        String author,
        String title,
        String bodyMarkdown
) {
}
