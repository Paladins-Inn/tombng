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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youthlin.avatar.Gravatar;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Person -- A default UserDetails implementation for use with the TOMB.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-28
 */
@SuppressWarnings("com.haulmont.jpb.LombokEqualsAndHashCodeInspection")
@Entity
@Table(
        name = "PERSONS",
        uniqueConstraints = {
                @UniqueConstraint(name = "PERSONS_SUBJECT_UK", columnNames = {"ISSUER", "SUBJECT"}),
                @UniqueConstraint(name = "PERSONS_NAME_UK", columnNames = {"NAME"})
        }
)
@NamedQueries({
        @NamedQuery(name = "Person.bySubject", query = "SELECT p FROM Person p WHERE p.issuer = :issuer and p.subject = :subject")
})
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = true)
public class Person extends PanacheEntityBase implements Profile {
    @Id
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Getter
    @EqualsAndHashCode.Exclude
    public UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    @EqualsAndHashCode.Exclude
    public Long version;

    @Column(name = "ISSUER", length = 255, nullable = false)
    public String issuer;

    @Column(name = "SUBJECT", length = 100, nullable = false)
    public String subject;

    @Getter
    @Column(name = "NAME", length = 100, nullable = false)
    public String name;

    @Getter
    @Column(name = "EMAIL", length = 100, nullable = false)
    public String email;

    @Column(name = "LAST_LOGIN")
    @Getter
    @EqualsAndHashCode.Exclude
    @Builder.Default
    public OffsetDateTime lastLogin = OffsetDateTime.now(ZoneOffset.UTC);


    @Column(name = "CREATED", nullable = false)
    @CreationTimestamp
    @Getter
    @EqualsAndHashCode.Exclude
    public Instant created;

    @Column(name = "UPDATED")
    @UpdateTimestamp
    @Getter
    @EqualsAndHashCode.Exclude
    public Instant updated;


    @Transient
    @JsonIgnore
    @Getter
    public DefaultJWTCallerPrincipal principal;

    @Transient
    @JsonIgnore
    @Getter
    public String roleClaim;


    public static Person findByIssuerAndSubject(final String issuer, final String subject) {
        log.trace("Loading from db. issuer='{}', subject='{}'", issuer, subject);

        return find("#Person.bySubject", Parameters.with("issuer", issuer).and("subject", subject))
                .firstResult();
    }

    public static Long deleteByIssuerAndSubject(final String issuer, final String subject) {
        return delete("#subject", Parameters.with("issuer", issuer).and("subject", subject));
    }

    @Override
    public String getTenant() {
        return principal.getIssuer();
    }

    @Override
    public String getAvatar() {
        return Gravatar.getUrlWithEmail(getEmail());
    }

    @Override
    public boolean isUserInRole(String role) {
        return principal != null
                && getRoles().contains(role);
    }

    @Override
    public Set<String> getRoles() {
        return principal != null ? principal.getClaim(roleClaim) : Collections.emptySet();
    }
}
