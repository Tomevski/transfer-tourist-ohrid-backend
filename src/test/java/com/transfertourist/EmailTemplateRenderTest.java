package com.transfertourist;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;
import com.transfertourist.event.BookingEmailPayload;
import com.transfertourist.event.ContactEmailPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Renders every booking email against both a one-way and a return payload.
 * Guards the Thymeleaf attribute-precedence gotcha where {@code th:replace}
 * runs before {@code th:if}, so a null return leg must be gated via a
 * conditional fragment expression rather than a sibling {@code th:if}.
 */
@SpringBootTest
@ActiveProfiles("test")
class EmailTemplateRenderTest {

    private static final String[] TEMPLATES = {
            "email/operator-new-booking",
            "email/customer-acknowledgement",
            "email/customer-confirm",
            "email/customer-decline",
    };

    @Autowired
    SpringTemplateEngine templateEngine;

    private BookingEmailPayload.LegSummary leg(String from, String to) {
        return new BookingEmailPayload.LegSummary(
                from, to, LocalDate.of(2026, 8, 1), "Arriving", "LH123", "12:30", null);
    }

    private BookingEmailPayload payload(BookingEmailPayload.LegSummary returnLeg) {
        return new BookingEmailPayload(
                "TT-ABC123", BookingStatus.PENDING,
                returnLeg == null ? TripType.ONE_WAY : TripType.RETURN,
                2, 1, "please call on arrival", "Sedan", new BigDecimal("55.00"),
                leg("Ohrid Airport", "Ohrid"), returnLeg,
                new BookingEmailPayload.CustomerSummary(
                        "Test User", "Test", "tomevskihristijan97@gmail.com", "+38970000000"));
    }

    private String render(String template, BookingEmailPayload booking) {
        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariable("booking", booking);
        return templateEngine.process(template, ctx);
    }

    @Test
    void rendersOneWayBookingForEveryTemplate() {
        for (String template : TEMPLATES) {
            String html = render(template, payload(null));
            assertThat(html).as("one-way %s", template)
                    .contains("Ohrid Airport")
                    .contains("TT-ABC123")
                    .doesNotContain("Return trip");
        }
    }

    @Test
    void rendersReturnBookingForEveryTemplate() {
        for (String template : TEMPLATES) {
            String html = render(template, payload(leg("Ohrid", "Ohrid Airport")));
            assertThat(html).as("return %s", template)
                    .contains("Ohrid Airport")
                    .contains("Return trip");
        }
    }

    @Test
    void rendersBothContactTemplates() {
        ContactEmailPayload contact = new ContactEmailPayload(
                "Jane Doe", "Jane", "jane@example.com",
                "Do you cover the airport at midnight?",
                "Mon, 1 Jan 2026 at 09:00 UTC");
        for (String template : new String[]{
                "email/operator-contact-message",
                "email/customer-contact-acknowledgement"}) {
            Context ctx = new Context(Locale.ENGLISH);
            ctx.setVariable("contact", contact);
            String html = templateEngine.process(template, ctx);
            assertThat(html).as("contact %s", template)
                    .contains("Do you cover the airport at midnight?");
        }
    }
}
