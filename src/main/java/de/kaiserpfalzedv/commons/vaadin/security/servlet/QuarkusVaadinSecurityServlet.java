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

package de.kaiserpfalzedv.commons.vaadin.security.servlet;

import com.google.common.eventbus.EventBus;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.quarkus.QuarkusVaadinServletService;
import de.kaiserpfalzedv.commons.vaadin.SessionEventBusRegistered;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

/**
 * QuarkusVaadinSecurityServlet --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-04
 */
@WebServlet(
        urlPatterns = {"/ui/*", "/VAADIN/*"},
        name = "Vaadin UI",
        asyncSupported = true,
        initParams = {
                @WebInitParam(
                        name = InitParameters.I18N_PROVIDER,
                        value = "de.kaiserpfalzedv.commons.vaadin.i18n.TranslationProvider",
                        description = "kp-commons I18N provider."
                )
        }
)
@Dependent
@Slf4j
public class QuarkusVaadinSecurityServlet extends QuarkusVaadinServlet implements
        SessionInitListener, SessionDestroyListener, UIInitListener,
        SessionEventBusRegistered {
    @Inject
    BeanManager beanManager;

    @ConfigProperty(name = "quarkus.oidc.authentication.redirect-path", defaultValue = "/ui/app/")
    String applicationLoginPage;


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
    protected VaadinServletService createServletService(
            final DeploymentConfiguration configuration)
            throws ServiceException {
        final QuarkusVaadinServletService service = new QuarkusVaadinSecurityServletService(
                this,
                configuration,
                this.beanManager,
                this.applicationLoginPage
        );

        service.init();

        return service;
    }

    @Override
    @Transactional
    protected void service(final HttpServletRequest request,
                           final HttpServletResponse response)
            throws ServletException, IOException {
        log.trace("Servlet request. request={}, response={}", request, response);

        super.service(request, response);
    }


    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();

        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);
        getService().addUIInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        createSessionEventBuss(event.getSession().getSession());

        registerToBus();
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        unregisterFromBus();
        event.getSession().getUIs().stream().forEach(ui -> ui.getPushConfiguration().setPushMode(PushMode.DISABLED));
    }

    @Produces
    public VaadinSession vaadinSession() {
        return VaadinSession.getCurrent();
    }

    private void createSessionEventBuss(WrappedSession session) {
        if (session != null) {
            EventBus bus = (EventBus) session.getAttribute(EventBus.class.getCanonicalName());
            synchronized (this) {
                if (bus == null) {
                    log.trace("Generating session event bus. session='{}'", session.getId());
                    bus = new EventBus(session.getId());
                    session.setAttribute(EventBus.class.getCanonicalName(), bus);
                } else {
                    log.trace("Session event bus exists. session='{}', bus={}", session.getId(), bus);
                }
            }
        } else {
            log.trace("No session to generate session event bus for in servlet.");
        }
    }

    @Override
    public void uiInit(UIInitEvent event) {
        log.trace("Initialie UI. session='{}', ui='{}'",
                event.getUI().getSession().getSession().getId(),
                event.getUI().getId()
        );
    }
}
