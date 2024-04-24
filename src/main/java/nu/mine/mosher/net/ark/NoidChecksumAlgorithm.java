package nu.mine.mosher.net.ark;

import lombok.*;

import static java.lang.Math.max;

// implements the "Noid check digit algorithm"
// https://metacpan.org/dist/Noid/view/noid#NOID-CHECK-DIGIT-ALGORITHM
public final class NoidChecksumAlgorithm implements ChecksumAlgorithm {
    public CheckDigit checksum(@NonNull final Naan naan, @NonNull final ShoulderBlade shoulderBlade, @NonNull final Alphabet alphabet) {
        val zone = "/"+naan+"/"+shoulderBlade;
        var prod = 0;
        for (int pos = 0; pos < zone.length(); ++pos) {
            val chr = zone.codePointAt(pos);
            val ord = max(0, alphabet.indexOf(chr));
            prod += pos*ord;
        }
        prod %= alphabet.length();
        return new CheckDigit(alphabet.at(prod));
    }
}
