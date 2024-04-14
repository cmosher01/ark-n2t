package nu.mine.mosher.net.ark_n2t.util;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class ArkID {
    public static final int DEFAULT_ID_LENGTH = 10;
    public static final String DEFAULT_SAMPLE_SPACE = "bcdfghjkmnpqrstvwxz23456789";
    public static final RandomGenerator DEFAULT_RNG = RandomGeneratorFactory.of("SecureRandom").create();



    private final int length;
    private final String sampleSpace;
    private final RandomGenerator RNG;

    public ArkID() {
        this(DEFAULT_ID_LENGTH, DEFAULT_SAMPLE_SPACE, DEFAULT_RNG);
    }

    public ArkID(final int length, final String sampleSpace, final RandomGenerator RNG) {
        this.length = length;
        this.sampleSpace = sampleSpace;
        this.RNG = RNG;
    }

    public String mint() {
        final StringBuilder sb = new StringBuilder(this.length);
        for (int i = 0; i < this.length; ++i) {
            final int random = this.RNG.nextInt(this.sampleSpace.length());
            final int cp = this.sampleSpace.codePointAt(random);
            sb.appendCodePoint(cp);
        }
        return sb.toString();
    }
}
