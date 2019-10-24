/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime.handler;

import java.util.StringTokenizer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatures {

    public static final String ANNOTATION_LITERAL  		= "QueryBasedFeature";
    public static final String ANNOTATION_SOURCE 		= "org.eclipse.viatra.query.querybasedfeature";
    public static final String LEGACY_ANNOTATION_SOURCE = "org.eclipse.incquery.querybasedfeature";
    public static final String PATTERN_FQN_KEY 			= "patternFQN";
    public static final String ECORE_ANNOTATION     	= "http://www.eclipse.org/emf/2002/Ecore";
    public static final String SETTING_DELEGATES_KEY  	= "settingDelegates";
           
    private QueryBasedFeatures() {}
    
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
        if(!useModelCode){
            annotationsOK = QueryBasedFeatures.checkEcorePackageAnnotation(pckg);
        }
        annotationsOK &= QueryBasedFeatures.checkFeatureAnnotation(feature, patternFQN);
        
        return annotationsOK;
    }
    
    public static boolean checkEcorePackageAnnotation(EPackage pckg) {
        return pckg.getEAnnotations().stream().anyMatch(annotation -> {
            if(QueryBasedFeatures.ECORE_ANNOTATION.equals(annotation.getSource())){
                return annotation.getDetails().entrySet().stream().anyMatch(entry -> {
                    if(QueryBasedFeatures.SETTING_DELEGATES_KEY.equals(entry.getKey())){
                        StringTokenizer delegateTokents = new StringTokenizer(entry.getValue());
                        while(delegateTokents.hasMoreTokens()){
                            if(QueryBasedFeatures.ANNOTATION_SOURCE.equals(delegateTokents.nextToken())){
                                return true;
                            }
                        }
                    }
                    return false;
                });
            }
            return false;
        });
    }

    public static boolean checkFeatureAnnotation(EStructuralFeature feature, final String patternFQN) {
        return feature.getEAnnotations().stream().anyMatch(annotation -> {
            if(QueryBasedFeatures.ANNOTATION_SOURCE.equals(annotation.getSource())){
                return annotation.getDetails().entrySet().stream().anyMatch(entry -> {
                    boolean keyOK = QueryBasedFeatures.PATTERN_FQN_KEY.equals(entry.getKey());
                    boolean valueOK = patternFQN.equals(entry.getValue());
                    return keyOK && valueOK;
                });
            }
            return false;
        });
    }
}
