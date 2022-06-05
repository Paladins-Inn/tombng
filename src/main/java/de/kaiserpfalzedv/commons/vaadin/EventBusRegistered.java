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
import org.slf4j.LoggerFactory;

/**
 * EventBusRegistered -- Access to event bus.
 * <p>
 * The eventbus is shared between the HttpSession and the VaadinSession and has the session id of the HttpSession as
 * identifier.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
public interface EventBusRegistered {
    default EventBus getBus() {
        if (VaadinSession.getCurrent() == null) {
            LoggerFactory.getLogger(getClass()).warn("Can't register to session event bus. There is no session.");

            return new EventBus("session-less");
        }

        return loadEventBusFromVaadinSessionOrHttpSessionOrCreateNewOne(getHttpSessionId());
    }

    private String getHttpSessionId() {
        return VaadinSession.getCurrent().getSession().getId();
    }

    private EventBus loadEventBusFromVaadinSessionOrHttpSessionOrCreateNewOne(String sessionId) {
        EventBus bus = VaadinSession.getCurrent().getAttribute(EventBus.class);
        if (bus == null) {
            bus = getEventBusFromSessionOrCreateNewOne(sessionId);

            VaadinSession.getCurrent().lock();
            VaadinSession.getCurrent().setAttribute(EventBus.class, bus);
            VaadinSession.getCurrent().unlock();
        }
        return bus;
    }

    private EventBus getEventBusFromSessionOrCreateNewOne(String sessionId) {
        LoggerFactory.getLogger(getClass()).info("No event bus in vaadin session. Checking http session. session='{}'",
                sessionId);

        EventBus result = (EventBus) VaadinSession.getCurrent().getSession().getAttribute(EventBus.class.getCanonicalName());
        if (result == null) {
            result = createNewEventBusAndRegisterToHttpSession(sessionId);
        }

        return result;
    }

    private EventBus createNewEventBusAndRegisterToHttpSession(String sessionId) {
        LoggerFactory.getLogger(getClass()).info("No event bus in http session. Creating a new one. session='{}'",
                sessionId);

        EventBus result = new EventBus(sessionId);
        VaadinSession.getCurrent().getSession().setAttribute(EventBus.class.getCanonicalName(), result);

        return result;
    }

    default void registerToBus() {
        getBus().register(this);
        LoggerFactory.getLogger(getClass()).debug("Registered to bus. bus='{}', object={}",
                getBus().identifier(), this);
    }

    default void unregisterFromBus() {
        try {
            getBus().unregister(this);
            LoggerFactory.getLogger(getClass()).debug("Unregistered from bus. bus='{}', object={}",
                    getBus().identifier(), this);
        } catch (IllegalArgumentException e) {
            LoggerFactory.getLogger(getClass())
                    .warn("This object has not been registered to the session event bus. bus='{}', object={}",
                            getBus().identifier(), this
                    );
        }
    }
}
