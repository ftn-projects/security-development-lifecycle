package ftn.security.minikms.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        responseWrapper.setHeader("X-Request-ID", requestId);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;

            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.info("[{}] REQUEST {} {} | Body={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBody);

            String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.info("[{}] RESPONSE {} {} | Status={} | Duration={}ms | Body={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    responseWrapper.getStatus(),
                    duration,
                    responseBody);

            responseWrapper.copyBodyToResponse();
        }
    }
}
