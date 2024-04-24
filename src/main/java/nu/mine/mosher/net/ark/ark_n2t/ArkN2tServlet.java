package nu.mine.mosher.net.ark_n2t;

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


@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    private final Alphabet alphabet = Alphabet.BETA_NUMERIC; // TODO env var for alphabet
    private final ChecksumAlgorithm check = new NoidChecksumAlgorithm(); // TODO env var for algorithm
    private Naan naan;
    private Shoulder shoulder;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        log.info("-------- HTTP servlet initialization --------");
        super.init(config);
        getNaanFromEnv();
        getShoulderFromEnv();
        log.info("-------- end of HTTP servlet initialization --------");
    }

    private void getShoulderFromEnv() {
        val envArkShoulder = Optional.ofNullable(System.getenv("ARK_SHOULDER"));
        if (envArkShoulder.isEmpty()) {
            this.shoulder = Shoulder.NULL;
            log.warn("Could not find ARK_NS (namespace \"shoulder\") environment variable; generated ARKs will not have shoulders.");
        } else {
            this.shoulder = new Shoulder(envArkShoulder.get(), this.alphabet);
            log.info("Will use namespace \"shoulder\" found in ARK_NS environment variable: \"{}\"", this.shoulder);
        }
    }

    private void getNaanFromEnv() {
        val envArkNaan = Optional.ofNullable(System.getenv("ARK_NAAN"));
        if (envArkNaan.isEmpty()) {
            this.naan = Naan.EXAMPLE;
            log.error(
                "Could not find ARK_NAAN (name assigning authority number) environment variable; " +
                "will use designated example NAAN: \"{}\".", this.naan);
        } else {
            this.naan = new Naan(envArkNaan.get());
            log.info("Will use NAAN found in ARK_NAAN environment variable: \"{}\"", this.naan);
        }
    }

    // TODO "Request strings too long for GET may be sent using HTTP's POST command"
    @Override
    @SneakyThrows
    public void doPost(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        super.doPost(request, response);
    }

    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uriRaw = Optional.ofNullable(request.getRequestURI()).orElse("");
        log.info("URI: \"{}\"", uriRaw);

        if (uriRaw.equalsIgnoreCase("/health")) {
            return;
        }



        val optArk = Ark.parse(uriRaw, this.alphabet, this.check);
        if (optArk.isEmpty()) {
            log.error("Invalid format ARK: {}", uriRaw);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val ark = optArk.get();
        if (!ark.hasValidCheckDigit()) {
            log.warn("Invalid checksum: expected={}, actual={}", ark.checkDigitExpected(), ark.checkDigitActual());
            if (System.getenv("ARK_TRY_ON_BAD_CHECKSUM") == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // TODO handle: "?", "??", "?info"

        val optUri = resolve(ark);
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

        if (System.getenv("ARK_ACCEL") != null) {
            response.addHeader("X-Accel-Redirect", uri);
        } else {
            response.sendRedirect(uri);
        }
    }

//    private Ark purl(final URI uri) throws SQLException {
//        return find(uri).orElse(mint(uri));
//    }

    /**
     * Mint a new ark for the given URL and add the mapping to the database.
     * Does not check if a mapping already exists.
     *
     * @param uri
     * @return ark in this form: {naan}/[{shoulder}]{blade}{check-digit}
     */
//    private Ark mint(final URI uri) throws SQLException {
//        val ark = this.shoulder.mint();
//        try (
//            val db = db();
//            val st = db.prepareStatement("INSERT INTO Ark (ark, shoulder, url) VALUES (?, ?, ?)")) {
//            st.setString(1, ark.toString());
//            st.setInt(2, this.shoulder.shoulder().length());
//            st.setString(3, uri.toASCIIString());
//            st.executeUpdate();
//        }
//        return ark;
//    }

    /**
     * Looks up the given ARK in the database, and returns its URL.
     * Returns empty string if not found.
     * In the case where database contains more than one record for the given
     * ark, an arbitrary one is returned.
     *
     * @param ark in this form: {naan}/[{shoulder}]{blade}{check-digit}
     * @return uri
     */
    private Optional<URI> resolve(final Ark ark) throws SQLException, URISyntaxException, NamingException {
        log.info("Will try to resolve ark: \"{}\"", ark.toString());
        try (
            val db = db();
            val st = db.prepareStatement("SELECT url FROM Ark WHERE ark = ?")) {
            st.setString(1, ark.toString());
            try (val rs = st.executeQuery()) {
                if (rs.next()) {
                    log.info("Found Ark row in datastore.");
                    val optStrUri = Optional.ofNullable(rs.getString("url"));
                    if (optStrUri.isEmpty()) {
                        log.warn("URL in datastore was blank.");
                        return Optional.empty();
                    }
                    log.info("URL from datastore: {}", optStrUri.get());
                    return Optional.of(new URI(optStrUri.get()));
                } else {
                    log.warn("Could not find Ark row in datastore.");
                }
            }
        }
        return Optional.empty();
    }

//    private Optional<Ark> find(final URI uri) throws SQLException {
//        try (
//            val db = db();
//            val st = db.prepareStatement("SELECT ark, shoulder FROM Ark WHERE url = ?")) {
//            st.setString(1, uri.toASCIIString());
//            try (val rs = st.executeQuery()) {
//                if (rs.next()) {
//                    val optStrArk = Optional.ofNullable(rs.getString("ark"));
//                    if (optStrArk.isEmpty()) {
//                        return Optional.empty();
//                    }
//                    val ark = optStrArk.get();
//                    val lenShoulder = rs.getInt("shoulder");
//                    val sShoulder = ark.substring(0, lenShoulder);
//                    if (!this.shoulder.isIdentifiedBy(new Shoulder(sShoulder))) {
//                        log.error("Shoulder in database not handled by this servlet: {}", sShoulder);
//                        return Optional.empty();
//                    }
//                    return this.shoulder.parseArk(ark.substring(lenShoulder));
//                }
//            }
//        }
//        return Optional.empty();
//    }

    private static Connection db() throws NamingException, SQLException {
        return ds().getConnection();
    }

    private static DataSource ds() throws NamingException {
        val ctx = new InitialContext();
        return (DataSource)ctx.lookup("java:/comp/env/jdbc/db");
    }
}
