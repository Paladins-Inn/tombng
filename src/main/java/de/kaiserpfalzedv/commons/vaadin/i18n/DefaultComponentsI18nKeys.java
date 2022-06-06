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

package de.kaiserpfalzedv.commons.vaadin.i18n;

/**
 * DefaultComponents --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-06
 */
public interface DefaultComponentsI18nKeys {
    static final String VIEWS_PERSON_INFO = "kp-commons.vaadin.views.person-info";

    static final String ACTIONS_LOGIN = "kp-commons.vaadin.actions.login";
    static final String ACTIONS_LOGOUT = "kp-commons.vaadin.actions.logout";
    static final String ACTIONS_SETTINGS = "kp-commons.vaadin.actions.settings";
    static final String ACTIONS_HELP = "kp-commons.vaadin.actions.help";

    static final String ERROR_NO_ACCESS = "kp-commons.vaadin.errors.access-denied";


    static final String PROFILE_ID = "kp-commons.vaadin.profile.id";
    static final String PROFILE_NAME = "kp-commons.vaadin.profile.name";
    static final String PROFILE_EMAIL = "kp-commons.vaadin.profile.email";
    static final String PROFILE_TENANT = "kp-commons.vaadin.profile.tenant";
    static final String PROFILE_IMAGE = "kp-commons.vaadin.profile.image";
    static final String PROFILE_LOGIN_FIRST = "kp-commons.vaadin.profile.first-login";
    static final String PROFILE_LOGIN_LAST = "kp-commons.vaadin.profile.last-login";
    static final String PROFILE_ERROR_ID_NOT_FOUND = "kp-commons.vaadin.profile.error.id-not-found";

    static final String PRINCIPAL_NAME = "kp-commons.vaadin.principal.name";
    static final String PRINCIPAL_GROUPS = "kp-commons.vaadin.principal.groups";
    static final String PRINCIPAL_CLAIMS = "kp-commons.vaadin.principal.claims";
    static final String PRINCIPAL_TOKEN_EXPIRE = "kp-commons.vaadin.principal.token-expire";
    static final String PRINCIPAL_TOKEN_CREATE = "kp-commons.vaadin.principal.token-create";
    static final String PRINCIPAL_TOKEN_UPDATE = "kp-commons.vaadin.principal.token-update";
}
