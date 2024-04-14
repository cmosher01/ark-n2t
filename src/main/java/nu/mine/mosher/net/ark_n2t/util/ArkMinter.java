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



    private final int lenBlade;
    private final String sampleSpace;
    private final RandomGenerator RNG;

    private boolean primed;



    public ArkMinter() {
        this(DEFAULT_BLADE_LENGTH, DEFAULT_SAMPLE_SPACE, DEFAULT_RNG);
    }

    // generates a new <blade>


    ArkID.Blade mintBlade() {
        prime();
        val sb = new StringBuilder(this.lenBlade);
        for (int i = 0; i < this.lenBlade; ++i) {
            val random = this.RNG.nextInt(this.sampleSpace.length());
            val cp = this.sampleSpace.codePointAt(random);
            sb.appendCodePoint(cp);
        }
        return new ArkID.Blade(sb.toString());
    }

    ArkID.CheckDigit computeCheckDigit(final ArkID.Naan naan, final ArkID.Shoulder shoulder, final ArkID.Blade blade) {
        // implements the "Noid check digit algorithm"
        // https://metacpan.org/dist/Noid/view/noid

        val s = ""+naan+"/"+shoulder+blade;

        var prod = 0;
        for (int pos = 1; pos <= s.length(); ++pos) {
            val _char = s.codePointAt(pos-1);
            var ord = max(0,this.sampleSpace.indexOf(_char));
            prod += pos*ord;
        }
        prod %= this.sampleSpace.length();

        return new ArkID.CheckDigit(this.sampleSpace.codePointAt(prod));
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