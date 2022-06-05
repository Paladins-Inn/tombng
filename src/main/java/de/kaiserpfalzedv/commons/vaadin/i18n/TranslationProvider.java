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

package de.kaiserpfalzedv.commons.vaadin.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import de.kaiserpfalzedv.commons.core.i18n.Translator;
import io.quarkus.arc.Unremovable;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@Unremovable
@VaadinServiceScoped
@VaadinServiceEnabled
public class TranslationProvider implements I18NProvider {
    public static final String NAME = TranslationProvider.class.getCanonicalName();
    private final Translator translator;

    @Inject
    public TranslationProvider(Translator translator) {
        this.translator = translator;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return translator.getProvidedLocales();
    }

    @Override
    public String getTranslation(String s, Locale locale, Object... objects) {
        return getTranslation(s, locale, objects);
    }
}
