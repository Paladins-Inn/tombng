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

import com.vaadin.flow.component.internal.UIInternalUpdater;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.quarkus.context.BeanProvider;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * KesUI -- a custom Vaadin UI for having access to CDI injection.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@NoArgsConstructor
@Slf4j
public class KesUI extends com.vaadin.flow.component.UI {

    @ConfigProperty(name = "application.name", defaultValue = "Application")
    String appName;

    public KesUI(UIInternalUpdater internalsHandler) {
        super(internalsHandler);
    }

    @Override
    public void init(VaadinRequest request) {
        BeanProvider.injectFields(this);

        log.debug("Injected fields. appName={}", appName);
    }

    @PostConstruct
    public void init() {
        log.info("UI created. appName={}", appName);
    }

    @WebServlet(
            urlPatterns = {"/ui/*", "/VAADIN/*", "/frontend/*"},
            name = "Vaadin UI",
            asyncSupported = true
    )
    @Slf4j
    public static class UIServlet extends QuarkusVaadinServlet {
        @Override
        public void init(final ServletConfig servletConfig)
                throws ServletException {
            super.init(servletConfig);
            log.debug("Initialized servlet. servletConfig={}", servletConfig);
        }

        @Override
        public void destroy() {
            log.debug("Destroy servlet.");
            super.destroy();
        }

        @Override
        protected void service(final HttpServletRequest request,
                               final HttpServletResponse response)
                throws ServletException, IOException {
            log.trace("Servlet request. request={}, response={}", request, response);

            super.service(request, response);
        }
    }
}
