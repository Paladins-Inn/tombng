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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import de.kaiserpfalzedv.commons.vaadin.i18n.DefaultComponentsI18nKeys;
import de.kaiserpfalzedv.commons.vaadin.profile.UserDetails;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * PermissionChecker --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-05
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Dependent
@Slf4j
public class PermissionChecker {
    @ConfigProperty(name = "quarkus.oidc.authentication.redirect-path", defaultValue = "/ui/app/")
    String applicationLoginPage;

    /**
     * Checks if the target is permitted for the current logged in user.
     *
     * @param target class to check for annotations.
     * @return if the class is denied for everyone.
     */
    public boolean hasPermission(Class<?> target, UserDetails userInfo) {
        return !isDeniedToAll(target)
                && !needsAdditionalAuthentication(target, userInfo)
                && (isPermittedForAll(target) || userHasRole(target, userInfo));
    }

    /**
     * If the target is annotated with {@link DenyAll} nobody is allowed to access the target.
     *
     * @param target the class to check for annotations.
     * @return TRUE if the class is denied for everyone.
     */
    private boolean isDeniedToAll(Class<?> target) {
        boolean result = target.isAnnotationPresent(DenyAll.class);

        log.trace("checked for deny-all annotation. view='{}', deny-all='{}'",
                target.getSimpleName(),
                result
        );

        return result;
    }


    /**
     * A target is permitted for all when it either has the {@link PermitAll} annotation or has no security annotation
     * at all.
     *
     * @param target The class to check for the annotation.
     * @return TRUE if there are no access restrictions at all.
     */
    private boolean isPermittedForAll(Class<?> target) {
        boolean result = target.isAnnotationPresent(PermitAll.class)
                || (!target.isAnnotationPresent(DenyAll.class) && !target.isAnnotationPresent(RolesAllowed.class));

        log.trace("checked for permit-all annotation. view='{}', permit-all='{}'",
                target.getSimpleName(),
                result
        );

        return result;
    }

    private boolean userHasRole(Class<?> target, UserDetails userInfo) {
        String[] rolesAllowed = target.getAnnotation(RolesAllowed.class) != null ?
                target.getAnnotation(RolesAllowed.class).value()
                : new String[0];

        log.trace("checking for roles-allowed. view='{}', roles-allowed='{}'",
                target.getSimpleName(),
                rolesAllowed
        );

        return (userInfo != null)
                && Arrays.stream(rolesAllowed).anyMatch(r -> userInfo.isUserInRole(r));
    }

    private String neededRoles(Class<?> target) {
        if (target.isAnnotationPresent(RolesAllowed.class)) {
            return Arrays.stream(target.getAnnotation(RolesAllowed.class).value()).collect(Collectors.joining(",", "[", "]"));
        } else {
            return "";
        }
    }

    private boolean needsAdditionalAuthentication(Class<?> target, UserDetails userInfo) {
        boolean result = (userInfo == null) && target.isAnnotationPresent(Authenticated.class);

        log.trace("checking for authentication required. view='{}', authenticated='{}'", target.getSimpleName(), result);

        return result;
    }

    public void denyEntryToProtectedPagesWithoutPermission(Class<?> target, UI ui, UserDetails userInfo) {
        if (!hasPermission(target, userInfo)) {
            log.error("user has no permission to enter the page. session='{}', target='{}'",
                    ui.getSession().getSession().getId(),
                    target.getSimpleName()
            );

            if (userInfo != null) {
                log.info("User has no permission for the target page. Stay on this page!");
                ui.access(() -> {
                    ui.getPage().getHistory().go(0);
                    Notification.show(ui.getTranslation(DefaultComponentsI18nKeys.ERROR_NO_ACCESS, neededRoles(target)));
                });
            } else {
                log.info("User is not logged in. Will be redirected to login page for protected page access.");
                ui.access(() -> {
                    ui.getPage().setLocation(applicationLoginPage);
                    ui.close();
                    ui.getSession().close();
                });
                log.trace("Redirected ...");
            }
        }
    }
}
