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

package de.kaiserpfalzedv.commons.security;

import com.vaadin.flow.server.VaadinSession;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.Principal;

/**
 * VaadinAuthenticationService --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@RequestScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Slf4j
public class VaadinAuthenticationService implements AutoCloseable {
    final DefaultJWTCallerPrincipal principal;

    final VaadinSession session;

    @ConfigProperty(name = "quarkus.oidc.roles.role-claim-path", defaultValue = "realm_access/roles")
    String roleClaimPath;

    @PostConstruct
    public void init() {
        log.trace("Loaded identities. session={}, principal={}", session, principal);

        if (session != null) {
            log.trace("Registering principal. principal={}", principal);

            session.lock();
            if (session.hasLock()) {
                Person user = Person.findByIssuerAndSubject(principal.getIssuer(), principal.getSubject()).await().indefinitely();

                if (user != null) {
                    user.setPrincipal(principal);
                    user.setRoleClaim(roleClaimPath);
                } else {
                    user = Person.builder()
                            .issuer(principal.getIssuer())
                            .subject(principal.getSubject())
                            .name(principal.getName())
                            .principal(principal)
                            .roleClaim(roleClaimPath)
                            .build();
                }

                user.persistAndFlush();

                session.setAttribute(Principal.class, user);
            }
            session.unlock();

            log.debug("User logged in. user={}", session.getAttribute(Principal.class));
        }
    }

    @PreDestroy
    public void close() {
        if (session != null) {
            log.trace("Unregistering principal. principal={}", principal);
            session.lock();
            if (session.hasLock()) {
                session.setAttribute(Principal.class, null);
                session.unlock();
            }
        }
    }
}
