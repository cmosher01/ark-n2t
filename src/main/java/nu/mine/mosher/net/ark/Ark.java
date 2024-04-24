package nu.mine.mosher.net.ark;

import lombok.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Character.isWhitespace;
import static java.util.regex.Pattern.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Ark {
    private static final String LABEL = "ark";
    private static final Pattern PATTERN = Pattern.compile(
        "^/?"+LABEL+ ":/?(\\p{Alnum}+)/([\\p{Alnum}_@%~+*=$]+).*$",
        CANON_EQ | CASE_INSENSITIVE | UNICODE_CASE);

    final Naan naan;
    final ShoulderBlade shoulderBlade;
    final CheckDigit checkDigitActual;
    final CheckDigit checkDigitExpected;

    public static Optional<Ark> parse(@NonNull final String s, @NonNull final Alphabet alphabet, @NonNull final ChecksumAlgorithm check) {
        val matcher = PATTERN.matcher(normalize(s));
        if (!matcher.matches()) {
            return Optional.empty();
        }

        try {
            val naan = new Naan(matcher.group(1));
            val shoulderBladeCheck = decodePercentHex(matcher.group(2));
            val shoulderBlade = new ShoulderBlade(shoulderBladeCheck.substring(0, shoulderBladeCheck.length()-1));
            val checkDigitActual = new CheckDigit(shoulderBladeCheck.substring(shoulderBladeCheck.length()-1).codePointAt(0));
            val checkDigitExpected = check.checksum(naan, shoulderBlade, alphabet);
            return Optional.of(new Ark(naan, shoulderBlade, checkDigitActual, checkDigitExpected));
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

    public static Ark build(@NonNull final Naan naan, @NonNull final ShoulderBlade shoulderBlade, @NonNull final ChecksumAlgorithm check, @NonNull final Alphabet alphabet) {
        val checkDigit = check.checksum(naan, shoulderBlade, alphabet);
        return new Ark(naan, shoulderBlade, checkDigit, checkDigit);
    }

    public boolean hasValidCheckDigit() {
        return this.checkDigitActual.equals(this.checkDigitExpected);
    }


    // ark:{naan}/{shoulder-blade}{check-digit}
    @Override
    public String toString() {
        return LABEL + ":" + this.naan + "/" + this.shoulderBlade + this.checkDigitActual;
    }

    @Override
    public boolean equals(final Object object) {
        return
            object instanceof Ark that &&
            this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.naan, this.shoulderBlade, this.checkDigitActual);
    }

    public CheckDigit checkDigitExpected() {
        return this.checkDigitExpected;
    }

    public CheckDigit checkDigitActual() {
        return this.checkDigitActual;
    }



    static String normalize(@NonNull final String s) {
        val sb = new StringBuilder(s.length());
        s
                .codePoints()
                .filter(c -> !isWhitespace(c))
                .filter(c -> !isDash(c))
                .map(c -> c==76 || c==108 ? 49 : c) // "L" or "l" to "1"
                .map(c -> c==79 || c==111 ? 48 : c) // "O" or "o" to "0"
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

    static String decodePercentHex(final String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.US_ASCII);
        } catch (final Exception e) {
            // TODO what to do here?
            return s;
        }
    }
}
