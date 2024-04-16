package nu.mine.mosher.net.ark_n2t;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark_n2t.util.*;

import java.util.*;
import java.util.regex.Pattern;

@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    private NamespaceSubdivision ns;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        val naan = getNaanFromEnv();
        val ns = getShoulderFromEnv();

        this.ns = new NamespaceSubdivision(new NameAssigningAuthority(naan), ns);
    }

    private static Ark.Shoulder getShoulderFromEnv() {
        final Ark.Shoulder ns;
        val ark_ns = Optional.ofNullable(System.getenv("ARK_NS"));
        if (ark_ns.isEmpty()) {
            ns = Ark.Shoulder.GLOBAL;
            log.warn("Could not find ARK_NS (namespace \"shoulder\") environment variable; ARKs will not have shoulders.");
        } else {
            ns = new Ark.Shoulder(ark_ns.get());
            log.info("Will use namespace \"shoulder\" found in ARK_NS environment variable: \"{}\"", ns);
        }
        return ns;
    }

    private static Ark.Naan getNaanFromEnv() {
        final Ark.Naan naan;
        val akr_naan = Optional.ofNullable(System.getenv("ARK_NAAN"));
        if (akr_naan.isEmpty()) {
            naan = Ark.Naan.EXAMPLE;
            log.error("Could not find ARK_NAAN (name assigning authority number) environment variable; will use NAAN: \"{}\".", naan);
        } else {
            naan = new Ark.Naan(akr_naan.get());
            log.info("Will use NAAN found in ARK_NAAN environment variable: \"{}\"", naan);
        }
        return naan;
    }

    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uriRaw = Optional.ofNullable(request.getRequestURI()).orElse("");
        log.info("URI: \"{}\"", uriRaw);

        val uriClean = CharUtil.removeAllWhitespaceAndHyphens(uriRaw);
        if (!uriClean.equals(uriRaw)) {
            log.warn("URI cleaned: \"{}\"", uriClean);
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
        if (!this.ns.authority().isIdentifiedBy(naan)) {
            log.error("Received incorrect NAAN: {}", naan);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val baseName = matcher.group(2);
        if (!this.ns.isIdentifiedBy(Ark.Shoulder.of(baseName))) {
            log.error("Received incorrect shoulder on base name: {}", baseName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val blade = this.ns.removeShoulderFrom(baseName);
        // TODO verify checksum received

        val ark = new Ark(this.ns, blade);

        try (val out = response.getWriter()) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("  <head>");
            out.println("    <title>ArkN2tServlet</title>");
            out.println("  </head>");
            out.println("  <body>");
            out.println("    <p>normalized ARK: <![CDATA["+ark+"]]></p>");
            out.println("  </body>");
            out.println("</html>");
            out.flush();
        }
    }
}
