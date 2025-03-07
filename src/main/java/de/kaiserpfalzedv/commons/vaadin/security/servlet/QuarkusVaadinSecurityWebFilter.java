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

import de.kaiserpfalzedv.commons.vaadin.profile.Person;
import de.kaiserpfalzedv.commons.vaadin.profile.Profile;
import de.kaiserpfalzedv.commons.vaadin.profile.ProfileHandler;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * QuarkusVaadinSecurityWebFilter -- Filters all UI requests for including the {@link Profile} in the
 * {@link javax.servlet.http.HttpSession} of the request.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@ApplicationScoped
@RequiredArgsConstructor
@WebFilter(urlPatterns = {"/ui/*"})
@Slf4j
public class QuarkusVaadinSecurityWebFilter implements Filter {
    public static final String PROFILE = Principal.class.getCanonicalName();

    private final ProfileHandler profileHandler;

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
            handlePrincipalFromRequest(request, principal);
        } else {
            logUserNotLoggedIn(request, principal);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }


    private void handlePrincipalFromRequest(HttpServletRequest request, Principal principal) {
        DefaultJWTCallerPrincipal jwt = (DefaultJWTCallerPrincipal) principal;

        log.debug("User is logged in. session='{}', principal={}", request.getSession().getId(), jwt);

        Profile profile = (Profile) request.getSession().getAttribute(PROFILE);
        if (profile != null && principal.equals(profile.getPrincipal())) {
            logAlreadyProvidedProfile(request, profile);
        } else {
            replaceProfileInSession(request, (DefaultJWTCallerPrincipal) principal, jwt);
        }
    }

    private void logUserNotLoggedIn(HttpServletRequest request, Principal principal) {
        log.debug("User is not logged in. session='{}', principal={}, type='{}'",
                request.getSession().getId(), principal, principal != null ? principal.getClass().getCanonicalName() : "./.");

        removePrincipalFromSession(request);
    }


    private void logAlreadyProvidedProfile(HttpServletRequest request, Profile profile) {
        log.trace("JWT principal already in session. profile='{}', session='{}', issuer='{}', subject='{}'",
                ((Profile) request.getSession().getAttribute(PROFILE)).getId(),
                request.getSession().getId(),
                ((DefaultJWTCallerPrincipal) profile.getPrincipal()).getIssuer(),
                ((DefaultJWTCallerPrincipal) profile.getPrincipal()).getSubject()
        );
    }

    private void replaceProfileInSession(HttpServletRequest request, DefaultJWTCallerPrincipal principal, DefaultJWTCallerPrincipal jwt) {
        log.trace("Changing principal in session. JWT principal changed. session='{}', issuer='{}', subject='{}'",
                request.getSession().getId(),
                principal.getIssuer(),
                principal.getSubject()
        );
        Optional<Profile> user = profileHandler.createOrLoadForLogin(jwt);
        user.ifPresentOrElse(
                u -> {
                    ((Person) u).lastLogin = OffsetDateTime.now(ZoneOffset.UTC);
                    addPrincipalToSession(request, u);
                },
                () -> {

                }
        );
    }

    private void addPrincipalToSession(HttpServletRequest request, final Profile user) {
        request.getSession().setAttribute(PROFILE, user);

        log.trace("Added user details to session. session='{}', user={}", request.getSession().getId(), user);
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
