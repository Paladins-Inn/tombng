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

package de.kaiserpfalzedv.commons.vaadin;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * SessionEventBusRegistered -- Access to event bus.
 * <p>
 * The eventbus is shared between the HttpSession and the VaadinSession and has the session id of the HttpSession as
 * identifier.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
public interface SessionEventBusRegistered {
    default EventBus getBus() {
        if (getVaadinSession() == null) {
            getLog().warn("Can't register to session event bus. There is no session.");

            return new EventBus("session-less");
        }

        synchronized (getVaadinSession()) {
            return loadEventBusFromVaadinSessionOrHttpSessionOrCreateNewOne(getVaadinSession());
        }
    }

    private VaadinSession getVaadinSession() {
        return VaadinSession.getCurrent();
    }

    private EventBus loadEventBusFromVaadinSessionOrHttpSessionOrCreateNewOne(final VaadinSession session) {
        EventBus bus = VaadinSession.getCurrent().getAttribute(EventBus.class);

        return Optional.ofNullable(bus).orElse(getEventBusFromSessionOrCreateNewOne(session));
    }

    private EventBus getEventBusFromSessionOrCreateNewOne(final VaadinSession session) {
        getLog().info("No event bus in vaadin session. Checking http session. session='{}'",
                session.getSession().getId());

        EventBus result = (EventBus) VaadinSession.getCurrent().getSession().getAttribute(EventBus.class.getCanonicalName());

        return Optional.ofNullable(result).orElse(createNewEventBusAndRegisterToHttpSession(session));
    }

    private EventBus createNewEventBusAndRegisterToHttpSession(final VaadinSession session) {
        getLog().info("No event bus in http session. Creating a new one. session='{}'",
                session.getSession().getId());

        EventBus result = new EventBus(session.getSession().getId());
        registerEventBusToVaadinAndHTTPSession(session, result);

        return result;
    }

    private void registerEventBusToVaadinAndHTTPSession(final VaadinSession session, final EventBus bus) {
        session.access(() -> session.setAttribute(EventBus.class, bus));
        session.getSession().setAttribute(EventBus.class.getCanonicalName(), bus);
    }

    default void registerToBus() {
        getBus().register(this);
        getLog().debug("Registered to bus. bus='{}', object={}",
                getBus().identifier(), this);
    }

    default void unregisterFromBus() {
        try {
            getBus().unregister(this);
            getLog().debug("Unregistered from bus. bus='{}', object={}",
                    getBus().identifier(), this);
        } catch (IllegalArgumentException e) {
            getLog().warn("This object has not been registered to the session event bus. bus='{}', object={}",
                    getBus().identifier(), this
            );
        }
    }


    private Logger getLog() {
        return LoggerFactory.getLogger(getClass());
    }
}
