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

package de.kaiserpfalzedv.commons.vaadin.security;

import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.RouteRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * QuarkusSecurityRouter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-04
 */
@Slf4j
public class QuarkusVaadinSecurityRouter extends Router {

    /**
     * Constructs a new router with the given route registry and a
     * {@link com.vaadin.flow.router.internal.DefaultRouteResolver}.
     *
     * @param registry the route registry to use, not <code>null</code>
     */
    public QuarkusVaadinSecurityRouter(@NotNull final RouteRegistry registry) {
        super(registry);
    }


}
