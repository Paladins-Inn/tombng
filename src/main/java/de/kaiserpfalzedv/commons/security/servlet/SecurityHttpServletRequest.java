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

package de.kaiserpfalzedv.commons.security.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.Optional;

/**
 * SecurityServletFilter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@Slf4j
public class SecurityHttpServletRequest extends HttpServletRequestWrapper {

    public SecurityHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Principal getUserPrincipal() {
        Principal result = (Principal) getSession().getAttribute("idToken");
        result = Optional.ofNullable(result).orElseGet(super::getUserPrincipal);

        log.debug("Principal requested. principal='{}', type='{}'", result, result.getClass().getCanonicalName());
        return result;
    }

    @Override
    public boolean isUserInRole(final String role) {
        return getUserPrincipal() instanceof UserDetails
                && ((UserDetails) getUserPrincipal()).isUserInRole(role)
                || super.isUserInRole(role);
    }
}
