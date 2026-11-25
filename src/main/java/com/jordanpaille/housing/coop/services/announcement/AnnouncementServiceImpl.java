package com.jordanpaille.housing.coop.services.announcement;

import com.jordanpaille.housing.coop.dao.announcement.AnnouncementDao;
import com.jordanpaille.housing.coop.entity.Announcement;
import com.jordanpaille.housing.coop.entity.AnnouncementDoc;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final Parser MARKDOWN_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().escapeHtml(true).build();
    private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder()
            .allowElements("a", "blockquote", "br", "code", "del", "em", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "li", "ol", "p", "pre", "strong", "ul")
            .allowUrlProtocols("http", "https", "mailto")
            .allowAttributes("href").onElements("a")
            .requireRelNofollowOnLinks()
            .toFactory();

    private final AnnouncementDao announcementDao;

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

    private AnnouncementDoc toAnnouncementDoc(Announcement announcement) {
        if (announcement.publishedAt() == null
                || announcement.author() == null
                || announcement.title() == null
                || announcement.bodyMarkdown() == null) {
            throw new IllegalArgumentException("Announcement fields must not be null");
        }

        String renderedMarkdown = HTML_RENDERER.render(MARKDOWN_PARSER.parse(announcement.bodyMarkdown()));
        String safeBodyHtml = HTML_POLICY.sanitize(renderedMarkdown);

        return new AnnouncementDoc(
                announcement.publishedAt(),
                announcement.author(),
                announcement.title(),
                safeBodyHtml
        );
    }
}
