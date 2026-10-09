
package com.calletuning.inventario.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

        @Bean
        public OncePerRequestFilter csrfCookieFilter() {
                return new OncePerRequestFilter() {

                        @Override
                        protected void doFilterInternal(
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        jakarta.servlet.FilterChain filterChain)
                                        throws jakarta.servlet.ServletException, IOException {

                                CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

                                if (csrfToken != null) {
                                        csrfToken.getToken();
                                }

                                filterChain.doFilter(request, response);
                        }
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

                CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

                http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfRepository)
                                                .csrfTokenRequestHandler(csrfHandler))
                                .authorizeHttpRequests(auth -> auth

                                                // Archivos públicos de la aplicación
                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/login.html",
                                                                "/tienda.html",
                                                                "/confirmacion-compra.html",
                                                                "/favicon.ico",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**")
                                                .permitAll()

                                                // Gestión de usuarios y roles: solo administrador
                                                .requestMatchers(
                                                                "/api/usuarios/**",
                                                                "/api/roles/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Catálogo: administrador, vendedor y cliente
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/productos/**",
                                                                "/api/categorias/**",
                                                                "/api/marcas/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "VENDEDOR",
                                                                "CLIENTE")

                                                // Registrar compras: solo cliente autenticado
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/ventas/compra")
                                                .hasRole("CLIENTE")

                                                // Resto de la API: administrador y vendedor
                                                .requestMatchers("/api/**")
                                                .hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                                                // Las demás rutas requieren autenticación
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login.html")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .successHandler((request, response, authentication) -> {

                                                        boolean esCliente = authentication.getAuthorities()
                                                                        .stream()
                                                                        .anyMatch(authority -> authority.getAuthority()
                                                                                        .equals("ROLE_CLIENTE"));

                                                        if (esCliente) {
                                                                response.sendRedirect(
                                                                                request.getContextPath()
                                                                                                + "/tienda.html");
                                                        } else {
                                                                response.sendRedirect(
                                                                                request.getContextPath() + "/");
                                                        }
                                                })
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login.html?logout")
                                                .permitAll());
                return http.build();
        }
}
