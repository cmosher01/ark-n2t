package nu.mine.mosher.net.ark.arkn2t;

import lombok.val;
import nu.mine.mosher.net.ark.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ArkN2tOptions {
    public Alphabet alphabet = Alphabet.RECOMMENDED;
    public ChecksumAlgorithm check = new NoidChecksumAlgorithm();
    public int lenBlade = 10;
    public Shoulder shoulder = Shoulder.NULL;
    public NameAssigningAuthority.Number naan = NameAssigningAuthority.Number.TEST;
    public List<String> urls = new ArrayList<>();
    public String database = "jdbc:sqlite:/var/lib/ark-n2t/ark-n2t.sqlite";



    public void naan(final Optional<String> s) {
        if (s.isPresent() && !s.get().isBlank()) {
            this.naan = new NameAssigningAuthority.Number(s.get());
        }
    }

    public void shoulder(final Optional<String> s) {
        if (s.isPresent() && !s.get().isBlank()) {
            this.shoulder = new Shoulder(s.get(), this.alphabet);
        }
    }

    public void length(final Optional<String> s) {
        if (s.isPresent() && !s.get().isBlank()) {
            this.lenBlade = Integer.parseInt(s.get(), 10);
        }
    }

    public void check(final Optional<String> s) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.check = (ChecksumAlgorithm)Class.forName(s.get()).getConstructor().newInstance();
    }

    public void alphabet(final Optional<String> s) {
        if (s.isPresent() && !s.get().isBlank()) {
            this.alphabet = new Alphabet(s.get());
        }
    }

    public void database(final Optional<String> s) {
        if (s.isPresent() && !s.get().isBlank()) {
            this.database = s.get();
        }
    }

    public void __(final Optional<String> s) throws IOException {
        if (s.isPresent() && !s.get().isBlank()) {
            if (s.get().equals("-")) {
                try (val in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)))) {
                    for (var url = in.readLine(); Objects.nonNull(url); url = in.readLine()) {
                        this.urls.add(url);
                    }
                }
            } else {
                this.urls.add(s.get());
            }
        }
    }
}
