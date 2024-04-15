package nu.mine.mosher.net.ark_n2t.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamespaceSubdivision {
    private final NameAssigningAuthority authority;
    private final Ark.Shoulder shoulder;

    public NamespaceSubdivision(final NameAssigningAuthority authority) {
        this(authority, Ark.Shoulder.GLOBAL);
    }
}
