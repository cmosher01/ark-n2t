package nu.mine.mosher.net.ark.arkn2t;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark.NameMappingAuthority;

import java.util.Optional;

@WebServlet("/resolve/*")
@Slf4j
public final class ResolveServlet extends HttpServlet {
    private NameMappingAuthority nma;

    @Override
    @SneakyThrows
    public void init(@NonNull final ServletConfig config) {
        super.init(config);
        this.nma = (NameMappingAuthority)config.getServletContext().getAttribute("nma");
    }

    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val arkRequest = uri(request);
        log.info("URI: \"{}\"", arkRequest);

        val optArk = this.nma.parse(arkRequest);
        if (optArk.isEmpty()) {
            log.error("Invalid format ARK: {}", arkRequest);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val ark = optArk.get();
        if (!ark.hasValidCheckDigit()) {
            log.warn("Invalid checksum: expected={}, actual={}", ark.checkDigitExpected(), ark.checkDigitActual());
            if (!Optional.ofNullable(System.getenv("ARK_TRY_ON_BAD_CHECKSUM")).orElse("true").equalsIgnoreCase("true")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // TODO handle: "?", "??", "?info"

        val optUri = this.nma.resolve(ark);
        if (optUri.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val uri = optUri.get().toASCIIString();

        // TODO provide a mechanism to map origin portion of URL

        if (Optional.ofNullable(System.getenv("ARK_ACCEL")).orElse("false").equalsIgnoreCase("true")) {
            response.addHeader("X-Accel-Redirect", uri);
        } else {
            response.sendRedirect(uri);
        }
    }

    // TODO "Request strings too long for GET may be sent using HTTP's POST command"
    @Override
    @SneakyThrows
    public void doPost(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        super.doPost(request, response);
    }



    private static String uri(final HttpServletRequest request) {
        // requires proxy to supply this header, which allows us to get the
        // query string in the case where it is a single question mark
        var uri = Optional.ofNullable(request.getHeader("x-forwarded-uri"));
        if (uri.isPresent()) {
            return uri.get();
        }

        return
            Optional.ofNullable(request.getPathInfo()).orElse("")+
            Optional.ofNullable(request.getQueryString()).orElse("");
    }
}
