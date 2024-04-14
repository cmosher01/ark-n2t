package nu.mine.mosher.net.ark_n2t.util;

import lombok.RequiredArgsConstructor;

import java.util.*;

/**

https://arks.org/specs/

[/]ark:[/]<naan>/[<shoulder>]<blade><check>[parsing terminated by non-alphanumeric character, or end-of-string]

This implementation does not deal with any of the following:
    %-encoded octets
    qualifiers
    inflections
    variants

Lenient processing:
    case insensitive (downcase everything)
    remove all whitespace
    remove all hyphens and hyphen-like characters (e.g., U+2010 to U+2015)

*/
public class ArkID {
    @RequiredArgsConstructor
    public static class Naan {
        private final String s;
        @Override
        public String toString() {
            return this.s;
        }
    }

    @RequiredArgsConstructor
    public static class Shoulder {
        private final String s;

        @Override
        public String toString() {
            return this.s;
        }
    }

    @RequiredArgsConstructor
    public static class Blade {
        private final String s;

        @Override
        public String toString() {
            return this.s;
        }
    }

    @RequiredArgsConstructor
    public static class CheckDigit {
        private final int codepoint;

        @Override
        public String toString() {
            return new String(new int[]{this.codepoint}, 0, 1);
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof final CheckDigit that)) {
                return false;
            }
            return this.codepoint == that.codepoint;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.codepoint);
        }
    }
}
