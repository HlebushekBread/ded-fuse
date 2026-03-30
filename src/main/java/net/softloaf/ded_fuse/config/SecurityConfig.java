package net.softloaf.ded_fuse.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.security.JwtRequestFilter;
import net.softloaf.ded_fuse.security.JwtUtils;
import net.softloaf.ded_fuse.security.UserDetailsImpl;
import net.softloaf.ded_fuse.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ott.OneTimeTokenAuthenticationConverter;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/contacts/**").authenticated()
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/auth/generate").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                .oneTimeTokenLogin(configurer -> configurer
                        .tokenGeneratingUrl("/api/v1/auth/generate")
                        .tokenGenerationSuccessHandler(getOneTimeTokenGenerationSuccessHandler())
                        .loginProcessingUrl("/api/v1/auth/login")
                        .authenticationConverter(request -> {
                            try {
                                Map<String, String> body = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                                String username = body.get("username");
                                String code = body.get("code");
                                if(username == null || username.contains(":") || code == null || code.contains(":")) {
                                    throw new IOException();
                                }
                                return new OneTimeTokenAuthenticationToken(username + ":" + code);
                            } catch (IOException e) {
                                return null;
                            }
                        })
                        .authenticationSuccessHandler(getAuthenticationSuccessHandler())
                )
                .cors(customizer -> customizer.configurationSource(getCorsConfigurationSource()))
                .csrf(customizer -> customizer.disable())
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource getCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        String origins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (origins != null && !origins.isEmpty()) {
            configuration.setAllowedOrigins(Arrays.asList(origins.split(",")));
        } else {
            configuration.setAllowedOrigins(List.of());
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public OneTimeTokenGenerationSuccessHandler getOneTimeTokenGenerationSuccessHandler() {
        return (request, response, oneTimeToken) -> {
            String tokenValue = oneTimeToken.getTokenValue();
            String username = oneTimeToken.getUsername();

            System.out.println("ОТПРАВКА КОДА: " + tokenValue + " для пользователя " + username);

            response.setStatus(204);
        };
    }

    @Bean
    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"token\": \"" + token + "\"}");
        };
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
