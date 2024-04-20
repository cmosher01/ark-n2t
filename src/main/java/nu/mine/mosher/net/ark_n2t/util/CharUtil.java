package nu.mine.mosher.net.ark_n2t.util;

import lombok.NonNull;
import lombok.val;

import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.Character.isWhitespace;
import static java.lang.Math.max;

public final class CharUtil {
    // implements the "Noid check digit algorithm"
    // https://metacpan.org/dist/Noid/view/noid#NOID-CHECK-DIGIT-ALGORITHM
    // input: "/"+naan+"/"+shoulder+blade
    public static int checksum(final String s, final String sampleSpace) {
        var prod = 0;
        for (int pos = 1; pos < s.length(); ++pos) {
            val chr = s.codePointAt(pos);
            val ord = max(0, sampleSpace.indexOf(chr));
            prod += pos*ord;
        }
        prod %= sampleSpace.length();
        return sampleSpace.codePointAt(prod);
    }

    // string from codepoint
    public static String scp(final int codepoint) {
        return new String(new int[] {codepoint}, 0, 1);
    }

    public static String getShoulderOf(final String s) {
        val pat = Pattern.compile("^(\\p{Alpha}+\\p{Digit}).*$");
        val mat = pat.matcher(s);
        if (!mat.matches()) {
            return "";
        }
        return mat.group(1);
    }

    public static String removeAllWhitespaceAndHyphens(@NonNull final String s) {
        val sb = new StringBuilder(s.length());
        s
            .codePoints()
            .filter(c -> !isWhitespace(c))
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
