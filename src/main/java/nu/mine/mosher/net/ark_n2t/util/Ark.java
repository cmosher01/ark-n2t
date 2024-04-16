package nu.mine.mosher.net.ark_n2t.util;

import lombok.*;

import java.util.*;

/**

 <a href="https://arks.org/specs/">https://arks.org/specs/</a>

 <code>
 [/]ark:[/]{naan}/[{shoulder}]{blade}{check-digit}[parsing terminated by non-alphanumeric character, or end-of-string]
 </code>

 {base-name} = [{shoulder}]{blade}
 portion checksummed = {naan}/[{shoulder}]{blade}

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
@RequiredArgsConstructor
public class Ark {
    @RequiredArgsConstructor
    public static class Naan {
        public static final Naan TERM = new Naan("99152");
        public static final Naan AGENT = new Naan("99166");
        public static final Naan EXAMPLE = new Naan("12345");
        public static final Naan TEST = new Naan("99999");

        private final String s;

        @Override
        public String toString() {
            return this.s;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Naan that)) {
                return false;
            }
            return Objects.equals(this.s, that.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.s);
        }
    }

    @RequiredArgsConstructor
    public static class Shoulder {
        public static final Shoulder GLOBAL = new Shoulder("");

        public static Shoulder of(final String baseName) {
            return new Shoulder(CharUtil.getShoulderOf(baseName));
        }

        public boolean exists() {
            return !this.s.isEmpty();
        }

        public String removeFrom(final String sShoulderBladeChecksum) {
            if (!sShoulderBladeChecksum.startsWith(this.s)) {
                throw new IllegalStateException("Wrong shoulder; expected: "+this.s);
            }
            return sShoulderBladeChecksum.substring(this.s.length());
        }

        private final String s;

        @Override
        public String toString() {
            return this.s;
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Blade that)) {
                return false;
            }
            return Objects.equals(this.s, that.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.s);
        }
    }

    @RequiredArgsConstructor
    public static class Blade {
        private final String s;

        @Override
        public String toString() {
            return this.s;
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Blade that)) {
                return false;
            }
            return Objects.equals(this.s, that.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.s);
        }
    }

// not needed, so far:
//    @RequiredArgsConstructor
//    public static class BaseName {
//        private final Shoulder shoulder;
//        private final Blade blade;
//
//        @Override
//        public String toString() {
//            return ""+this.shoulder+this.blade;
//        }
//
//        @Override
//        public boolean equals(final Object object) {
//            if (!(object instanceof BaseName that)) {
//                return false;
//            }
//            return
//                Objects.equals(this.shoulder, that.shoulder) &&
//                Objects.equals(this.blade, that.blade);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(this.shoulder, this.blade);
//        }
//    }

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

    private final NamespaceSubdivision ns;
    private final Blade blade;

    @Override
    public String toString() {
        val sb = new StringBuilder(256);
        // {naan}/[{shoulder}]{blade}{check-digit}
        sb.append(this.ns.authority().number());
        sb.append("/");
        sb.append(this.ns.shoulder());
        sb.append(this.blade);
        sb.append(computeCheckDigit());
        return sb.toString();
    }

    private CheckDigit computeCheckDigit() {
        return this.ns.authority().minter().computeCheckDigit(this.ns.authority().number(), this.ns.shoulder(), this.blade);
    }
}
