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

import de.kaiserpfalzedv.commons.vaadin.security.Person;
import de.kaiserpfalzedv.commons.vaadin.security.PersonCreator;
import io.quarkus.cache.CacheResult;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * UserDetailsCache -- Loads the person from the JPA datasource.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-03
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Slf4j
public class UserDetailsLoader {
    @ConfigProperty(name = "quarkus.oidc.roles.role-claim-path", defaultValue = "realm_access/roles")
    String roleClaimPath;

    private final PersonCreator creator;

    @CacheResult(cacheName = "user-details")
    public UserDetails load(final DefaultJWTCallerPrincipal principal) {
        log.debug("Loading user. issuer='{}', subject='{}'", principal.getIssuer(), principal.getSubject());

        Person user = Person.findByIssuerAndSubject(principal.getIssuer(), principal.getSubject());

        if (user == null) {
            user = creator.createPerson(principal, roleClaimPath);
        }

        user.setPrincipal(principal);
        user.setRoleClaim(roleClaimPath);

        return user;
    }


}

