package nu.mine.mosher.net.ark;

import lombok.*;

import java.util.random.*;

@RequiredArgsConstructor
public final class Minter {
    public static final RandomGenerator DEFAULT_RNG = RandomGeneratorFactory.of("SecureRandom").create();

    @NonNull private final Shoulder shoulder;
    private final int lenBlade;
    @NonNull private final Alphabet alphabet;
    @NonNull private final RandomGenerator rng;

    private boolean primed = false;

    public ShoulderBlade mint() {
        prime();
        val sb = new StringBuilder();
        appendShoulder(sb);
        appendBlade(sb);
        return new ShoulderBlade(sb.toString());
    }

    private void appendShoulder(final StringBuilder sb) {
        sb.append(this.shoulder);
    }

    private void appendBlade(final StringBuilder sb) {
        for (int i = 0; i < this.lenBlade; ++i) {
            sb.appendCodePoint(rand());
        }
    }

    private int rand() {
        val rnd = this.rng.nextInt(this.alphabet.length());
        return this.alphabet.at(rnd);
    }

    private synchronized void prime() {
        if (!this.primed) {
            for (int i = 0; i < 2011; ++i) {
                this.rng.nextInt();
            }
            this.primed = true;
        }
    }
}
