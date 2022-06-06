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

package de.kaiserpfalzedv.commons.vaadin.profile;

import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

/**
 * PersonCreator --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-03
 */

/**
 * Creates a new person. Needs to be another class than the loader to enable transaction handling.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-03
 */
@ApplicationScoped
@Slf4j
public class PersonCreator {
    @Transactional
    public Person createPerson(final DefaultJWTCallerPrincipal principal, final String roleClaimPath) {
        log.trace("person not found - creating new person. issuer='{}', subject='{}'", principal.getIssuer(), principal.getSubject());

        Person result = Person.builder()
                .issuer(principal.getIssuer())
                .subject(principal.getSubject())
                .name(principal.getName())
                .email(principal.getClaim("email"))
                .principal(principal)
                .roleClaim(roleClaimPath)
                .build();

        result.persistAndFlush();

        log.debug("Created new person. person={}", result);
        return result;
    }
}
