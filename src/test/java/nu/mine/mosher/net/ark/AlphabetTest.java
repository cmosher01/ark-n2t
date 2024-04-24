package nu.mine.mosher.net.ark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlphabetTest {
    @Test
    void covers() {
        assertTrue(Alphabet.BETA_NUMERIC.covers("bcd"));
        assertTrue(Alphabet.BETA_NUMERIC.covers(""));
        assertTrue(Alphabet.BETA_NUMERIC.covers("z"));
    }

    @Test
    void coversNeg() {
        assertFalse(Alphabet.BETA_NUMERIC.covers("bcde"));
        assertFalse(Alphabet.BETA_NUMERIC.covers("abcd"));
        assertFalse(Alphabet.BETA_NUMERIC.covers("hij"));
    }

    @Test
    void nonNull() {
        assertThrows(NullPointerException.class, () -> Alphabet.BETA_NUMERIC.covers(null));
    }

    @Test
    void empty() {
        assertThrows(Exception.class, () -> new Alphabet(null));
        assertThrows(Exception.class, () -> new Alphabet(""));
        assertThrows(Exception.class, () -> new Alphabet(" "));
    }
}
