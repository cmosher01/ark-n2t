package nu.mine.mosher.net.ark_n2t;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        var uri = Optional.ofNullable(request.getRequestURI()).orElse("");
        // "/12345/ark:/g8tst-tstk/foo.jpg"
        uri = removeAllWhitespaceAndHyphens(uri);

        val pat = Pattern.compile(
            "/?([0123456789bcdfghjkmnpqrstvwxz]+)/ark:/?([0123456789bcdfghjkmnpqrstvwxz]+)*",
            Pattern.CANON_EQ|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);

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



    private static String removeAllWhitespaceAndHyphens(final String s) {
        var sb = new StringBuilder(s.length());
        s
            .codePoints()
            .filter(c -> !Character.isWhitespace(c))
            .filter(c -> !isDash(c))
            .forEach(sb::appendCodePoint);
        return sb.toString();
    }

    private static final Set<Integer> DASHES = Set.of(
        0x002d,0x058a,0x058b,0x1400,0x1806,0x2010,
        0x2011,0x2012,0x2013,0x2014,0x2015,0x2e17,
        0x2e1a,0x2e3a,0x2e3b,0x2e40,0x301c,0x3030,
        0x30a0,0xfe31,0xfe32,0xfe58,0xfe63,0xff0d,0x10ead);

    private static boolean isDash(final int c) {
        return DASHES.contains(c);
    }
}
