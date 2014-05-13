/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.api;

import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexResourceFilter;

/**
 * The base index options indicate how the indices are built.
 * 
 * <p>
 * One of the options is to build indices in <em>wildcard mode</em>, meaning that all EClasses, EDataTypes, EReferences
 * and EAttributes are indexed. This is convenient, but comes at a high memory cost. To save memory, one can disable
 * <em>wildcard mode</em> and manually register those EClasses, EDataTypes, EReferences and EAttributes that should be
 * indexed.
 * </p>
 * 
 * <p>
 * Another choice is whether to build indices in <em>dynamic EMF mode</em>, meaning that types are identified by the
 * String IDs that are ultimately derived from the nsURI of the EPackage. Multiple types with the same ID are treated as
 * the same. This is useful if dynamic EMF is used, where there can be multiple copies (instantiations) of the same
 * EPackage, representing essentially the same metamodel. If one disables <em>dynamic EMF mode</em>, an error is logged
 * if duplicate EPackages with the same nsURI are encountered.
 * </p>
 * 
 * @author Abel Hegedus
 * 
 */
public class BaseIndexOptions {

    /**
     * 
     * By default, base indices will be constructed with wildcard mode set as false.
     */
    protected static final boolean WILDCARD_MODE_DEFAULT = false;
    /**
     * 
     * By default, base indices will be constructed with only well-behaving features traversed.
     */
    private static final boolean TRAVERS_ONLY_WELLBEHAVING_DERIVED_FEATURES_DEFAULT = true;
    /**
     * 
     * By default, base indices will be constructed with dynamic EMF mode set as false.
     */
    protected static final boolean DYNAMIC_EMF_MODE_DEFAULT = false;

    protected boolean dynamicEMFMode = DYNAMIC_EMF_MODE_DEFAULT;
    protected boolean traverseOnlyWellBehavingDerivedFeatures = TRAVERS_ONLY_WELLBEHAVING_DERIVED_FEATURES_DEFAULT;
    protected boolean wildcardMode = WILDCARD_MODE_DEFAULT;
    protected IBaseIndexObjectFilter notifierFilterConfiguration;
    protected IBaseIndexResourceFilter resourceFilterConfiguration;

    /**
     * Creates a base index options with the default values.
     */
    public BaseIndexOptions() {
    }

    /**
     * Creates a base index options using the provided values for dynamic EMF mode and wildcard mode.
     */
    public BaseIndexOptions(boolean dynamicEMFMode, boolean wildcardMode) {
        this.dynamicEMFMode = dynamicEMFMode;
        this.wildcardMode = wildcardMode;
    }

    public void setDynamicEMFMode(boolean dynamicEMFMode) {
        this.dynamicEMFMode = dynamicEMFMode;
    }

    /**
     * Adds an object-level filter to the indexer. Warning - object-level indexing can increase indexing time
     * noticeably. If possibly, use {@link #setResourceFilterConfiguration(IBaseIndexResourceFilter)} instead.
     * 
     * @param filter
     */
    public void setObjectFilterConfiguration(IBaseIndexObjectFilter filter) {
        this.notifierFilterConfiguration = filter;
    }

    /**
     * @return the selected object filter configuration, or null if not set
     */
    public IBaseIndexObjectFilter getObjectFilterConfiguration() {
        return notifierFilterConfiguration;
    }

    /**
     * Adds a resource filter
     * 
     * @param filter
     */
    public void setResourceFilterConfiguration(IBaseIndexResourceFilter filter) {
        this.resourceFilterConfiguration = filter;
    }

    /**
     * @return the selected resource filter, or null if not set
     */
    public IBaseIndexResourceFilter getResourceFilterConfiguration() {
        return resourceFilterConfiguration;
    }

    /**
     * @return whether the base index option has dynamic EMF mode set
     */
    public boolean isDynamicEMFMode() {
        return dynamicEMFMode;
    }

    /**
     * @return whether the base index option has traverse only well-behaving derived features set
     */
    public boolean isTraverseOnlyWellBehavingDerivedFeatures() {
        return traverseOnlyWellBehavingDerivedFeatures;
    }

    public void setWildcardMode(boolean wildcardMode) {
        this.wildcardMode = wildcardMode;
    }

    /**
     * @return whether the base index option has wildcard mode set
     */
    public boolean isWildcardMode() {
        return wildcardMode;
    }

    /**
     * Creates an independent copy of itself. The values of each option will be the same as this options. This method is
     * used when a provided option must be copied to avoid external option changes afterward.
     * 
     * @return the copy of this options
     */
    public BaseIndexOptions copy() {
        BaseIndexOptions baseIndexOptions = new BaseIndexOptions(this.dynamicEMFMode, this.wildcardMode);
        baseIndexOptions.traverseOnlyWellBehavingDerivedFeatures = this.traverseOnlyWellBehavingDerivedFeatures;
        baseIndexOptions.notifierFilterConfiguration = this.notifierFilterConfiguration;
        baseIndexOptions.resourceFilterConfiguration = this.resourceFilterConfiguration;
        return baseIndexOptions;
    }

}
