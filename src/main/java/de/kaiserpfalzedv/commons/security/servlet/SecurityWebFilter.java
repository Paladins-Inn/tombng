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

package de.kaiserpfalzedv.commons.security.servlet;

import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * SecurityWebFilter --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@WebFilter(urlPatterns = {"/ui", "/ui/", "/ui/*"})
@Slf4j
public class SecurityWebFilter implements Filter {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    UserInfo userInfo;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.trace("Creating security web filter.");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.trace("security web filter. issuer='{}', subject='{}', name='{}', userInfo={}",
                idToken.getIssuer(), idToken.getSubject(), idToken.getName(), userInfo.getString("sub"));

        ((HttpServletRequest) servletRequest).getSession().setAttribute("idToken", idToken);
        ((HttpServletRequest) servletRequest).getSession().setAttribute("userInfo", userInfo);
        filterChain.doFilter(new SecurityHttpServletRequest((HttpServletRequest) servletRequest), servletResponse);

        ((HttpServletRequest) servletRequest).getSession().setAttribute("idToken", null);
        ((HttpServletRequest) servletRequest).getSession().setAttribute("userInfo", null);
    }

    @Override
    public void destroy() {
        log.trace("Destroying security web filter.");
        Filter.super.destroy();
    }
}
