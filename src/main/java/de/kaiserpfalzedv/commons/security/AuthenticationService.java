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

import io.quarkus.oidc.SecurityEvent;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import java.io.Serializable;

/**
 * AuthenticationService -- Authentication service for usage with OIDC and a local pointer entry in JPA.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-27
 */
@ApplicationScoped
@Slf4j
public class AuthenticationService implements SecurityIdentityAugmentor, Serializable {

    private DefaultJWTCallerPrincipal principal;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (DefaultJWTCallerPrincipal.class.isAssignableFrom(identity.getPrincipal().getClass())) {
            principal = ((DefaultJWTCallerPrincipal) identity.getPrincipal());
            log.debug("Augmenting identity. subject='{}', name='{}', issuer='{}'",
                    principal.getSubject(), principal.getName(), principal.getIssuer());
        } else {
            principal = null;
        }

        RoutingContext ctx = identity.getAttribute(RoutingContext.class.getName());

        if (
                ctx != null
                && ctx.normalizedPath().endsWith("/github")
        ) {
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);
            UserInfo userInfo = identity.getAttribute("userinfo");
            builder.setPrincipal(() -> userInfo.getString("preferred_username"));

            identity = builder.build();
        }


        return Uni.createFrom().item(identity);
    }

    public void event(@Observes SecurityEvent event) {
        try {
            principal = (DefaultJWTCallerPrincipal) event.getSecurityIdentity().getPrincipal();

            log.debug("event={}, issuer='{}', principal='{}', tenant={}",
                    event.getEventType().name(),
                    principal.getIssuer(), principal.getName(),
                    event.getSecurityIdentity().getAttribute("tenant-id"));
        } catch (ClassCastException e) {
            principal = null;

            log.error("Unknown principal type. event={}, principal={}, type='{}'",
                    event,
                    event.getSecurityIdentity().getPrincipal(),
                    event.getSecurityIdentity().getClass().getCanonicalName());
        }
    }

    @Produces
    public DefaultJWTCallerPrincipal getPrincipal() {
        return principal;
    }
}
