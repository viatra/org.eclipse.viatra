/*******************************************************************************
 * Copyright (c) 2010-2014, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.tooling

import java.io.IOException
import java.util.ArrayList
import java.util.StringTokenizer
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.incquery.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.querybasedfeatures.runtime.handler.QueryBasedFeatures
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper.*
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation

/**
 * @author Abel Hegedus
 *
 */
class SettingDelegateBasedGenerator {
  
  extension QueryBasedFeatureGenerator gen
  
  new(QueryBasedFeatureGenerator generator) {
    gen = generator
  }
  
  def protected void updateAnnotations(Pattern pattern, Annotation annotation, boolean generate) {
    try{
      val parameters = pattern.processDerivedFeatureAnnotation(annotation, generate)
      if(generate){
        updateEcorePackage(parameters)
        updateFeatureAnnotation(parameters)
      } else {
        removeFeatureAnnotation(parameters)
      }
      
      try{
        parameters.ePackage.eResource.save(null)
      } catch(IOException e){
        val message = String.format("Cannot save Ecore resource %s, make sure your it is in the workspace!", parameters.ePackage.eResource.URI)
        errorFeedback.reportError(annotation, message, QueryBasedFeatureGenerator::DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
        logger.warn(message, e)
      }
    } catch(IllegalArgumentException e){
      if(generate){
        logger.error(e.message,e);
      }
    }
  }
  
  def private updateEcorePackage(QueryBasedFeatureParameters parameters) {
    try{
      val pckg = parameters.ePackage
      val annotations = new ArrayList(pckg.EAnnotations)
      var ecoreAnnotation = annotations.findFirst[
        source == QueryBasedFeatureGenerator::ECORE_ANNOTATION
      ]
      if(ecoreAnnotation == null) {
        ecoreAnnotation = EcoreFactory::eINSTANCE.createEAnnotation
        ecoreAnnotation.source = QueryBasedFeatureGenerator::ECORE_ANNOTATION
        pckg.EAnnotations.add(ecoreAnnotation)
      }
      var entry = ecoreAnnotation.details.findFirst[
        key == QueryBasedFeatureGenerator::SETTING_DELEGATES_KEY
      ]
      if(entry == null) {
        // add entry ("patternFQN", pattern.fullyQualifiedName)
        ecoreAnnotation.details.put(QueryBasedFeatureGenerator::SETTING_DELEGATES_KEY, QueryBasedFeatureGenerator::SETTING_DELEGATES_VALUE)
      } else {
        val delegates = new StringTokenizer(entry.value)
        while(delegates.hasMoreTokens){
          val delegate = delegates.nextToken
          if(delegate == QueryBasedFeatureGenerator::SETTING_DELEGATES_VALUE){
            return
          }
        }
        entry.value = entry.value + " " + QueryBasedFeatureGenerator::SETTING_DELEGATES_VALUE
      }
    } catch(Exception e){
      logger.warn(String.format("Error happened when trying to add QBF annotation to package %s in Ecore!",parameters.ePackage.name), e)
    }
  }
  
  def private updateFeatureAnnotation(QueryBasedFeatureParameters parameters) {
    try{
      val feat = parameters.feature
      val annotations = new ArrayList(feat.EAnnotations)
      annotations.forEach[
        if(it.source == QueryBasedFeatures::ANNOTATION_SOURCE) {
          feat.EAnnotations.remove(it)
        }
      ]
      val annotation = EcoreFactory::eINSTANCE.createEAnnotation
      annotation.source = QueryBasedFeatures::ANNOTATION_SOURCE
      feat.EAnnotations.add(annotation)

      annotation.details.put(QueryBasedFeatures::PATTERN_FQN_KEY, parameters.pattern.fullyQualifiedName)
    } catch(Exception e) {
      logger.warn(String.format("Error happened when trying to add QBF annotation to feature %s in Ecore!",parameters.feature.name), e)
    }
  }
  
  def private removeFeatureAnnotation(QueryBasedFeatureParameters parameters) {
    try{
      val feat = parameters.feature
      val annotations = new ArrayList(feat.EAnnotations)
      annotations.forEach[
        if(it.source == QueryBasedFeatures::ANNOTATION_SOURCE){
          feat.EAnnotations.remove(it)
        }
      ]
    } catch(Exception e){
      logger.warn(String.format("Error happened when trying to remove annotation to feature %s in Ecore!",parameters.feature), e)
    }
  }
  
}