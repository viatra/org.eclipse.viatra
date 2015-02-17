/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.comprehension;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.IncQueryBasePlugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Abel Hegedus
 * 
 */
public class WellbehavingDerivedFeatureRegistry {

	private static final String DUPLICATE_SURROGATE_QUERY = "Duplicate surrogate query definition %s for feature %s of EClass %s in package %s (FQN in map %s, contributing plug-ins %s, plug-in %s)";
	
	private static Collection<EStructuralFeature> contributedWellbehavingDerivedFeatures = Collections.newSetFromMap(new WeakHashMap<EStructuralFeature, Boolean>());
    private static Collection<EClass> contributedWellbehavingDerivedClasses = Collections.newSetFromMap(new WeakHashMap<EClass, Boolean>());
    private static Collection<EPackage> contributedWellbehavingDerivedPackages = Collections.newSetFromMap(new WeakHashMap<EPackage, Boolean>());
    private static Map<EStructuralFeature,String> surrogateQueryFQNMap = new WeakHashMap<EStructuralFeature,String>();
    private static Multimap<String, String> contributingPluginOfFeatureMap = HashMultimap.create();

    private WellbehavingDerivedFeatureRegistry() {
    }

    /**
     * Called by IncQueryBasePlugin.
     */
    public static void initRegistry() {
        getContributedWellbehavingDerivedFeatures().clear();
        getContributedWellbehavingDerivedClasses().clear();
        getContributedWellbehavingDerivedPackages().clear();

        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IExtensionPoint poi;

        poi = reg.getExtensionPoint(IncQueryBasePlugin.WELLBEHAVING_DERIVED_FEATURE_EXTENSION_POINT_ID);
        if (poi != null) {
            IExtension[] exts = poi.getExtensions();

            for (IExtension ext : exts) {

                IConfigurationElement[] els = ext.getConfigurationElements();
                for (IConfigurationElement el : els) {
                    if (el.getName().equals("wellbehaving-derived-feature")) {
                        processWellbehavingExtension(el);
                    } else {
                        throw new UnsupportedOperationException("Unknown configuration element " + el.getName()
                                + " in plugin.xml of " + el.getDeclaringExtension().getUniqueIdentifier());
                    }
                }
            }
        }
    }

    /**
     * @param el
     */
    private static void processWellbehavingExtension(IConfigurationElement el) {
        try {
            String packageUri = el.getAttribute("package-nsUri");
            String featureName = el.getAttribute("feature-name");
            String classifierName = el.getAttribute("classifier-name");
            String surrogateQueryFQN = el.getAttribute("surrogate-query-fqn");
            String contributorName = el.getContributor().getName();
            StringBuilder featureIdBuilder = new StringBuilder();
            if (packageUri != null) {
                EPackage pckg = EPackage.Registry.INSTANCE.getEPackage(packageUri);
                featureIdBuilder.append(packageUri);
                if (pckg != null) {
                    if (classifierName != null) {
                        EClassifier clsr = pckg.getEClassifier(classifierName);
                        featureIdBuilder.append("##").append(classifierName);
                        if (clsr instanceof EClass) {
                            if (featureName != null) {
                                EClass cls = (EClass) clsr;
                                EStructuralFeature feature = cls.getEStructuralFeature(featureName);
                                featureIdBuilder.append("##").append(featureName);
                                if (feature != null) {
                                    if(surrogateQueryFQN != null) {
                                    	String fqnInMap = surrogateQueryFQNMap.get(feature);
                                    	featureIdBuilder.append("##").append(surrogateQueryFQN);
										if(fqnInMap != null) {
                                    		String duplicateSurrogateFormatString = DUPLICATE_SURROGATE_QUERY;
                                    		Collection<String> contributorPlugins = contributingPluginOfFeatureMap.get(featureIdBuilder.toString());
											String duplicateSurrogateMessage = String.format(duplicateSurrogateFormatString, surrogateQueryFQN, featureName, classifierName, packageUri, fqnInMap, contributorPlugins, contributorName);
											throw new IllegalStateException(duplicateSurrogateMessage);
                                    	}
                                    	registerSurrogateQueryForFeature(feature, surrogateQueryFQN);
                                    } else {
                                    	// only well-behaving derived features without surrogate queries are handled by Base
                                    	registerWellbehavingDerivedFeature(feature);
                                    }
                                } else {
                                	throw new IllegalStateException(String.format("Feature %s of EClass %s in package %s not found! (plug-in %s)", featureName, classifierName, packageUri, contributorName));
                                }
                            } else {
                            	registerWellbehavingDerivedClass((EClass) clsr);
                            }
                        } else {
                        	throw new IllegalStateException(String.format("EClassifier %s does not exist in package %s! (plug-in %s)", classifierName, packageUri, contributorName));
                        }
                    } else {
                    	if(featureName != null){
                    		throw new IllegalStateException(String.format("Feature name must be empty if classifier name is not set! (package %s, plug-in %s)", packageUri, contributorName));
                    	}
                    	registerWellbehavingDerivedPackage(pckg);
                    }
                }
                contributingPluginOfFeatureMap.put(featureIdBuilder.toString(), contributorName);
            }
        } catch (Exception e) {
            final Logger logger = Logger.getLogger(WellbehavingDerivedFeatureRegistry.class);
            logger.error("Well-behaving feature registration failed", e);
        }
    }

