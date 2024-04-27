package nu.mine.mosher.net.ark.ark_n2t;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nu.mine.mosher.net.ark.NameAssigningAuthority;

import java.net.*;
import java.util.Optional;

@WebServlet("/bind")
@Slf4j
public final class BindServlet extends HttpServlet {
    private NameAssigningAuthority naa;

    @Override
    @SneakyThrows
    public void init(@NonNull final ServletConfig config) {
        super.init(config);
        this.naa = (NameAssigningAuthority)config.getServletContext().getAttribute("naa");
    }

    @Override
    @SneakyThrows
    public void doPost(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        val uri = uri(request);
        val ark = this.naa.bind(uri);
        val origin = Optional.ofNullable(request.getHeader("origin")).orElse("");
        response.sendRedirect(origin+"/"+ark);
    }

    private static URI uri(final HttpServletRequest request) throws URISyntaxException {
        return new URI(request.getParameter("url"));
    }
}
