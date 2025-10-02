package ftn.security.minikms.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Value("${logging.controller.enabled:true}")
    private boolean loggingEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!loggingEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader("X-Request-ID", requestId);

        String username = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : "anonymous";

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            log.info("{}", Map.of(
                    "message", "HTTP request completed",
                    "requestId", requestId,
                    "username", username,
                    "method", request.getMethod(),
                    "uri", request.getRequestURI(),
                    "status", response.getStatus(),
                    "durationMs", duration
            ));
        }
    }
}
