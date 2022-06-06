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
import de.kaiserpfalzedv.commons.vaadin.profile.Person;
import de.kaiserpfalzedv.commons.vaadin.profile.UserDetails;
import de.kaiserpfalzedv.commons.vaadin.security.servlet.QuarkusVaadinSecurityRequestHandler;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * KesServiceInitListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
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
    }


    @Override
    @Transactional
    public void sessionDestroy(SessionDestroyEvent event) {
        UserDetails userDetails = (UserDetails) event.getSession().getSession().getAttribute(Principal.class.getCanonicalName());

        if (userDetails != null) {
            log.debug("Ending session for user. session='{}', user='{}'", event.getSession().getSession().getId(),
                    userDetails.getId()
            );

            if (userDetails instanceof Person) {
                Person person = Person.findById(userDetails.getId());
                person.lastLogin = OffsetDateTime.now(ZoneOffset.UTC);
                person.persistAndFlush();
            } else {
                log.warn("User is not of correct type. session='{}', user='{}', type='{}'",
                        event.getSession().getSession().getId(),
                        userDetails.getId(),
                        userDetails.getClass().getCanonicalName()
                );
            }
        } else {
            log.trace("No user logged in in the session to destroy.");
        }

        log.debug("Session end. session='{}'", event.getSession().getSession().isNew());
    }
}
