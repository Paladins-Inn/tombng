/*
 * Copyright (c) 2022. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.vaadin.security.servlet;

import de.kaiserpfalzedv.commons.vaadin.profile.UserDetails;
import de.kaiserpfalzedv.commons.vaadin.profile.UserDetailsLoader;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * SecurityWebFilter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@ApplicationScoped
@RequiredArgsConstructor
@WebFilter(urlPatterns = {"/ui", "/ui/", "/ui/*"})
@Slf4j
public class SecurityWebFilter implements Filter {
    public static final String PRINCIPAL = Principal.class.getCanonicalName();

    private final UserDetailsLoader loader;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.trace("Creating security web filter.");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Principal principal = request.getUserPrincipal();
        if (principal instanceof DefaultJWTCallerPrincipal) {
            DefaultJWTCallerPrincipal jwt = (DefaultJWTCallerPrincipal) principal;

            log.debug("User is logged in. session='{}', principal={}", request.getSession().getId(), principal);

            UserDetails user = loader.load(jwt);
            addPrincipalToSession(request, user);

            log.trace("Added user details to session. session='{}', user={}", request.getSession().getId(), user);
        } else {
            log.debug("User is not logged in. session='{}', principal={}, type='{}'",
                    request.getSession().getId(), principal, principal != null ? principal.getClass().getCanonicalName() : "./.");

            removePrincipalFromSession(request);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void addPrincipalToSession(HttpServletRequest request, UserDetails user) {
        request.getSession().setAttribute(PRINCIPAL, user);
    }

    private void removePrincipalFromSession(HttpServletRequest request) {
        addPrincipalToSession(request, null);
    }

    @Override
    public void destroy() {
        log.trace("Destroying security web filter.");

        Filter.super.destroy();
    }
}
