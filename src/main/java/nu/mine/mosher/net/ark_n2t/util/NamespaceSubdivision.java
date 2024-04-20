package nu.mine.mosher.net.ark_n2t.util;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

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

    public String removeShoulderFrom(final String sShoulderBladeChecksum) {
        if (!this.shoulder.exists()) {
            // we have a "global" ark (we don't use any shoulder)
            return sShoulderBladeChecksum;
        }

        return this.shoulder.removeFrom(sShoulderBladeChecksum);
    }

    public Ark mint() {
        return new Ark(this, authority().minter().mintBlade());
    }

    public Optional<Ark> parseArk(final String s) {
        // TODO

        return Optional.empty();
    }
}
