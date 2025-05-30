/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.jpa.model;

import java.io.Serial;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.DistributionSetMetadata;

/**
 * Meta data for {@link DistributionSet}.
 */
@NoArgsConstructor // Default constructor needed for JPA entities.
@Getter
@IdClass(DsMetadataCompositeKey.class)
@Entity
@Table(name = "sp_ds_metadata")
public class JpaDistributionSetMetadata extends AbstractJpaMetaData implements DistributionSetMetadata {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ds", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_ds_metadata_ds"))
    private JpaDistributionSet distributionSet;

    public JpaDistributionSetMetadata(final String key, final String value) {
        super(key, value);
    }

    public JpaDistributionSetMetadata(final String key, final DistributionSet distributionSet, final String value) {
        super(key, value);
        this.distributionSet = (JpaDistributionSet) distributionSet;
    }

    public DsMetadataCompositeKey getId() {
        return new DsMetadataCompositeKey(distributionSet.getId(), getKey());
    }

    public void setDistributionSet(final DistributionSet distributionSet) {
        this.distributionSet = (JpaDistributionSet) distributionSet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((distributionSet == null) ? 0 : distributionSet.hashCode());
        return result;
    }

    @Override
    // exception squid:S2259 - obj is checked for null in super
    @SuppressWarnings("squid:S2259")
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final JpaDistributionSetMetadata other = (JpaDistributionSetMetadata) obj;
        if (distributionSet == null) {
            return other.distributionSet == null;
        } else {
            return distributionSet.equals(other.distributionSet);
        }
    }
}
