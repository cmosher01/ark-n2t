package nu.mine.mosher.net.ark_n2t.util;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArkTest {
    @Test
    void nominal() {
        final ArkMinter uut = new ArkMinter();
        final Ark.Blade actual = uut.mintBlade();
        System.out.println("ArkMinter.mintBlade() = "+actual);
        assertEquals(ArkMinter.DEFAULT_BLADE_LENGTH, actual.toString().length());
    }

    @Test
    void checkDigit() {
        final ArkMinter uut = new ArkMinter(7, "0123456789bcdfghjkmnpqrstvwxz", ArkMinter.DEFAULT_RNG);
        val naan = new Ark.Naan("13030");
        val shoulder = new Ark.Shoulder("");
        val blade = new Ark.Blade("xf93gt2");

        val actual = uut.computeCheckDigit(naan, shoulder, blade);

        assertEquals(new Ark.CheckDigit('q'), actual);
    }

    @Test
    void checkDigitWithShoulder() {
        final ArkMinter uut = new ArkMinter(4, "0123456789bcdfghjkmnpqrstvwxz", ArkMinter.DEFAULT_RNG);
        val naan = new Ark.Naan("13030");
        val shoulder = new Ark.Shoulder("xf9");
        val blade = new Ark.Blade("3gt2");

        val actual = uut.computeCheckDigit(naan, shoulder, blade);

        assertEquals(new Ark.CheckDigit('q'), actual);
    }

    @Test
    void removeAllWhitespaceAndHyphens() {
        val input =
            "/ark:/1234"+
            Character.toString(0x000A)+
            "5/z9 tst-t"+
            Character.toString(0x2002)+
            "st"+
            Character.toString(0x2014)+
            "k/foo/bar.v2.jpg?info";
        val expected = "/ark:/12345/z9tsttstk/foo/bar.v2.jpg?info";
        val actual = CharUtil.removeAllWhitespaceAndHyphens(input);
        assertEquals(expected, actual);
    }
}
