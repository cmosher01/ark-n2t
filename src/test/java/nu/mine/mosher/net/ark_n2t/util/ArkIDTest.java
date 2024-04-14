package nu.mine.mosher.net.ark_n2t.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArkIDTest {
    @Test
    void nominal() {
        final ArkID uut = new ArkID();
        final String actual = uut.mint();
        System.out.println("ArkID.mint() = "+actual);
        Assertions.assertEquals(ArkID.DEFAULT_ID_LENGTH, actual.length());
    }
}
