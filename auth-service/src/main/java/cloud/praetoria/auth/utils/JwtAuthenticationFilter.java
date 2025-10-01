package cloud.praetoria.auth.utils;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cloud.praetoria.auth.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	  log.debug("No Authorization header or not Bearer -> skipping auth");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = jwtUtil.extractTokenFromHeader(authHeader);

        try {
            if (!jwtUtil.validateToken(token) || !jwtUtil.isAccessToken(token)) {
                log.debug("Invalid or not access token -> skipping auth");
                filterChain.doFilter(request, response);
                return;
            }

            String ypareoId = jwtUtil.getYpareoIdFromToken(token);

            if (ypareoId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userService.loadUserByUsername(ypareoId);
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
        	log.warn("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}