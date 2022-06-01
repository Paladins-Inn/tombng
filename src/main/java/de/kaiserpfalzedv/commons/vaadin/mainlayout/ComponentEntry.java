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

package de.kaiserpfalzedv.commons.vaadin.mainlayout;

import com.vaadin.flow.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * ComponentEntry --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@AllArgsConstructor
@Getter
class ComponentEntry implements Map.Entry<String, Component> {
    private String key;
    private Component value;

    @Override
    public Component setValue(Component value) {
        this.value = value;

        return value;
    }
}
