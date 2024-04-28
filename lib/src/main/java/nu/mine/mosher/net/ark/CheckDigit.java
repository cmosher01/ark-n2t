package nu.mine.mosher.net.ark;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public final class CheckDigit {
    public static final CheckDigit NULL = new CheckDigit(0x0010FFFF);

    private final int codepoint;

    @Override
    public String toString() {
        return new String(new int[] {this.codepoint}, 0, 1);
    }

    @Override
    public boolean equals(final Object object) {
        return
            object instanceof final CheckDigit that &&
            this.codepoint == that.codepoint;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.codepoint);
    }
}
