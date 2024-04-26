package nu.mine.mosher.net.ark;

public interface ChecksumAlgorithm {
    CheckDigit checksum(NameAssigningAuthority.Number naan, ShoulderBlade sb, Alphabet alphabet);
}
