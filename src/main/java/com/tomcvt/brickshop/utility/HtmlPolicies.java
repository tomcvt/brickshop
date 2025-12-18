package com.tomcvt.brickshop.utility;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlPolicies {
    private static final AttributePolicy safeUrlAttributePolicy = (elementName, attributeName, value) -> {
        String lowerCasedValue = value.toLowerCase();
        if (!lowerCasedValue.matches("[^<>\"'`]+")) {
            return null;
        }
        if (lowerCasedValue.startsWith("http:") || lowerCasedValue.startsWith("https:")) {
            return value;
        }
        return null;
    };
    private static final PolicyFactory HTML_POLICY_V1 = new HtmlPolicyBuilder()
            .allowCommonBlockElements()
            .allowCommonInlineFormattingElements()
            .allowElements("p", "ul", "ol", "li", "br")
            .allowElements("strong", "em", "b", "i")
            .allowElements("h1", "h2", "h3")
            .allowElements("a")
            .allowAttributes("href").matching(safeUrlAttributePolicy).onElements("a")
            .allowUrlProtocols("http", "https")
            .requireRelNofollowOnLinks()
            .toFactory().and(Sanitizers.IMAGES);
    public static String sanitizeHtmlV1(String html) {
        return HTML_POLICY_V1.sanitize(html);
    }
}
