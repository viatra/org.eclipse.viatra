/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.index;

import java.util.Map;

import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

/**
 * @author Abel Hegedus
 * @since 1.4
 */
public enum XtextIndexBasedRegistryUpdaterFactory {

    INSTANCE;
    
    private Map<IQuerySpecificationRegistry, XtextIndexBasedRegistryUpdater> updaters = Maps.newHashMap();
    
    /**
     * Returns an updater connecting the Xtext index with the given registry.
     * If an updater for the given registry exists, it is returned instead.
     * 
     * @param registry to connect with the index
     * @return the connected updater
     */
    public XtextIndexBasedRegistryUpdater getUpdater(IQuerySpecificationRegistry registry) {
        Preconditions.checkArgument(registry != null, "Registry cannot be null!");
        if(updaters.containsKey(registry)){
            return updaters.get(registry);
        } else {
            Injector injector = EMFPatternLanguageActivator.getInstance().getInjector(EMFPatternLanguageActivator.ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE);
            XtextIndexBasedRegistryUpdater updater = injector.getInstance(XtextIndexBasedRegistryUpdater.class);
            updaters.put(registry, updater);
            updater.connectIndexToRegistry(QuerySpecificationRegistry.getInstance());
            return updater;
        }
    }
    
    /**
     * Removes the updater from the map and disconnects it from the registry.
     * 
     * @param updater to be disconnected and removed from the map
     */
    public void removeUpdater(XtextIndexBasedRegistryUpdater updater) {
        Preconditions.checkArgument(updater != null, "Updater cannot be null!");
        IQuerySpecificationRegistry registry = updater.getConnectedRegistry();
        if(updater.equals(updaters.get(registry))){
            updaters.remove(updater);
            updater.disconnectIndexFromRegistry();
        }
        
    }
}