    /**
	 * @return the contributingPluginOfFeatureMap
	 */
	public static Multimap<String, String> getContributingPluginOfFeatureMap() {
		return contributingPluginOfFeatureMap;
	}

	/**
	 * 
	 * @param feature
	 * @return true if the feature (or its defining EClass or ) is registered as well-behaving
	 */
	public static boolean isWellbehavingFeature(EStructuralFeature feature) {
		if(feature == null){
			return false;
		} else if (contributedWellbehavingDerivedFeatures.contains(feature)) {
	        return true;
	    } else if (contributedWellbehavingDerivedClasses.contains(feature.getEContainingClass())) {
	        return true;
	    } else if (contributedWellbehavingDerivedPackages.contains(feature.getEContainingClass().getEPackage())) {
	        return true;
	    } else {
	    	return false;
	    }
	}

	/**
     * @param feature
     */
    public static void registerWellbehavingDerivedFeature(EStructuralFeature feature) {
        contributedWellbehavingDerivedFeatures.add(feature);
    }

    /**
     * @param cls
     */
    public static void registerWellbehavingDerivedClass(EClass cls) {
        contributedWellbehavingDerivedClasses.add(cls);
    }

    /**
     * @param pkg
     */
    public static void registerWellbehavingDerivedPackage(EPackage pkg) {
        contributedWellbehavingDerivedPackages.add(pkg);
    }

    /**
	 * 
	 * @param feature
	 * @param surrogateQueryFQN
	 * @return the previous surrogate query FQN associated with feature, or null if there was no such query FQN registered
	 * @throws IllegalArgumentException if feature or surrogateQueryFQN is null 
	 */
	public static String registerSurrogateQueryForFeature(EStructuralFeature feature, String surrogateQueryFQN) {
		Preconditions.checkArgument(feature != null, "Feature must not be null!");
		Preconditions.checkArgument(surrogateQueryFQN != null, "Surrogate query FQN must not be null!");
		return surrogateQueryFQNMap.put(feature, surrogateQueryFQN);
	}

	/**
     * @return the contributedWellbehavingDerivedFeatures
     */
    public static Collection<EStructuralFeature> getContributedWellbehavingDerivedFeatures() {
        return contributedWellbehavingDerivedFeatures;
    }

    public static Collection<EClass> getContributedWellbehavingDerivedClasses() {
        return contributedWellbehavingDerivedClasses;
    }

    public static Collection<EPackage> getContributedWellbehavingDerivedPackages() {
        return contributedWellbehavingDerivedPackages;
    }

    /**
     * 
     * @param feature that may have surrogate query defined, null not allowed
     * @return true if the feature has a surrogate query defined
     * @throws IllegalArgumentException if feature is null
     */
    public static boolean hasSurrogateQueryFQN(EStructuralFeature feature) {
    	Preconditions.checkArgument(feature != null, "Feature must not be null!");
   		return surrogateQueryFQNMap.containsKey(feature);
    }
    
    /**
	 * 
	 * @param feature for which the surrogate query FQN should be returned
	 * @return the surrogate query FQN defined for the feature
	 * @throws IllegalArgumentException if feature is null
	 * @throws NoSuchElementException if the feature has no surrogate query defined, use {@link #hasSurrogateQueryFQN} to check
	 */
	public static String getSurrogateQueryFQN(EStructuralFeature feature) {
		Preconditions.checkArgument(feature != null, "Feature must not be null!");
		String surrogateFQN = surrogateQueryFQNMap.get(feature);
		if(surrogateFQN == null){
			return surrogateFQN;
		} else {
			throw new NoSuchElementException(String.format("Feature %s has no surrogate query defined! Use #hasSurrogateQueryFQN to check existence.", feature.getName()));
		}
	}

	/**
	 * @return the surrogateQueryFQNMap
	 */
	public static Map<EStructuralFeature, String> getSurrogateQueryFQNMap() {
		return surrogateQueryFQNMap;
	}
    
    
}
