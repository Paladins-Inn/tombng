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

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

/**
 * RedirectRootURI -- Redirecting requests to "/" to the configured UI.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@Slf4j
@ApplicationScoped
public class RouteToUI {
    @ConfigProperty(name = "application.http.ui-base", defaultValue = "/ui/")
    String redirectURI;

    @RouteFilter(400)
    void redirectRootToUi(RoutingContext rc) {
        String uri = rc.request().uri();

        if (uri.equals("/")) {
            log.trace("Redirecting. uri='{}', destination='{}'", uri, redirectURI);
            rc.redirect(redirectURI);
            return;
        } else {
            log.trace("Don't redirect. uri='{}'", uri);
        }

        rc.next();
    }
}
