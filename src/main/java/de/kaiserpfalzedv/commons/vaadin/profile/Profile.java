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

import de.kaiserpfalzedv.commons.core.resources.HasId;
import de.kaiserpfalzedv.commons.core.resources.HasName;

import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * UserDetails -- User information for the UI.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
public interface Profile extends Serializable, Principal, HasName, HasId {
    Principal getPrincipal();

    String getEmail();

    String getTenant();

    String getAvatar();

    boolean isUserInRole(final String role);

    Set<String> getRoles();

    OffsetDateTime getLastLogin();


    Instant getCreated();

    Instant getUpdated();
}
