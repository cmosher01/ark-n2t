package nu.mine.mosher.net.ark_n2t.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamespaceSubdivision {
    private final NameAssigningAuthority authority;
    private final Ark.Shoulder shoulder;

    public NamespaceSubdivision(final NameAssigningAuthority authority) {
        this(authority, Ark.Shoulder.GLOBAL);
    }

    @Override
    public String toString() {
        throw new IllegalStateException();
    }

    public NameAssigningAuthority authority() {
        return this.authority;
    }

    public Ark.Shoulder shoulder() {
        return this.shoulder;
    }

    public boolean isIdentifiedBy(final Ark.Shoulder shoulder) {
        return !this.shoulder.exists() || this.shoulder.equals(shoulder);
    }

    public Ark.Blade removeShoulderFrom(final String baseName) {
        if (!this.shoulder.exists()) {
            // we have a "global" ark (we don't use any shoulder)
            return new Ark.Blade(baseName);
        }

        return this.shoulder.removeFrom(baseName);
    }
}
