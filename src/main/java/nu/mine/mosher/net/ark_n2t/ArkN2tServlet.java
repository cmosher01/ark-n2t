package nu.mine.mosher.net.ark_n2t;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark_n2t.util.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static nu.mine.mosher.net.ark_n2t.util.CharUtil.scp;

@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    private NamespaceSubdivision ns;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        log.info("-------- HTTP servlet initialization --------");
        super.init(config);

        val naan = getNaanFromEnv();
        val ns = getShoulderFromEnv();

        this.ns = new NamespaceSubdivision(new NameAssigningAuthority(naan), ns);
        log.info("-------- end of HTTP servlet initialization --------");
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

        if (uriRaw.equalsIgnoreCase("/health")) {
            return;
        }

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

        val sShoulderBladeChecksum = matcher.group(2);

        val sShoulder = Ark.Shoulder.of(sShoulderBladeChecksum);
        log.info("Received shoulder: {}", sShoulder);
        if (!this.ns.isIdentifiedBy(sShoulder)) {
            log.error("Received incorrect shoulder on base name: {}", sShoulderBladeChecksum);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val sBladeChecksum = this.ns.removeShoulderFrom(sShoulderBladeChecksum);
        val sCheckActual = sBladeChecksum.substring(sBladeChecksum.length()-1);
        val sBlade = sBladeChecksum.substring(0, sBladeChecksum.length()-1);

        if (!this.ns.authority().minter().couldHaveMinted(sBlade)) {
            log.error("Received invalid ARK (blade not from this minter): {}", sBlade);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val prefix = "/"+this.ns.authority().number()+"/"+this.ns.shoulder();
        val sCheckExpected = scp(CharUtil.checksum(prefix+sBlade, this.ns.authority().minter().sampleSpace()));
        if (!sCheckActual.equals(sCheckExpected)) {
            log.error("Received incorrect checksum. actual=\"{}\", expected=\"{}\"", sCheckActual, sCheckExpected);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val ark = new Ark(this.ns, new Ark.Blade(sBlade));

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

    private Ark purl(final URI uri) throws SQLException {
        return find(uri).orElse(mint(uri));
    }

    /**
     * Mint a new ark for the given URL and add the mapping to the database.
     * Does not check if a mapping already exists.
     *
     * @param uri
     * @return ark in this form: {naan}/[{shoulder}]{blade}{check-digit}
     */
    private Ark mint(final URI uri) throws SQLException {
        val ark = this.ns.mint();
        try (
            val db = db();
            val st = db.prepareStatement("INSERT INTO Ark (ark, shoulder, url) VALUES (?, ?, ?)")) {
            st.setString(1, ark.toString());
            st.setInt(2, this.ns.shoulder().length());
            st.setString(3, uri.toASCIIString());
            st.executeUpdate();
        }
        return ark;
    }

    /**
     * Looks up the given ARK in the database, and returns its URL.
     * Returns empty string if not found.
     * In the case where database contains more than one record for the given
     * ark, an arbitrary one is returned.
     *
     * @param ark in this form: {naan}/[{shoulder}]{blade}{check-digit}
     * @return uri
     */
    private Optional<URI> resolve(final Ark ark) throws SQLException, URISyntaxException {
        try (
            val db = db();
            val st = db.prepareStatement("SELECT url FROM Ark WHERE ark = ?")) {
            st.setString(1, ark.toString());
            try (val rs = st.executeQuery()) {
                if (rs.next()) {
                    val optStrUri = Optional.ofNullable(rs.getString("url"));
                    if (optStrUri.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(new URI(optStrUri.get()));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Ark> find(final URI uri) throws SQLException {
        try (
            val db = db();
            val st = db.prepareStatement("SELECT ark, shoulder FROM Ark WHERE url = ?")) {
            st.setString(1, uri.toASCIIString());
            try (val rs = st.executeQuery()) {
                if (rs.next()) {
                    val optStrArk = Optional.ofNullable(rs.getString("ark"));
                    if (optStrArk.isEmpty()) {
                        return Optional.empty();
                    }
                    val ark = optStrArk.get();
                    val lenShoulder = rs.getInt("shoulder");
                    val sShoulder = ark.substring(0, lenShoulder);
                    if (!this.ns.isIdentifiedBy(new Ark.Shoulder(sShoulder))) {
                        log.error("Shoulder in database not handled by this servlet: {}", sShoulder);
                        return Optional.empty();
                    }
                    return this.ns.parseArk(ark.substring(lenShoulder));
                }
            }
        }
        return Optional.empty();
    }

    private static Connection db() {
        try {
            val ctx = new InitialContext();
            val ds = (DataSource)ctx.lookup("java:/comp/env/jdbc/db");
            return ds.getConnection();
        } catch (final Exception wrap) {
            throw new IllegalStateException(wrap);
        }
    }
}
