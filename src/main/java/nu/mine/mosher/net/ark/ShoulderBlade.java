package nu.mine.mosher.net.ark;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

// blade with optional shoulder
@RequiredArgsConstructor
public final class ShoulderBlade {
    @NonNull private final String s;

    @Override
    public String toString() {
        return this.s;
    }

    @Override
    public boolean equals(final Object object) {
        return
            object instanceof ShoulderBlade that &&
            Objects.equals(this.s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.s);
    }
}
