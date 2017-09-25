/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.tooling

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue
import org.eclipse.viatra.query.tooling.core.generator.ExtensionGenerator
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragment
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.runtime.extensibility.PQueryExtensionFactory
import org.eclipse.viatra.query.runtime.extensibility.ViatraQueryRuntimeConstants
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider

/**
 * @author Abel Hegedus
 *
 */
class SurrogateGeneratorFragment implements IGenerationFragment {
    
    @Inject protected IVQGenmodelProvider provider
    @Inject protected Logger logger
    @Inject protected IErrorFeedback errorFeedback
    @Inject protected extension ExtensionGenerator exGen
    @Inject protected extension EMFPatternLanguageJvmModelInferrerUtil inferrerUtil
    
    protected static String SURROGATE_ERROR_CODE        = "org.eclipse.viatra.query.runtime.surrogatequeryemf.error"
    protected static String SURROGATE_EXTENSION_PREFIX  = "extension.surrogate."
  
    override getRemovableExtensions() {
        newArrayList(
            Pair::of(SURROGATE_EXTENSION_PREFIX, ViatraQueryRuntimeConstants.SURROGATE_QUERY_EXTENSIONID)
        )
    }
    
    override removeExtension(Pattern pattern) {
        newArrayList(
            Pair::of(pattern.derivedContributionId, ViatraQueryRuntimeConstants.SURROGATE_QUERY_EXTENSIONID)
        )
    }
    
    override extensionContribution(Pattern pattern) {
        val surrogateExtension = newArrayList(
          // create surrogate query extension using nsUri, classifier name, feature name and query FQN
          contribExtension(pattern.derivedContributionId, ViatraQueryRuntimeConstants.SURROGATE_QUERY_EXTENSIONID) [
            pattern.gatherSurrogateParameters.forEach [ parameters |
              contribElement(it, "surrogate-query-emf") [
                contribAttribute(it, "package-nsUri", parameters.ePackage.nsURI)
                contribAttribute(it, "class-name", parameters.source.name)
                contribAttribute(it, "feature-name", parameters.feature.name)
                contribAttribute(it, "query-fqn", pattern.fullyQualifiedName)
                contribAttribute(it, "surrogate-query", 
                    typeof(PQueryExtensionFactory).canonicalName + ":" + pattern.findInferredSpecification.qualifiedName
                )
              ]
            ]
          ]
        )
        return surrogateExtension
    }
    
    def protected gatherSurrogateParameters(Pattern pattern) {
        val parameterList = newArrayList()
        for(annotation : pattern.getAnnotationsByName("Surrogate")) {
          try{
            parameterList += pattern.processAnnotation(annotation, false)
          } catch(IllegalArgumentException e){
            logger.error(e.message)
          }
        }
        if(parameterList.empty){
          return newArrayList()
        }
        return parameterList
    }
    
    def protected derivedContributionId(Pattern pattern) {
        SURROGATE_EXTENSION_PREFIX+getFullyQualifiedName(pattern)
    }
  
    override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
        pattern.gatherSurrogateParameters.forEach[ parameters |
            //TODO readd dynamic surrogate support 
            //SurrogateQueryRegistry.instance.addDynamicSurrogateQueryForFeature(parameters.feature, pattern.fullyQualifiedName)
        ]
    }
    
    override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
        pattern.gatherSurrogateParameters.forEach[ parameters |
            //TODO readd dynamic surrogate support 
//            SurrogateQueryRegistry.instance.removeDynamicSurrogateQueryForFeature(parameters.feature)
        ]
    }
    
    
    override getAdditionalBinIncludes() {
        return newArrayList()
    }
    
    override getProjectDependencies() {
        return newArrayList()
    }
    
    override getProjectPostfix() {
        return null
    }
    
    
    def protected processAnnotation(Pattern pattern, Annotation annotation, boolean feedback){
        val parameters = new QueryBasedFeatureParameters
        parameters.pattern = pattern
        parameters.annotation = annotation
        
        var featureTmp = ""
    
        if(pattern.parameters.size != 2){
          if(feedback)
            errorFeedback.reportError(pattern,"Pattern must have exactly 2 parameters!", SURROGATE_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Surrogate pattern "+pattern.fullyQualifiedName+" must have 2 parameters!")
        }
    
        for (ap : annotation.parameters) {
          if (ap.name.matches("feature")) {
            featureTmp = (ap.value as StringValue).value
          }
        }
    
        if(featureTmp == ""){
          featureTmp = pattern.name
        }
    
        val sourcevar = pattern.parameters.get(0)
        val sourceType = sourcevar.type
        if(!(sourceType instanceof ClassType) || !((sourceType as ClassType).classname instanceof EClass)){
          if(feedback)
            errorFeedback.reportError(sourcevar,"Source " + sourcevar.name +" is not EClass!", SURROGATE_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Surrogate pattern "+pattern.fullyQualifiedName+": Source " + sourcevar.name +" is not EClass!")
        }
        var source = (sourceType as ClassType).classname as EClass
    
        parameters.source = source
    
        if(source === null || source.EPackage === null){
          if(feedback)
            errorFeedback.reportError(sourcevar,"Source EClass or EPackage not found!", SURROGATE_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Surrogate pattern "+pattern.fullyQualifiedName+": Source EClass or EPackage not found!")
        }
        val pckg = source.EPackage
        if(pckg === null){
          if(feedback)
            errorFeedback.reportError(sourcevar,"EPackage not found!", SURROGATE_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Surrogate pattern "+pattern.fullyQualifiedName+": EPackage not found!")
        }
        parameters.ePackage = pckg
    
    
        val featureString = featureTmp
        val features = source.EAllStructuralFeatures.filter[it.name == featureString]
        if(features.size != 1){
          if(feedback)
            errorFeedback.reportError(annotation,"Feature " + featureTmp +" not found in class " + source.name +"!", SURROGATE_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Surrogate pattern "+pattern.fullyQualifiedName+": Feature " + featureTmp +" not found in class " + source.name +"!")
        }
        val feature = features.iterator.next
        parameters.feature = feature
        parameters.targetVar = pattern.parameters.get(1).name
        
        return parameters
    }
}

class SurrogateQueryParameters{

    public Pattern pattern
    public Annotation annotation
    public EPackage ePackage
    public EClass source
    public EClass target
    public EStructuralFeature feature
      
}