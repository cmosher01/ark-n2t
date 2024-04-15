package nu.mine.mosher.net.ark_n2t;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark_n2t.util.Ark;
import nu.mine.mosher.net.ark_n2t.util.CharUtil;

import java.util.*;
import java.util.regex.Pattern;

@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uriRaw = Optional.ofNullable(request.getRequestURI()).orElse("");
        log.info("URI: \"{}\"", uriRaw);
        // "/ark:/12345/g8tst-tstk/foo.jpg"

        val uriClean = CharUtil.removeAllWhitespaceAndHyphens(uriRaw);
        if (!uriClean.equals(uriRaw)) {
            log.warn("URI standardized to: \"{}\"", uriClean);
        }

        val pat = Pattern.compile(
            "^/?ark:/?(\\p{Alnum}+)/([\\p{Alnum}_@~+*=$]+).*$",
            Pattern.CANON_EQ|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);

        val matcher = pat.matcher(uriClean);
        if (!matcher.matches()) {
            log.error("Invalid ARK format: \"{}\"", uriClean);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val naan = new Ark.Naan(matcher.group(1).toLowerCase());
        log.info("NAAN: \"{}\"", naan);
        val name = new Ark.Blade(matcher.group(2));
        // check against know shoulders, and create Ark

        try (val out = response.getWriter()) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("  <head>");
            out.println("    <title>ArkN2tServlet</title>");
            out.println("  </head>");
            out.println("  <body>");
            out.println("    <p>ArkN2tServlet OK</p>");
            out.println("  </body>");
            out.println("</html>");
        }
    }


}
