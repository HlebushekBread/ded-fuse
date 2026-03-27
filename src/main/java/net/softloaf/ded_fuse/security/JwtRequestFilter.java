package net.softloaf.ded_fuse.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.model.Role;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.UserRepository;
import net.softloaf.ded_fuse.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    String username = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtils.getUsernameFromToken(jwt);
            } catch (ExpiredJwtException e) {
                logger.debug("Expired JWT");
            } catch (SecurityException e) {
                logger.debug("Security exception");
            } catch (Exception e) {
                logger.debug(e.getCause());
            }
        }
        if(username != null && jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long id = Long.parseLong(jwtUtils.getIdFromToken(jwt));
            List<String> authorities = jwtUtils.getAuthoritiesFromToken(jwt);

            User user = new User();
            user.setId(id);
            user.setUsername(username);

            Role role = new Role();

            String roleName = authorities.getFirst().replace("ROLE_", "");
            role.setName(roleName);

            user.setRole(role);

            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    jwtUtils.getAuthoritiesFromToken(jwt).stream().map(SimpleGrantedAuthority::new).toList()
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
