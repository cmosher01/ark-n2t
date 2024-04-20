package nu.mine.mosher.net.ark_n2t.util;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class UriTest {
    @Test
    void ascii() throws URISyntaxException {
        val s = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b\uD800\uDF00ar?a=1%3D3";
        val uut = new URI(s);
        assertNotEquals(s, uut.toASCIIString());
        assertEquals("http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b%F0%90%8C%80ar?a=1%3D3", uut.toASCIIString());
    }

    @Test
    void string() throws URISyntaxException {
        val s = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b\uD800\uDF00ar?a=1%3D3";
        val uut = new URI(s);
        assertEquals(s, uut.toString());
    }

    @Test
    void eq() throws URISyntaxException {
        val s1 = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b\uD800\uDF00ar?a=1%3D3";
        val u1 = new URI(s1);
        val s2 = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b%F0%90%8C%80ar?a=1%3D3";
        val u2 = new URI(s2);
        assertNotEquals(u1, u2);
        assertTrue(0 < u1.compareTo(u2));
        assertNotEquals(u1.toString(), u1.toASCIIString());
    }

    @Test
    void asc() throws URISyntaxException {
        val s1 = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b\uD800\uDF00ar?a=1%3D3";
        val u1 = new URI(new URI(s1).toASCIIString());
        val s2 = "http://example.com/a%20b/x%2Fy/fo%F0%90%8C%80o.b%F0%90%8C%80ar?a=1%3D3";
        val u2 = new URI(s2);
        assertEquals(u1, u2);
        assertEquals(0, u1.compareTo(u2));
        assertEquals(u1.toString(), u1.toASCIIString());
    }
}
