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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * PersonHandler -- Implements the {@link ProfileHandler} for the concrete {@link Person} class.
 * <p>
 * Creates a new person. Needs to be another class than the loader to enable transaction handling.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-03
 */
@Dependent
@Slf4j
public class PersonHandler implements ProfileHandler {

    @ConfigProperty(name = "quarkus.oidc.roles.role-claim-path", defaultValue = "realm/")
    String roleClaimPath;

    @Override
    @Transactional
    synchronized public Optional<Profile> load(final DefaultJWTCallerPrincipal principal) {
        log.trace("trying to load profile from db. issuer='{}', subjectO'{}'",
                principal.getIssuer(), principal.getSubject()
        );

        Person result = Person.findByIssuerAndSubject(principal.getIssuer(), principal.getSubject());

        if (result != null) {
            result.principal = principal;
            result.roleClaim = roleClaimPath;

            log.debug(
                    "profile loaded from db. profile='{}', issuer='{}', subject='{}', first-login='{}', last-login='{}'",
                    result.id,
                    result.issuer,
                    result.subject,
                    result.getCreated(),
                    result.getLastLogin()
            );
        } else {
            log.info("profile not found in db. issuer='{}', subject='{}'",
                    principal.getIssuer(), principal.getSubject());
        }

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    synchronized public Person create(final DefaultJWTCallerPrincipal principal) {
        log.trace("creating new profile. issuer='{}', subject='{}'", principal.getIssuer(), principal.getSubject());

        Person result = Person.builder()
                .issuer(principal.getIssuer())
                .subject(principal.getSubject())
                .name(principal.getName())
                .email(principal.getClaim("email"))
                .principal(principal)
                .roleClaim(roleClaimPath)
                .build();

        result.persist();
        result = (Person) load(principal).get();

        log.info("created new profile. profile='{}'", result.id);
        return result;
    }

    @Override
    @Transactional
    synchronized public Optional<Profile> createOrLoadForLogin(final DefaultJWTCallerPrincipal principal) {
        log.debug("create or load profile. issuer='{}', subject='{}'",
                principal.getIssuer(),
                principal.getSubject()
        );

        Optional<Profile> result = load(principal);

        if (result.isPresent()) {
            ((Person) result.get()).lastLogin = OffsetDateTime.now(ZoneOffset.UTC);
        } else {
            result = Optional.ofNullable(create(principal));
        }

        return result;
    }
}