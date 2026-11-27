package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.dao.announcement.AnnouncementDao;
import com.jordanpaille.housing.coop.dao.person.PersonDao;
import com.jordanpaille.housing.coop.entity.Announcement;
import com.jordanpaille.housing.coop.entity.AnnouncementDoc;
import com.jordanpaille.housing.coop.entity.PersonName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final int MAX_TITLE_LENGTH = 40;
    private static final int MAX_BODY_LENGTH = 4000;
    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().escapeHtml(true).build();
    private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder()
            .allowElements("a", "blockquote", "br", "code", "del", "em", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "li", "ol", "p", "pre", "strong", "ul")
            .allowUrlProtocols("http", "https", "mailto")
            .allowAttributes("href").onElements("a")
            .requireRelNofollowOnLinks()
            .toFactory();

    private final AnnouncementDao announcementDao;
    private final PersonDao personDao;

    @Override
    public List<AnnouncementDoc> getAnnouncements() {
        List<AnnouncementDoc> announcements = new ArrayList<>();

        for (Announcement announcement : announcementDao.getAnnouncements()) {
            if (announcement == null) {
                log.warn("Skipping a null announcement returned by the DAO");
                continue;
            }

            try {
                announcements.add(toAnnouncementDoc(announcement));
            } catch (IllegalArgumentException exception) {
                log.warn("Skipping announcement {} because its content could not be rendered", announcement.id(), exception);
            }
        }

        return List.copyOf(announcements);
    }

    @Override
    public String renderMarkdownPreview(String bodyMarkdown) {
        if (bodyMarkdown == null) {
            throw new IllegalArgumentException("Le texte est obligatoire.");
        }
        if (bodyMarkdown.length() > MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("Le texte ne peut pas dépasser 4 000 caractères.");
        }
        validateNoHeadings(bodyMarkdown);

        return renderSafeMarkdown(bodyMarkdown);
    }

    @Override
    public void publishPost(String title, String bodyMarkdown, String username) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Le titre ne peut pas dépasser 40 caractères.");
        }
        if (bodyMarkdown == null || bodyMarkdown.isBlank()) {
            throw new IllegalArgumentException("Le texte est obligatoire.");
        }
        if (bodyMarkdown.length() > MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("Le texte ne peut pas dépasser 4 000 caractères.");
        }
        validateNoHeadings(bodyMarkdown);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le compte connecté est introuvable.");
        }

        PersonName author = personDao.findMemberNameByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Votre compte n'est pas associé à un membre."));
        announcementDao.insertAnnouncement(
                LocalDate.now(),
                author.firstName() + " " + author.lastName(),
                username,
                title.strip(),
                bodyMarkdown
        );
    }

    @Override
    public boolean canModifyAnnouncementById(long id, String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        return announcementDao.findAnnouncementById(id)
                .map(announcement -> username.equals(announcement.authorEmail()))
                .orElse(false);
    }

    @Override
    public boolean modifyAnnouncementById(long id, String title, String bodyMarkdown, String username) {
        if (!canModifyAnnouncementById(id, username)) {
            return false;
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Le titre ne peut pas dépasser 40 caractères.");
        }
        if (bodyMarkdown == null || bodyMarkdown.isBlank()) {
            throw new IllegalArgumentException("Le texte est obligatoire.");
        }
        if (bodyMarkdown.length() > MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("Le texte ne peut pas dépasser 4 000 caractères.");
        }
        validateNoHeadings(bodyMarkdown);

        return announcementDao.updateAnnouncementById(id, title.strip(), bodyMarkdown);
    }

    @Override
    public boolean deleteAnnouncementById(long id, String username) {
        if (!canModifyAnnouncementById(id, username)) {
            return false;
        }
        return announcementDao.deleteAnnouncementById(id);
    }

    @Override
    public boolean deletePostById(long id) {
        return announcementDao.deleteAnnouncementById(id);
    }

    @Override
    public boolean canPublishPost(String username) {
        return username != null
                && !username.isBlank()
                && personDao.findMemberNameByUsername(username).isPresent();
    }

    private AnnouncementDoc toAnnouncementDoc(Announcement announcement) {
        if (announcement.publishedAt() == null
                || announcement.authorName() == null
                || announcement.title() == null
                || announcement.bodyMarkdown() == null) {
            throw new IllegalArgumentException("Announcement fields must not be null");
        }

        return new AnnouncementDoc(
                announcement.id(),
                announcement.publishedAt(),
                announcement.authorName(),
                announcement.title(),
                renderSafeMarkdown(announcement.bodyMarkdown()),
                announcement.bodyMarkdown()
        );
    }

    private String renderSafeMarkdown(String bodyMarkdown) {
        String renderedMarkdown = HTML_RENDERER.render(MARKDOWN_PARSER.parse(bodyMarkdown));
        return HTML_POLICY.sanitize(renderedMarkdown);
    }

    private void validateNoHeadings(String bodyMarkdown) {
        if (containsHeading(MARKDOWN_PARSER.parse(bodyMarkdown))) {
            throw new IllegalArgumentException("Les titres Markdown ne sont pas autorisés dans le texte de l'annonce.");
        }
    }

    private boolean containsHeading(Node parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNext()) {
            if (child instanceof Heading || containsHeading(child)) {
                return true;
            }
        }
        return false;
    }
}
