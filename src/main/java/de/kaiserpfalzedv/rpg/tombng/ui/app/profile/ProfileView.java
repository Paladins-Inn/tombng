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

package de.kaiserpfalzedv.rpg.tombng.ui.app.profile;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.kaiserpfalzedv.commons.vaadin.i18n.DefaultComponentsI18nKeys;
import de.kaiserpfalzedv.commons.vaadin.profile.Profile;
import de.kaiserpfalzedv.rpg.tombng.ui.UserLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * ProfileView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-05
 */
@PageTitle(DefaultComponentsI18nKeys.VIEWS_PERSON_INFO)
@Route(value = "profile", layout = UserLayout.class)
@RolesAllowed({"user"})
@Dependent
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Slf4j
public class ProfileView extends de.kaiserpfalzedv.commons.vaadin.profile.ProfileView {
    private final Profile data;
}
