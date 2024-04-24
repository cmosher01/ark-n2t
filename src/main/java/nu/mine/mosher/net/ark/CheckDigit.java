package nu.mine.mosher.net.ark;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public final class CheckDigit {
    private final int codepoint;

    // TODO do we need to check against Alphabet in the constructor? or make this package-private?

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
