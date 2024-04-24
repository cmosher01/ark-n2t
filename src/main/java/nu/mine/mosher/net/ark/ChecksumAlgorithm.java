package nu.mine.mosher.net.ark;

public interface ChecksumAlgorithm {
    CheckDigit checksum(Naan naan, ShoulderBlade sb, Alphabet alphabet);
}
