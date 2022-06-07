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

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * ProfileHandler -- The handling of the profile.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-06
 */
public interface ProfileHandler {
    /**
     * loads the profile from the database.
     *
     * @param principal The principal from OIDC with the issuer and subject specifying the account.
     * @return the profile loaded (or empty).
     */
    Optional<Profile> load(final DefaultJWTCallerPrincipal principal);

    /**
     * creates the profile in the database.
     *
     * @param principal The principal from OIDC with the issuer and subject specifying the account.
     * @return the profile created.
     */
    Profile create(final DefaultJWTCallerPrincipal principal);

    /**
     * Loads the profile from the database. If it does not exist, it will be created instead.
     * The last login date in the profile gets updated.
     *
     * @param principal The principal from OIDC with the issuer and subject specifying the profile to load.
     * @return the profile loaded or created (or empty).
     */
    @Transactional
    Optional<Profile> createOrLoadForLogin(DefaultJWTCallerPrincipal principal);
}
