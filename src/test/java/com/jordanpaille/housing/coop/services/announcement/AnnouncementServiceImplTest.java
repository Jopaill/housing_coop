package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.dao.announcement.AnnouncementDao;
import com.jordanpaille.housing.coop.entity.Announcement;
import com.jordanpaille.housing.coop.entity.AnnouncementDoc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnouncementServiceImplTest {

    @Test
    void rendersMarkdownAndSanitizesHtml() {
        AnnouncementDao dao = () -> List.of(new Announcement(
                1L,
                LocalDate.of(2026, 11, 25),
                "Comité",
                "Annonce",
                "**Important** [Contactez-nous](mailto:contact@example.com) <script>alert('x')</script> [unsafe](javascript:alert(1))"
        ));
        AnnouncementService service = new AnnouncementServiceImpl(dao);

        AnnouncementDoc announcement = service.getAnnouncements().getFirst();

        assertTrue(announcement.bodyHtml().contains("<strong>Important</strong>"));
        assertTrue(announcement.bodyHtml().contains("mailto:contact@example.com"));
        assertFalse(announcement.bodyHtml().contains("<script>"));
        assertFalse(announcement.bodyHtml().contains("javascript:"));
    }

    @Test
    void skipsAnnouncementWithInvalidBodyAndContinuesRendering() {
        AnnouncementDao dao = () -> List.of(
                new Announcement(1L, LocalDate.of(2026, 11, 25), "Comité", "Invalid", null),
                new Announcement(2L, LocalDate.of(2026, 11, 24), "Comité", "Valid", "Body")
        );
        AnnouncementService service = new AnnouncementServiceImpl(dao);

        List<AnnouncementDoc> announcements = service.getAnnouncements();

        assertEquals(1, announcements.size());
        assertEquals("Valid", announcements.getFirst().title());
    }
}
