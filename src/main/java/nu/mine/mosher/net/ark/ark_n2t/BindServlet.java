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

@WebServlet("/bind/*")
@Slf4j
public final class BindServlet extends HttpServlet {
    private final Alphabet alphabet = Alphabet.BETA_NUMERIC; // TODO env var for alphabet
    private final ChecksumAlgorithm check = new NoidChecksumAlgorithm(); // TODO env var for algorithm

    private Naan naan;
    private Minter minter;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        log.info("-------- HTTP servlet initialization --------");
        super.init(config);
        this.naan = getNaanFromEnv();
        val shoulder = getShoulderFromEnv(this.alphabet);
        this.minter = new Minter(shoulder, 10, this.alphabet, Minter.DEFAULT_RNG);
        log.info("-------- end of HTTP servlet initialization --------");
    }

    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uri = uri(request);
        val ark = bind(uri);
        response.sendRedirect("/"+ark);
    }

    private static Shoulder getShoulderFromEnv(final Alphabet alphabet) {
        final Shoulder shoulder;
        val envArkShoulder = Optional.ofNullable(System.getenv("ARK_SHOULDER"));
        if (envArkShoulder.isEmpty()) {
            shoulder = Shoulder.NULL;
            log.warn("Could not find ARK_NS (namespace \"shoulder\") environment variable; generated ARKs will not have shoulders.");
        } else {
            shoulder = new Shoulder(envArkShoulder.get(), alphabet);
            log.info("Will use namespace \"shoulder\" found in ARK_NS environment variable: \"{}\"", shoulder);
        }
        return shoulder;
    }

    private static Naan getNaanFromEnv() {
        final Naan naan;
        val envArkNaan = Optional.ofNullable(System.getenv("ARK_NAAN"));
        if (envArkNaan.isEmpty()) {
            naan = Naan.EXAMPLE;
            log.error(
                "Could not find ARK_NAAN (name assigning authority number) environment variable; " +
                "will use designated example NAAN: \"{}\".", naan);
        } else {
            naan = new Naan(envArkNaan.get());
            log.info("Will use NAAN found in ARK_NAAN environment variable: \"{}\"", naan);
        }
        return naan;
    }

    private String bind(final URI uri) throws SQLException, NamingException {
        return find(uri).orElse(mint(uri).toString());
    }

    /**
     * Mint a new ark for the given URL and add the mapping to the database.
     * Does not check if a mapping already exists.
     *
     * @param uri
     * @return ark in this form: {naan}/{shoulder-blade}{check-digit}
     */
    private Ark mint(@NonNull final URI uri) throws SQLException, NamingException {
        val ark = Ark.build(this.naan, this.minter.mint(), this.check, this.alphabet);
        try (val db = db(); val st = db.prepareStatement(
                "INSERT INTO Ark (ark, url) VALUES (?, ?)")) {
            st.setString(1, ark.toString());
            st.setString(2, uri.toASCIIString());
            st.executeUpdate();
        }
        return ark;
    }

    private Optional<String> find(final URI uri) throws SQLException, NamingException {
        try (
            val db = db();
            val st = db.prepareStatement("SELECT ark FROM Ark WHERE url = ?")) {
            st.setString(1, uri.toASCIIString());
            try (val rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("ark"));
                }
            }
        }
        return Optional.empty();
    }

    private static URI uri(final HttpServletRequest request) throws URISyntaxException {
        return new URI(request.getParameter("uri"));
    }

    private static Connection db() throws NamingException, SQLException {
        return ds().getConnection();
    }

    private static DataSource ds() throws NamingException {
        val ctx = new InitialContext();
        return (DataSource)ctx.lookup("java:/comp/env/jdbc/db");
    }
}
