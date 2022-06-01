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

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import io.smallrye.jwt.auth.cdi.NullJsonWebToken;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * VaadinSessionFilter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@VaadinServiceScoped
@VaadinServiceEnabled
@NoArgsConstructor
@Slf4j
public class VaadinSessionFilter implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(
                initEvent -> {
                    log.info("A new Session has been initialized. session='{}', principal='{}'",
                            initEvent.getSession().getSession().getId(),
                            Optional.ofNullable(initEvent.getRequest().getUserPrincipal()).orElse(new NullJsonWebToken()).getName());
                });

        event.getSource().addUIInitListener(
                initEvent -> log.info("A new UI has been initialized. session='{}', ui='{}'",
                        initEvent.getUI().getSession().getSession().getId(),
                        initEvent.getUI().getId()));
    }

}
