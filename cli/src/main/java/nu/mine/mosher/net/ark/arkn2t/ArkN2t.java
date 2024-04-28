package nu.mine.mosher.net.ark.arkn2t;

import lombok.val;
import nu.mine.mosher.gnopt.Gnopt;
import nu.mine.mosher.net.ark.*;

import java.net.URI;
import java.sql.SQLException;

public class ArkN2t {
    public static void main(final String... args) throws Gnopt.InvalidOption, SQLException {
        val opts = Gnopt.process(ArkN2tOptions.class, args);

        val ds = new SimpleDataSource(opts.database);

        val naa = new NameAssigningAuthority(ds, opts.naan, opts.shoulder, opts.lenBlade, opts.alphabet, opts.check);
//        val nma = new NameMappingAuthority(ds, naa);

        for (val url : opts.urls) {
            naa.bind(URI.create(url));
        }
    }
}
