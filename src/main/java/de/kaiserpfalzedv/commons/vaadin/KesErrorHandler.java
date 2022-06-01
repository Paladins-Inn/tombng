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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import io.quarkus.arc.Unremovable;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * ErrorHandler -- For internal errors to log and display to user.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@Unremovable
@VaadinServiceScoped
@VaadinServiceEnabled
@Slf4j
public class KesErrorHandler implements com.vaadin.flow.server.ErrorHandler {

    @Override
    public void error(ErrorEvent event) {
        log.error(event.getThrowable().getMessage(), event.getThrowable());

        UI ui = KesUI.getCurrent();
        Optional.ofNullable(ui).ifPresent(p -> p.access(() -> {
            Notification.show("Error: " + event.getThrowable().getMessage());
        }));
    }
}
