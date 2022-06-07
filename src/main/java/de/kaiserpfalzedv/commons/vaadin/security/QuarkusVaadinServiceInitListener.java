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

import com.vaadin.flow.server.*;
import de.kaiserpfalzedv.commons.vaadin.profile.Profile;
import de.kaiserpfalzedv.commons.vaadin.security.servlet.QuarkusVaadinSecurityRequestHandler;
import de.kaiserpfalzedv.commons.vaadin.security.servlet.QuarkusVaadinSecurityWebFilter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * QuarkusVaadinServiceInitListener -- Handles the principal of the vaadin session.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@Dependent
@Slf4j
public class QuarkusVaadinServiceInitListener implements VaadinServiceInitListener, SessionInitListener, SessionDestroyListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        log.debug("Service initialized");

        event.getSource().addSessionInitListener(this);
        event.getSource().addSessionDestroyListener(this);

        event.addRequestHandler(new QuarkusVaadinSecurityRequestHandler());
    }


    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        log.debug("Session start. session='{}'", event.getSession().getSession().isNew());

        Profile profile = (Profile) event.getSession().getSession().getAttribute(QuarkusVaadinSecurityWebFilter.PROFILE);

        Optional.ofNullable(profile).ifPresent(u -> {
            event.getSession().setAttribute(Profile.class, profile);

            log.trace("copied user profile to vaadin session. session='{}', profile='{}'",
                    event.getSession().getSession().getId(),
                    profile.getId()
            );
        });
    }


    @Override
    @Transactional
    public void sessionDestroy(SessionDestroyEvent event) {
        log.debug("Session end. session='{}'", event.getSession().getSession().isNew());
    }
}
