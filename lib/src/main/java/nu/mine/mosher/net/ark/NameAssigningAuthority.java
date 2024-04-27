package nu.mine.mosher.net.ark;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.*;
import java.util.*;

@Slf4j
public final class NameAssigningAuthority {
    public static final class Number {
        private static final Alphabet ALLOWABLE = Alphabet.BETA_NUMERIC;

        public static final Number TERM = new Number("99152");
        public static final Number AGENT = new Number("99166");
        public static final Number EXAMPLE = new Number("12345");
        public static final Number TEST = new Number("99999");

        @NonNull
        private final String s;

        public Number(@NonNull String s) {
            s = s.strip().toLowerCase();
            if (!ALLOWABLE.covers(s)) {
                throw new IllegalStateException("Invalid NAAN format.");
            }
            this.s = s;
        }

        @Override
        public String toString() {
            return this.s;
        }

        @Override
        public boolean equals(final Object object) {
            return
                object instanceof Number that &&
                Objects.equals(this.s, that.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.s);
        }
    }








    private final DataSource dataSource;
    private final Number naan;
    private final Alphabet alphabet;
    private final ChecksumAlgorithm check;
    private final Minter mint;



    public NameAssigningAuthority(@NonNull final DataSource dataSource, @NonNull final Number naan) {
        this(dataSource, naan, Shoulder.NULL, 10, Alphabet.RECOMMENDED, new NoidChecksumAlgorithm());
    }

    public NameAssigningAuthority(@NonNull final DataSource dataSource, @NonNull final Number naan, @NonNull final Shoulder shoulder, final int lenBlade, @NonNull final Alphabet alphabet, @NonNull final ChecksumAlgorithm check) {
        this.dataSource = dataSource;
        this.naan = naan;
        this.alphabet = alphabet;
        this.check = check;
        this.mint = new Minter(shoulder, lenBlade, this.alphabet, Minter.DEFAULT_RNG);
    }


    /**
     * Finds existing ark bound to the given URL, or mints a new one and binds it to the given URL.
     * @param uri
     * @return ark (existing or new)
     * @throws SQLException
     */
    public Ark bind(@NonNull final URI uri) throws SQLException {
        return find(uri).orElse(mint(uri));
    }

    /**
     * Finds any existing ARK for the given URL
     * @param uri
     * @return ark
     * @throws SQLException
     */
    public Optional<Ark> find(@NonNull final URI uri) throws SQLException {
        try (
            val db = db();
            val st = db.prepareStatement("SELECT ark FROM Ark WHERE url = ?")) {
            st.setString(1, uri.toASCIIString());
            try (val rs = st.executeQuery()) {
                if (rs.next()) {
                    return parse(rs.getString("ark"));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Mints a new ark for the given URL and add the mapping to the database.
     * Does not check if a mapping already exists.
     *
     * @param uri
     * @return ark
     */
    public Ark mint(@NonNull final URI uri) throws SQLException {
        val ark = Ark.build(this.naan, this.mint.mint(), this.alphabet, this.check);
        try (val db = db(); val st = db.prepareStatement(
            "INSERT INTO Ark (ark, url) VALUES (?, ?)")) {
            st.setString(1, ark.toString());
            st.setString(2, uri.toASCIIString());
            st.executeUpdate();
        }
        return ark;
    }

    public Optional<Ark> parse(@NonNull final String s) {
        return Ark.parse(s, this.alphabet, this.check);
    }



    private Connection db() throws SQLException {
        return this.dataSource.getConnection();
    }
}
