package nu.mine.mosher.net.ark_n2t.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NameAssigningAuthority {
    private final Ark.Naan number;
    private final ArkMinter minter;

    public NameAssigningAuthority(final Ark.Naan number) {
        this(number, new ArkMinter());
    }

    @Override
    public String toString() {
        throw new IllegalStateException();
    }

    public Ark.Naan number() {
        return this.number;
    }

    public ArkMinter minter() {
        return this.minter;
    }

    public boolean isIdentifiedBy(final Ark.Naan naan) {
        return naan.equals(this.number);
    }
}
