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

package de.kaiserpfalzedv.commons.vaadin.about;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import de.kaiserpfalzedv.commons.vaadin.TraceNavigation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;

/**
 * AboutView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-28
 */
@Slf4j
@Route(value = "tos")
@RoutePrefix("free")
public class TermsOfServiceView extends FormLayout implements TraceNavigation {

    @ConfigProperty(name = "application.name", defaultValue = "Application")
    String appName;

    @ConfigProperty(name = "application.tos", defaultValue = "./.")
    String tos;

    public TermsOfServiceView() {
        add(new Label("Terms of service"));
    }

    @PostConstruct
    public void init() {
        if ("./.".equals(tos)) {
            add(new Label("No terms of service defined."));
        } else {
            add(new Label(tos));
        }
    }
}
