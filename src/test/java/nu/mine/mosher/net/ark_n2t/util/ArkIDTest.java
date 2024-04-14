package nu.mine.mosher.net.ark_n2t.util;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArkIDTest {
    @Test
    void nominal() {
        final ArkMinter uut = new ArkMinter();
        final ArkID.Blade actual = uut.mintBlade();
        System.out.println("ArkMinter.mintBlade() = "+actual);
        assertEquals(ArkMinter.DEFAULT_BLADE_LENGTH, actual.toString().length());
    }

    @Test
    void checkDigit() {
        final ArkMinter uut = new ArkMinter(7, "0123456789bcdfghjkmnpqrstvwxz", ArkMinter.DEFAULT_RNG);
        val naan = new ArkID.Naan("13030");
        val shoulder = new ArkID.Shoulder("");
        val blade = new ArkID.Blade("xf93gt2");

        val actual = uut.computeCheckDigit(naan, shoulder, blade);

        assertEquals(new ArkID.CheckDigit('q'), actual);
    }

    @Test
    void checkDigitWithShoulder() {
        final ArkMinter uut = new ArkMinter(4, "0123456789bcdfghjkmnpqrstvwxz", ArkMinter.DEFAULT_RNG);
        val naan = new ArkID.Naan("13030");
        val shoulder = new ArkID.Shoulder("xf9");
        val blade = new ArkID.Blade("3gt2");

        val actual = uut.computeCheckDigit(naan, shoulder, blade);

        assertEquals(new ArkID.CheckDigit('q'), actual);
    }
}
