package nu.mine.mosher.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;

@WebServlet("/health")
public final class Health extends HttpServlet {
    @Override
    @SneakyThrows
    public void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
    }
}
