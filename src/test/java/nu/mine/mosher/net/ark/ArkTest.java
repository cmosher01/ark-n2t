package nu.mine.mosher.net.ark;

import lombok.val;
import org.junit.jupiter.api.Test;

import static nu.mine.mosher.net.ark.Ark.normalize;
import static org.junit.jupiter.api.Assertions.*;

public class ArkTest {
    @Test
    void nominal() {
        val BN = Alphabet.BETA_NUMERIC;
        val uut = new Minter(new Shoulder("g8", BN), 10, BN, Minter.DEFAULT_RNG);
        val actual = uut.mint();
        System.out.println("Minter.mint() = "+actual);
        assertEquals(12, actual.toString().length());
    }

    @Test
    void checkDigit() {
        val BN = Alphabet.BETA_NUMERIC;
        val uut = new NoidChecksumAlgorithm();
        val naan = new NameAssigningAuthority.Number("13030");
        val shoulder_blade = new ShoulderBlade("xf93gt2");

        val actual = uut.checksum(naan, shoulder_blade, BN);

        assertEquals(new CheckDigit('q'), actual);
    }

    @Test
    void normalizeArkCharacters() {
        val input =
            "/ark:/1234"+
            Character.toString(0x000A)+
            "5/z9 tst-t"+
            Character.toString(0x2002)+
            "st"+
            Character.toString(0x2014)+
            "k-foo-bal";
        val expected = "/ark:/12345/z9tsttstkf00ba1";
        val actual = normalize(input);
        assertEquals(expected, actual);
    }

    @Test
    void normalizeArkCharacters2() {
        val input = "/ark:86874/g8tst-tst-tst96";
        val expected = "/ark:86874/g8tsttsttst96";
        val actual = normalize(input);
        assertEquals(expected, actual);
    }

    @Test
    void parse() {
        val uri = "/ark:86874/g8tst-tst-tst9t";
        val optark = Ark.parse(uri, Alphabet.BETA_NUMERIC, new NoidChecksumAlgorithm());
        assertTrue(optark.isPresent());
        assertTrue(optark.get().hasValidCheckDigit());
    }
}
