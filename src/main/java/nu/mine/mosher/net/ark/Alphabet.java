package nu.mine.mosher.net.ark;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public final class Alphabet {
    public static final Alphabet ALPHA_NUMERIC = new Alphabet("0123456789abcdefghijklmnopqrstuvwxyz");
    public static final Alphabet BETA_NUMERIC = new Alphabet("0123456789bcdfghjkmnpqrstvwxz");
    public static final Alphabet GAMMA_NUMERIC = new Alphabet("2345678bcdfhjkmnpqstvwx");
    public static final Alphabet DELTA_NUMERIC = new Alphabet("2347cdfhjkmnpqtvwx");
    public static final Alphabet ALPHA = new Alphabet("abcdefghijklmnopqrstuvwxyz");
    public static final Alphabet NUMERIC = new Alphabet("0123456789");

    public static final Alphabet RECOMMENDED = BETA_NUMERIC;

    private final List<Integer> r;

    public Alphabet(@NonNull final String s) {
        // TODO check for duplicates, reserved characters, anything else?
        this.r = List.copyOf(s.strip().codePoints().boxed().collect(Collectors.toList()));
        if (this.r.isEmpty()) {
            throw new IllegalArgumentException("Alphabet cannot be empty.");
        }
    }

    public int length() {
        return this.r.size();
    }

    public int at(final int index) {
        return this.r.get(index);
    }

    // returns index of, or -1 if not found
    public int indexOf(final int codepoint) {
        return this.r.indexOf(codepoint);
    }

    // checks to make sure the given string contains only characters from this alphabet
    public boolean covers(@NonNull final String s) {
        val r = s.codePoints().boxed().collect(Collectors.toList());
        r.removeAll(this.r);
        return r.isEmpty();
    }
}
