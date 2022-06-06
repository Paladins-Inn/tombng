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

package de.kaiserpfalzedv.commons.vaadin.profile;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import de.kaiserpfalzedv.commons.vaadin.layouts.AbstractBaseLayout;
import io.quarkus.security.Authenticated;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.util.UUID;

import static de.kaiserpfalzedv.commons.vaadin.i18n.DefaultComponentsI18nKeys.*;

/**
 * PersonInfoView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-05
 */
@Authenticated
@PageTitle(VIEWS_PERSON_INFO)
@Dependent
@NoArgsConstructor
@Slf4j
public class PersonInfoView extends FormLayout implements HasUrlParameter<UUID>, AfterNavigationObserver {

    @Inject
    UserDetails data;

    private Binder<UserDetails> binder;

    private TextField id, name, email, tenant;

    private DateTimePicker firstLogin, lastLogin;

    @PostConstruct
    public void init() {
        binder = new Binder<>(UserDetails.class);

        id = new TextField(getTranslation(PROFILE_ID));
        binder.forField(id).bind(d -> d.getId().toString(), null);

        name = new TextField(getTranslation(PROFILE_NAME));
        setColspan(name, 2);
        binder.forField(name).bind(UserDetails::getName, null);

        email = new TextField(getTranslation(PROFILE_EMAIL));
        setColspan(email, 2);
        binder.forField(email).bind(UserDetails::getEmail, null);

        tenant = new TextField(getTranslation(PROFILE_TENANT));
        binder.forField(tenant).bind(UserDetails::getTenant, null);


        firstLogin = new DateTimePicker(getTranslation(PROFILE_LOGIN_FIRST));
        binder.forField(firstLogin).bind(d -> d.getCreated().atOffset(ZoneOffset.UTC).toLocalDateTime(), null);
        lastLogin = new DateTimePicker(getTranslation(PROFILE_LOGIN_LAST));
        binder.forField(lastLogin).bind(d -> d.getLastLogin().toLocalDateTime(), null);

        binder.readBean(data);

        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("800px", 2)
        );

        add(
                id, tenant,
                name,
                email,
                firstLogin, lastLogin
        );
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final UUID id) {
        if (id != null) {
            if (data.isUserInRole("admin")) {
                log.debug("Loading profile for user with id. id='{}'", id);

                data = Person.findById(id);

                if (data == null) {
                    getUI().ifPresent(ui -> Notification.show(
                            getTranslation(PROFILE_ERROR_ID_NOT_FOUND, id.toString()),
                            5000,
                            Notification.Position.BOTTOM_END
                    ));
                }
            } else {
                log.warn("User tried to load special profile without being admin. user='{}', tried-to-load='{}'", data.getAvatar(), id);
                getUI().ifPresent(ui -> Notification.show(
                        getTranslation(ERROR_NO_ACCESS, "admin"),
                        5000,
                        Notification.Position.BOTTOM_END
                ));
            }
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        getParent().ifPresentOrElse(
                c -> {
                    if (c instanceof AbstractBaseLayout && data != null) {
                        ((AbstractBaseLayout) c).changeViewTitle(getTranslation(VIEWS_PERSON_INFO) + " - " + data.getName());
                    }
                },
                () -> log.trace("No parent for person view.")
        );
    }
}
