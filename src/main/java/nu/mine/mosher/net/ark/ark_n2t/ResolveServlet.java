package nu.mine.mosher.net.ark.ark_n2t;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark.*;

import javax.naming.*;
import javax.sql.DataSource;
import java.net.*;
import java.sql.*;
import java.util.Optional;


@WebServlet("/resolve/*")
@Slf4j
public final class ResolveServlet extends HttpServlet {
//    private final Alphabet alphabet = Alphabet.RECOMMENDED; // TODO env var for alphabet
//    private final ChecksumAlgorithm check = new NoidChecksumAlgorithm(); // TODO env var for algorithm

    private NameMappingAuthority nma;

    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uriRaw = uri(request);
        log.info("URI: \"{}\"", uriRaw);

        val optArk = this.nma.parse(uriRaw);
        if (optArk.isEmpty()) {
            log.error("Invalid format ARK: {}", uriRaw);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val ark = optArk.get();
        if (!ark.hasValidCheckDigit()) {
            log.warn("Invalid checksum: expected={}, actual={}", ark.checkDigitExpected(), ark.checkDigitActual());
            if (!Optional.ofNullable(System.getenv("ARK_TRY_ON_BAD_CHECKSUM")).orElse("true").equalsIgnoreCase("true")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // TODO handle: "?", "??", "?info"

        val optUri = this.nma.resolve(ark);
        if (optUri.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val uri = optUri.get().toASCIIString();

//        response.setContentType("application/xhtml+xml;charset=UTF-8");
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        try (val out = response.getWriter()) {
//            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
//            out.println("  <head>");
//            out.println("    <title>ArkN2tServlet</title>");
//            out.println("  </head>");
//            out.println("  <body>");
//            out.println("    <p>ark:<![CDATA["+ark.toString()+"]]></p>");
//            out.println("    <p><a href=\""+uri+"\">"+uri+"</link></p>");
//            out.println("  </body>");
//            out.println("</html>");
//        }

        if (Optional.ofNullable(System.getenv("ARK_ACCEL")).orElse("false").equalsIgnoreCase("true")) {
            response.addHeader("X-Accel-Redirect", uri);
        } else {
            response.sendRedirect(uri);
        }
    }

    // TODO "Request strings too long for GET may be sent using HTTP's POST command"
    @Override
    @SneakyThrows
    public void doPost(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        super.doPost(request, response);
    }

//    /**
//     * Looks up the given ARK in the database, and returns its URL.
//     * Returns empty string if not found.
//     * In the case where database contains more than one record for the given
//     * ark, an arbitrary one is returned.
//     *
//     * @param ark in this form: {naan}/{shoulder-blade}{check-digit}
//     * @return uri
//     */
//    private Optional<URI> resolve(@NonNull final Ark ark) throws SQLException, URISyntaxException, NamingException {
//        log.info("Will try to resolve ark: \"{}\"", ark.toString());
//        try (val db = db(); val st = db.prepareStatement(
//            "SELECT url FROM Ark WHERE ark = ?")) {
//            st.setString(1, ark.toString());
//            try (val rs = st.executeQuery()) {
//                if (rs.next()) {
//                    log.info("Found Ark row in datastore.");
//                    val optStrUri = Optional.ofNullable(rs.getString("url"));
//                    if (optStrUri.isEmpty()) {
//                        log.warn("URL in datastore was blank.");
//                        return Optional.empty();
//                    }
//                    log.info("URL from datastore: {}", optStrUri.get());
//                    return Optional.of(new URI(optStrUri.get()));
//                } else {
//                    log.warn("Could not find Ark row in datastore.");
//                }
//            }
//        }
//        return Optional.empty();
//    }

    private static String uri(final HttpServletRequest request) {
        // requires proxy to supply this header, which allows us to get the
        // query string in the case where it is a single question mark
        var uri = Optional.ofNullable(request.getHeader("x-forwarded-uri"));
        if (uri.isPresent()) {
            return uri.get();
        }

        return
            Optional.ofNullable(request.getPathInfo()).orElse("")+
            Optional.ofNullable(request.getQueryString()).orElse("");
    }

//    private static Connection db() throws NamingException, SQLException {
//        return ds().getConnection();
//    }

    private static DataSource ds() throws NamingException {
        val ctx = new InitialContext();
        return (DataSource)ctx.lookup("java:/comp/env/jdbc/db");
    }
}
