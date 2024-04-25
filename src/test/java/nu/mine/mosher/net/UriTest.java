package nu.mine.mosher.net;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.net.*;

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

    @Test
    void space() {
        val s = "http://example.com/a b/p.jpg";
        assertThrows(URISyntaxException.class, () -> {
            new URI(s);
        });
    }

    @Test
    void nonbmp() throws URISyntaxException {
        val gclef = new String(new int[] {0x1D11E}, 0, 1);
        val s = "http://example.com/a"+gclef+"b/p.jpg";
        assertEquals("http://example.com/a%F0%9D%84%9Eb/p.jpg", new URI(s).toASCIIString());
    }

    @Test
    void max() throws URISyntaxException {
        val gclef = new String(new int[] {0x1D11E}, 0, 1);
        URI uri = new URI("http://m%3Ae:fido@www.example.com:8080/the/ignore/../pa" + gclef + "th/t%2Fo/./index.html?f%3Doo=b+r&a=1&a=x@2&b=&c&d=d#pl%25ace");
        dump(uri);
        uri = new URI(uri.toASCIIString());
        dump(uri);
        uri = uri.normalize();
        dump(uri);
        assertEquals("http://m%3Ae:fido@www.example.com:8080/the/pa%F0%9D%84%9Eth/t%2Fo/index.html?f%3Doo=b+r&a=1&a=x@2&b=&c&d=d#pl%25ace", uri.toASCIIString());
    }

    private static void dump(final URI uri) {
        d(uri.toString(), "string");
        d(uri.toASCIIString(), "ascii string");
        d(uri.getScheme(), "scheme");
        d(uri.getUserInfo(), "user-info");
        d(uri.getRawUserInfo(), "user-info (raw)");
        d(uri.getHost(), "host");
        d(Integer.toString(uri.getPort()), "port");
        d(uri.getAuthority(), "authority");
        d(uri.getRawAuthority(), "authority (raw)");
        d(uri.getPath(), "path");
        d(uri.getRawPath(), "path (raw)");
        d(uri.getQuery(), "query");
        d(uri.getRawQuery(), "query (raw)");
        d(uri.getSchemeSpecificPart(), "scheme-specific-part");
        d(uri.getRawSchemeSpecificPart(), "scheme-specific-part (raw)");
        d(uri.getFragment(), "fragment");
        d(uri.getRawFragment(), "fragment (raw)");
    }

    private static void d(final String value, final String label) {
        System.out.printf("%26s: %s\n", label, value);
    }
}
