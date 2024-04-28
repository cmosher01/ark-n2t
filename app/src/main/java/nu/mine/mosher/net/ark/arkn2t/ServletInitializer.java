package nu.mine.mosher.net.ark.arkn2t;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark.*;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Optional;

@WebListener
@Slf4j
public class ServletInitializer implements ServletContextListener {
    @Override
    @SneakyThrows
    public void contextInitialized(@NonNull final ServletContextEvent event) {
        log.info("-------- contextInitialized: begin --------");

        val alphabet = Alphabet.RECOMMENDED; // TODO read from env
        val check = new NoidChecksumAlgorithm(); // TODO read from env
        val lenBlade = 10; // TODO read from env
        val shoulder = getShoulderFromEnv(alphabet);
        val naan = getNaanFromEnv();

        final DataSource ds = InitialContext.doLookup("java:/comp/env/jdbc/db");

        val naa = new NameAssigningAuthority(ds, naan, shoulder, lenBlade, alphabet, check);
        val nma = new NameMappingAuthority(ds, naa);

        val ctx = event.getServletContext();
        ctx.setAttribute("naa", naa);
        ctx.setAttribute("nma", nma);

        log.info("-------- contextInitialized: end   --------");
    }



    private static Shoulder getShoulderFromEnv(final Alphabet alphabet) {
        final Shoulder shoulder;
        val envArkShoulder = Optional.ofNullable(System.getenv("ARK_SHOULDER"));
        if (envArkShoulder.isEmpty()) {
            shoulder = Shoulder.NULL;
            log.warn("Could not find ARK_SHOULDER environment variable; " +
                "generated ARKs will not have shoulders.");
        } else {
            shoulder = new Shoulder(envArkShoulder.get(), alphabet);
            log.info("Will use \"shoulder\" found in ARK_SHOULDER environment variable: \"{}\"", shoulder);
        }
        return shoulder;
    }

    private static NameAssigningAuthority.Number getNaanFromEnv() {
        final NameAssigningAuthority.Number naan;
        val envArkNaan = Optional.ofNullable(System.getenv("ARK_NAAN"));
        if (envArkNaan.isEmpty()) {
            naan = NameAssigningAuthority.Number.TEST;
            log.error(
                "Could not find ARK_NAAN (name assigning authority number) environment variable; " +
                "will use designated test NAAN: \"{}\".", naan);
        } else {
            naan = new NameAssigningAuthority.Number(envArkNaan.get());
            log.info("Will use NAAN found in ARK_NAAN environment variable: \"{}\"", naan);
        }
        return naan;
    }
}
