/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Zoltan Ujhelyi - lazy loading support
 *******************************************************************************/
package org.eclipse.incquery.runtime.internal;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.util.IProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 *
 */
public class ExtensionBasedSurrogateQueryLoader {

    private static final String DUPLICATE_SURROGATE_QUERY = "Duplicate surrogate query definition %s for feature %s of EClass %s in package %s (FQN in map %s, contributing plug-ins %s, plug-in %s)";
    static final String EXTENSIONID = "org.eclipse.incquery.patternlanguage.emf.surrogatequeryemf";

    private Multimap<String, String> contributingPluginOfFeatureMap = HashMultimap.create();
    private Map<EStructuralFeature, PQueryProvider> contributedSurrogateQueries;

    private static final ExtensionBasedSurrogateQueryLoader INSTANCE = new ExtensionBasedSurrogateQueryLoader();

    /**
     * A provider implementation for PQuery instances based on extension elements. It is expected that the getter will only
     * @author stampie
     *
     */
    private static final class PQueryProvider implements IProvider<PQuery> {

    	private final IConfigurationElement element;
    	private PQuery query;
    	
		public PQueryProvider(IConfigurationElement element) {
			this.element = element;
			this.query = null;
		}

		@Override
		public PQuery get() {
			try {
				if (query == null) {
					query = (PQuery) element.createExecutableExtension("surrogate-query");
				}
				return query;
			} catch (CoreException e) {
				throw new IllegalArgumentException("Error initializing surrogate query", e);
			}
		}
    }
    
    public static ExtensionBasedSurrogateQueryLoader instance() {
        return INSTANCE;
    }

    public void loadKnownSurrogateQueriesIntoRegistry() {
        Map<EStructuralFeature, PQueryProvider> knownSurrogateQueryFQNs = getSurrogateQueryProviders();
        for (Entry<EStructuralFeature, PQueryProvider> entry : knownSurrogateQueryFQNs.entrySet()) {
        	final IInputKey inputKey = new EStructuralFeatureInstancesKey(entry.getKey());
            SurrogateQueryRegistry.instance().registerSurrogateQueryForFeature(inputKey, entry.getValue());
        }
    }
    
    private Map<EStructuralFeature, PQueryProvider> getSurrogateQueryProviders() {
        if(contributedSurrogateQueries != null) {
            return contributedSurrogateQueries;
        }
        contributedSurrogateQueries = Maps.newHashMap();
        if (Platform.isRunning()) {

            final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    EXTENSIONID);
            for (IConfigurationElement e : config) {
            	if (e.isValid()) {
            		processExtension(e);
            	}
            }
        }
        return contributedSurrogateQueries;
    }

    private void processExtension(IConfigurationElement el) {
        
        try {
            String packageUri = el.getAttribute("package-nsUri");
            String className = el.getAttribute("class-name");
            String featureName = el.getAttribute("feature-name");
            String queryFqn = el.getAttribute("query-fqn");
            if (queryFqn == null) {
            	queryFqn = "";
            }
            PQueryProvider surrogateQueryProvider = new PQueryProvider(el); 
            
            String contributorName = el.getContributor().getName();
            StringBuilder featureIdBuilder = new StringBuilder();
            checkState(packageUri != null, "Package NsURI cannot be null in extension");
            checkState(className != null, "Class name cannot be null in extension");
            checkState(featureName != null, "Feature name cannot be null in extension");
            
            EPackage pckg = EPackage.Registry.INSTANCE.getEPackage(packageUri);
            featureIdBuilder.append(packageUri);
            checkState(pckg != null, "Package %s not found! (plug-in %s)", packageUri, contributorName);
            
            EClassifier clsr = pckg.getEClassifier(className);
            featureIdBuilder.append("##").append(className);
            checkState(clsr instanceof EClass, "EClassifier %s does not exist in package %s! (plug-in %s)", className, packageUri, contributorName);
            
            EClass cls = (EClass) clsr;
            EStructuralFeature feature = cls.getEStructuralFeature(featureName);
            featureIdBuilder.append("##").append(featureName);
            checkState(feature != null, "Feature %s of EClass %s in package %s not found! (plug-in %s)", featureName, className, packageUri, contributorName);
            PQueryProvider fqnInMap = contributedSurrogateQueries.get(feature);
            if(fqnInMap != null) {
                String duplicateSurrogateFormatString = DUPLICATE_SURROGATE_QUERY;
                Collection<String> contributorPlugins = contributingPluginOfFeatureMap.get(featureIdBuilder.toString());
                String duplicateSurrogateMessage = String.format(duplicateSurrogateFormatString, queryFqn, featureName, className, packageUri, fqnInMap, contributorPlugins, contributorName);
                throw new IllegalStateException(duplicateSurrogateMessage);
            }
            contributedSurrogateQueries.put(feature, surrogateQueryProvider);
            contributingPluginOfFeatureMap.put(featureIdBuilder.toString(), contributorName);
        } catch (Exception e) {
            final Logger logger = Logger.getLogger(SurrogateQueryRegistry.class);
            logger.error("Surrogate query registration failed", e);
        }
    }
}
