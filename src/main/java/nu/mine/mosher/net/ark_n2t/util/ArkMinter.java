package nu.mine.mosher.net.ark_n2t.util;

import lombok.*;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static java.lang.Math.max;



@RequiredArgsConstructor
public class ArkMinter {
    public static final int DEFAULT_BLADE_LENGTH = 10;
    public static final String DEFAULT_SAMPLE_SPACE = "bcdfghjkmnpqrstvwxz23456789";
    public static final RandomGenerator DEFAULT_RNG = RandomGeneratorFactory.of("SecureRandom").create();

    public boolean couldHaveMinted(final String sBlade) {
        if (sBlade.length() != this.lenBlade) {
            return false;
        }

        for (int i = 0; i < sBlade.length(); ++i) {
            if (!this.sampleSpace.contains(sBlade.substring(i, i+1))) {
                return false;
            }
        }

        return true;
    }


    private final int lenBlade;
    private final String sampleSpace;
    private final RandomGenerator RNG;

    private boolean primed;



    public ArkMinter() {
        this(DEFAULT_BLADE_LENGTH, DEFAULT_SAMPLE_SPACE, DEFAULT_RNG);
    }



    public String sampleSpace() {
        return this.sampleSpace;
    }

    /**
     * generates a new random <blade>
     */
    Ark.Blade mintBlade() {
        prime();
        val sb = new StringBuilder(this.lenBlade);
        for (int i = 0; i < this.lenBlade; ++i) {
            val random = this.RNG.nextInt(this.sampleSpace.length());
            val cp = this.sampleSpace.codePointAt(random);
            sb.appendCodePoint(cp);
        }
        return new Ark.Blade(sb.toString());
    }

    Ark.CheckDigit computeCheckDigit(final Ark.Naan naan, final Ark.Shoulder shoulder, final Ark.Blade blade) {
        val s = "/"+naan+"/"+shoulder+blade;
        val c = CharUtil.checksum(s, this.sampleSpace);
        return new Ark.CheckDigit(c);
    }

    private synchronized void prime() {
        if (!this.primed) {
            for (int i = 0; i < 2011; ++i) {
                this.RNG.nextInt();
            }
            this.primed = true;
        }
    }
}
