/**
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.jpa.rsql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * Helper class providing static access to the RSQL configuration as managed bean.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@SuppressWarnings("java:S6548") // singleton holder ensures static access to spring resources in some places
public final class RsqlConfigHolder {

    private static final RsqlConfigHolder SINGLETON = new RsqlConfigHolder();

    public enum RsqlToSpecBuilder {
        LEGACY_G1, // legacy RSQL visitor
        LEGACY_G2, // G2 RSQL visitor
        G3 // G3 RSQL visitor - still experimental / yet default
    }
    /**
     * If RSQL comparison operators shall ignore the case. If ignore case is <code>true</code> "x == ax" will match "x == aX"
     */
    @Value("${hawkbit.rsql.ignore-case:true}")
    private boolean ignoreCase;
    /**
     * Declares if the database is case-insensitive, by default assumes <code>false</code>. In case it is case-sensitive and,
     * {@link #ignoreCase} is set to <code>true</code> the SQL queries use upper case comparisons to ignore case.
     *
     * If the database is declared as case-sensitive and ignoreCase is set to <code>false</code> the RSQL queries shall use strict
     * syntax - i.e. 'and' instead of 'AND' / 'aND'. Otherwise, the queries would be case-insensitive regarding operators.
     */
    @Value("${hawkbit.rsql.case-insensitive-db:false}")
    private boolean caseInsensitiveDB;

    /**
     * @deprecated in favour fixed final visitor / spec builder of G2 RSQL visitor / G3 spec builder. since 0.6.0
     */
    @Setter // for tests only
    @Deprecated(forRemoval = true, since = "0.6.0")
    @Value("${hawkbit.rsql.rsql-to-spec-builder:G3}") //
    private RsqlToSpecBuilder rsqlToSpecBuilder;

    /**
     * @return The holder singleton instance.
     */
    public static RsqlConfigHolder getInstance() {
        return SINGLETON;
    }
}