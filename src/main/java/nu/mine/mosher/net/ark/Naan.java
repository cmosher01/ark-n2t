package nu.mine.mosher.net.ark;

import lombok.NonNull;

import java.util.Objects;

public final class Naan {
    private static final Alphabet ALLOWABLE = Alphabet.BETA_NUMERIC;

    public static final Naan TERM = new Naan("99152");
    public static final Naan AGENT = new Naan("99166");
    public static final Naan EXAMPLE = new Naan("12345");
    public static final Naan TEST = new Naan("99999");

    @NonNull private final String s;

    public Naan(@NonNull String s) {
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
            object instanceof Naan that &&
            Objects.equals(this.s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.s);
    }
}
