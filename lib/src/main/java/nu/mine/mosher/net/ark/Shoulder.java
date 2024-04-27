package nu.mine.mosher.net.ark;

import lombok.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Shoulder {
    public static final Shoulder NULL = new Shoulder();

    private static final Pattern VALID = Pattern.compile("^\\p{Alpha}+\\p{Digit}$");

//    public static Shoulder of(final String baseName) {
//        return new Shoulder(CharUtil.getShoulderOf(baseName));
//    }

//    public String removeFrom(final String sShoulderBladeChecksum) {
//        if (!sShoulderBladeChecksum.startsWith(this.s)) {
//            throw new IllegalStateException("Wrong shoulder; expected: " + this.s);
//        }
//        return sShoulderBladeChecksum.substring(this.s.length());
//    }

    @NonNull private final String s;

    private Shoulder() {
        this.s = "";
    }

    public Shoulder(final String s, final Alphabet alphabet) {
        this.s = valid(s, alphabet);
    }

    private static String valid(@NonNull String s, @NonNull final Alphabet alphabet) {
        s = s.strip();
        if (s.isEmpty() || !VALID.matcher(s.strip()).matches() || !alphabet.covers(s)) {
            throw new IllegalStateException("Invalid shoulder format.");
        }
        return s;
    }

    public boolean exists() {
        return !this.s.isEmpty();
    }

    @Override
    public String toString() {
        return this.s;
    }

    @Override
    public boolean equals(final Object object) {
        return
            object instanceof Shoulder that &&
            Objects.equals(this.s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.s);
    }
}
