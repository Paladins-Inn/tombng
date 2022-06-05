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

import org.slf4j.LoggerFactory;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

/**
 * PermissionHolding --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-04
 */
public interface PermissionHolding {
    default boolean getDenyAll() {
        LoggerFactory.getLogger(getClass()).debug("class='{}'", getClass().getCanonicalName());

        return getClass().getAnnotatedSuperclass().getAnnotation(DenyAll.class) != null;
    }

    default boolean getPermitAll() {
        LoggerFactory.getLogger(getClass()).debug("class='{}'", getClass().getCanonicalName());

        return getClass().getAnnotatedSuperclass().getAnnotation(PermitAll.class) != null
                && !getDenyAll()
                && getRolesAllowed().length == 0;
    }

    default String[] getRolesAllowed() {
        LoggerFactory.getLogger(getClass()).debug("class='{}'", getClass().getCanonicalName());

        return getClass().getAnnotatedSuperclass().getAnnotation(RolesAllowed.class) != null ?
                getClass().getAnnotatedSuperclass().getAnnotation(RolesAllowed.class).value()
                : new String[0];
    }
}
