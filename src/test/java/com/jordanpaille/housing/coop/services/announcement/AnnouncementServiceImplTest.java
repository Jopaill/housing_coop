package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.dao.announcement.AnnouncementDao;
import com.jordanpaille.housing.coop.dao.person.PersonDao;
import com.jordanpaille.housing.coop.entity.Announcement;
import com.jordanpaille.housing.coop.entity.AnnouncementDoc;
import com.jordanpaille.housing.coop.entity.PersonName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnouncementServiceImplTest {

    @Test
    void rendersMarkdownAndSanitizesHtml() {
        AnnouncementDao dao = mock(AnnouncementDao.class);
        when(dao.getAnnouncements()).thenReturn(List.of(new Announcement(
                1L,
                LocalDate.of(2026, 11, 25),
                "Comité",
                "Annonce",
                "**Important** [Contactez-nous](mailto:contact@example.com) <script>alert('x')</script> [unsafe](javascript:alert(1))"
        )));
        AnnouncementService service = new AnnouncementServiceImpl(dao, username -> Optional.empty());

        AnnouncementDoc announcement = service.getAnnouncements().getFirst();

        assertTrue(announcement.bodyHtml().contains("<strong>Important</strong>"));
        assertTrue(announcement.bodyHtml().contains("mailto:contact@example.com"));
        assertFalse(announcement.bodyHtml().contains("<script>"));
        assertFalse(announcement.bodyHtml().contains("javascript:"));
    }

    @Test
    void skipsAnnouncementWithInvalidBodyAndContinuesRendering() {
        AnnouncementDao dao = mock(AnnouncementDao.class);
        when(dao.getAnnouncements()).thenReturn(List.of(
                new Announcement(1L, LocalDate.of(2026, 11, 25), "Comité", "Invalid", null),
                new Announcement(2L, LocalDate.of(2026, 11, 24), "Comité", "Valid", "Body")
        ));
        AnnouncementService service = new AnnouncementServiceImpl(dao, username -> Optional.empty());

        List<AnnouncementDoc> announcements = service.getAnnouncements();

        assertEquals(1, announcements.size());
        assertEquals("Valid", announcements.getFirst().title());
    }

    @Test
    void publishesWithTheAuthenticatedMembersNameAndCurrentDate() {
        AnnouncementDao dao = mock(AnnouncementDao.class);
        PersonDao personDao = username -> Optional.of(new PersonName("Marie", "Morin"));
        AnnouncementService service = new AnnouncementServiceImpl(dao, personDao);
        LocalDate today = LocalDate.now();

        service.publishPost("Assemblée", "**Bienvenue**", "marie@example.com");

        verify(dao).insertAnnouncement(today, "Marie Morin", "Assemblée", "**Bienvenue**");
    }

    @Test
    void rejectsPostExceedingBackendLengthLimits() {
        AnnouncementDao dao = mock(AnnouncementDao.class);
        AnnouncementService service = new AnnouncementServiceImpl(
                dao,
                username -> Optional.of(new PersonName("Marie", "Morin"))
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.publishPost("T".repeat(41), "Texte", "marie@example.com"));
        assertThrows(IllegalArgumentException.class, () ->
                service.publishPost("Titre", "T".repeat(4001), "marie@example.com"));
        verify(dao, never()).insertAnnouncement(any(), any(), any(), any());
    }
}
