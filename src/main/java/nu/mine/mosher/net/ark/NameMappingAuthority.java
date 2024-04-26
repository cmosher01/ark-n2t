package nu.mine.mosher.net.ark;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.net.*;
import java.sql.*;
import java.util.Optional;

@Slf4j
public final class NameMappingAuthority {
    private final DataSource dataSource;
    private final Optional<NameAssigningAuthority> optNaa;

    public NameMappingAuthority(@NonNull final DataSource dataSource) {
        this.dataSource = dataSource;
        this.optNaa = Optional.empty();
    }

    public NameMappingAuthority(@NonNull final DataSource dataSource, @NonNull final NameAssigningAuthority naa) {
        this.dataSource = dataSource;
        this.optNaa = Optional.of(naa);
    }

    /**
     * Looks up the given ARK in the database, and returns its URL.
     * Returns empty string if not found.
     * In the case where database contains more than one record for the given
     * ark, an arbitrary one is returned.
     *
     * @param ark in this form: {naan}/{shoulder-blade}{check-digit}
     * @return uri
     */
    public Optional<URI> resolve(@NonNull final Ark ark) throws SQLException, URISyntaxException {
        log.info("Will try to resolve ark: \"{}\"", ark.toString());
        try (val db = db(); val st = db.prepareStatement(
            "SELECT url FROM Ark WHERE ark = ?")) {
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

    public Optional<Ark> parse(@NonNull final String s) {
        if (this.optNaa.isPresent()) {
            return this.optNaa.get().parse(s);
        }
        return Ark.parseWeak(s);
    }

    private Connection db() throws SQLException {
        return this.dataSource.getConnection();
    }
}
