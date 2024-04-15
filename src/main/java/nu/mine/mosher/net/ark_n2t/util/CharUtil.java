package nu.mine.mosher.net.ark_n2t.util;

import lombok.NonNull;
import lombok.val;

import java.util.Set;

public final class CharUtil {
    public static String removeAllWhitespaceAndHyphens(@NonNull final String s) {
        val sb = new StringBuilder(s.length());
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
