package nu.mine.mosher.net.ark_n2t;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@WebServlet("/*")
@Slf4j
public final class ArkN2tServlet extends HttpServlet {
    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        try (val out = response.getWriter()) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("  <head>");
            out.println("    <title>ArkN2tServlet</title>");
            out.println("  </head>");
            out.println("  <body>");
            out.println("    <p>ArkN2tServlet OK</p>");
            out.println("  </body>");
            out.println("</html>");
        }
    }
}
