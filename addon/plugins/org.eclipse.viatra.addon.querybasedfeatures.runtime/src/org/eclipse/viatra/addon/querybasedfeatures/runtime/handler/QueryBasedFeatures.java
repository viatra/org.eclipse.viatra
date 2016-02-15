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
package org.eclipse.viatra.addon.querybasedfeatures.runtime.handler;

import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatures {

	public static final String ANNOTATION_LITERAL  		= "QueryBasedFeature";
	public static final String ANNOTATION_SOURCE 		= "org.eclipse.viatra.query.querybasedfeature";
    public static final String PATTERN_FQN_KEY 			= "patternFQN";
    public static final String ECORE_ANNOTATION     	= "http://www.eclipse.org/emf/2002/Ecore";
    public static final String SETTING_DELEGATES_KEY  	= "settingDelegates";
    		  
    public static SingleValueQueryBasedFeature newSingleValueFeature(EStructuralFeature feature, boolean keepCache) {
        return new SingleValueQueryBasedFeature(feature, keepCache);
    }
    
    public static MultiValueQueryBasedFeature newMultiValueFeatue(EStructuralFeature feature, boolean keepCache) {
        return new MultiValueQueryBasedFeature(feature, keepCache);
    }
    
    public static SumQueryBasedFeature newSumFeature(EStructuralFeature feature) {
        return new SumQueryBasedFeature(feature, QueryBasedFeatureKind.SUM);
    }
 
    public static boolean checkEcoreAnnotation(EPackage pckg, EStructuralFeature feature, String patternFQN, boolean useModelCode) {
    	boolean annotationsOK = true;
    	if(useModelCode){
    		annotationsOK = QueryBasedFeatures.checkEcorePackageAnnotation(pckg);
    	}
    	annotationsOK &= QueryBasedFeatures.checkFeatureAnnotation(feature, patternFQN);
    	
    	return annotationsOK;
	}
    
    public static boolean checkEcorePackageAnnotation(EPackage pckg) {
    	return Iterables.any(pckg.getEAnnotations(), new Predicate<EAnnotation>() {
			@Override
			public boolean apply(EAnnotation annotation) {
				if(QueryBasedFeatures.ECORE_ANNOTATION.equals(annotation.getSource())){
					return Iterables.any(annotation.getDetails().entrySet(), new Predicate<Entry<String, String>>() {
						@Override
						public boolean apply(Entry<String, String> entry) {
							if(QueryBasedFeatures.SETTING_DELEGATES_KEY.equals(entry.getKey())){
								StringTokenizer delegateTokents = new StringTokenizer(entry.getValue());
						    	while(delegateTokents.hasMoreTokens()){
						    		if(QueryBasedFeatures.ANNOTATION_SOURCE.equals(delegateTokents.nextToken())){
						    			return true;
						    		}
						    	}
							}
							return false;
						}
					});
				}
				return false;
			}
		});
    }

    public static boolean checkFeatureAnnotation(EStructuralFeature feature, final String patternFQN) {
    	return Iterables.any(feature.getEAnnotations(), new Predicate<EAnnotation>() {
			@Override
			public boolean apply(EAnnotation annotation) {
				if(QueryBasedFeatures.ANNOTATION_SOURCE.equals(annotation.getSource())){
					return Iterables.any(annotation.getDetails().entrySet(), new Predicate<Entry<String, String>>() {
						@Override
						public boolean apply(Entry<String, String> entry) {
							boolean keyOK = QueryBasedFeatures.PATTERN_FQN_KEY.equals(entry.getKey());
							boolean valueOK = patternFQN.equals(entry.getValue());
							return keyOK && valueOK;
						}
					});
				}
				return false;
			}
		});
    }
}
