package nu.mine.mosher.net.ark;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

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
            val random = this.rng.nextInt(this.alphabet.length());
            val cp = this.alphabet.at(random);
            sb.appendCodePoint(cp);
        }
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
