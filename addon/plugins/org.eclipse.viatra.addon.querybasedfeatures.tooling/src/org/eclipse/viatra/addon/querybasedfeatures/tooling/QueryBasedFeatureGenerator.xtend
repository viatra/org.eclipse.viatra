/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
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
import java.util.Map
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureKind
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.QueryBasedFeatures
import org.eclipse.viatra.query.tooling.core.generator.ExtensionGenerator
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragment
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper.*
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider

/**
 * @author Abel Hegedus
 *
 */
class QueryBasedFeatureGenerator implements IGenerationFragment {

  @Inject protected IVQGenmodelProvider provider
  @Inject protected Logger logger
  @Inject protected IErrorFeedback errorFeedback
  @Inject protected extension ExtensionGenerator exGen
  
  protected static String DERIVED_EXTENSION_POINT   = "org.eclipse.viatra.query.runtime.base.wellbehaving.derived.features"
  protected static String DERIVED_ERROR_CODE        = "org.eclipse.viatra.query.runtime.querybasedfeature.error"
  protected static String DERIVED_EXTENSION_PREFIX  = "extension.derived."

  protected static Map<String,QueryBasedFeatureKind> kinds = newHashMap(
    Pair::of("single", QueryBasedFeatureKind::SINGLE_REFERENCE),
    Pair::of("many", QueryBasedFeatureKind::MANY_REFERENCE),
    Pair::of("sum", QueryBasedFeatureKind::SUM),
    Pair::of("iteration", QueryBasedFeatureKind::ITERATION)
  )
  
  private SettingDelegateBasedGenerator delegateBasedGenerator = new SettingDelegateBasedGenerator(this)
  
  override getAdditionalBinIncludes() {
    return newArrayList()
  }

  override getProjectDependencies() {
    return newArrayList("org.eclipse.viatra.addon.querybasedfeatures.runtime")
  }

  override getProjectPostfix() {
    return null
  }
  
  override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
    processAnnotations(pattern, true)
  }
  
  override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
    processAnnotations(pattern, false)
  }
  
  def private processAnnotations(Pattern pattern, boolean generate) {
    for(annotation : pattern.getAnnotationsByName(QueryBasedFeatures::ANNOTATION_LITERAL)) {
        delegateBasedGenerator.updateAnnotations(pattern, annotation, generate)
    }
  }
  
  override extensionContribution(Pattern pattern) {
    val parameterList = newArrayList()
    for(annotation : pattern.getAnnotationsByName(QueryBasedFeatures::ANNOTATION_LITERAL)) {
      try{
        parameterList += pattern.processDerivedFeatureAnnotation(annotation, false)
      } catch(IllegalArgumentException e){
        logger.error(e.message)
      }
    }
    if(parameterList.empty){
      return newArrayList()
    }
    val wellbehaving = newArrayList(
      // create well-behaving extension using nsUri, classifier name and feature name
      contribExtension(pattern.derivedContributionId, DERIVED_EXTENSION_POINT) [
        parameterList.forEach [ parameters |
          contribElement(it, "wellbehaving-derived-feature") [
            contribAttribute(it, "package-nsUri", parameters.ePackage.nsURI)
            contribAttribute(it, "classifier-name", parameters.source.name)
            contribAttribute(it, "feature-name", parameters.feature.name)
          ]
        ]
      ]
    )
    return wellbehaving
  }
  
  override getRemovableExtensions() {
    newArrayList(
      Pair::of(DERIVED_EXTENSION_PREFIX, DERIVED_EXTENSION_POINT)
    )
  }
  
  override removeExtension(Pattern pattern) {
    newArrayList(
      Pair::of(pattern.derivedContributionId, DERIVED_EXTENSION_POINT)
    )
  }
  
  def protected derivedContributionId(Pattern pattern) {
    DERIVED_EXTENSION_PREFIX+getFullyQualifiedName(pattern)
  }
  
  def protected processDerivedFeatureAnnotation(Pattern pattern, Annotation annotation, boolean feedback){
    val parameters = new QueryBasedFeatureParameters
    parameters.pattern = pattern
    parameters.annotation = annotation
    
    var sourceTmp = ""
    var targetTmp = ""
    var featureTmp = ""
    var kindTmp = ""
    var keepCacheTmp = true
    var useAsSurrogate = false

    if(pattern.parameters.size < 2){
      if(feedback)
        errorFeedback.reportError(pattern,"Pattern has less than 2 parameters!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+" has less than 2 parameters!")
    }

    for (ap : annotation.parameters) {
      if (ap.name.matches("source")) {
        sourceTmp = (ap.value as VariableReference).getVar
      } else if (ap.name.matches("target")) {
        targetTmp = (ap.value as VariableReference).getVar
      } else if (ap.name.matches("feature")) {
        featureTmp = (ap.value as StringValue).value
      } else if (ap.name.matches("kind")) {
        kindTmp = (ap.value as StringValue).value
      } else if (ap.name.matches("keepCache")) {
        keepCacheTmp = ap.value.getValue(Boolean)
      } else if (ap.name.matches("useAsSurrogate")) {
        useAsSurrogate = ap.value.getValue(Boolean)
      }
    }

    if(featureTmp == ""){
      featureTmp = pattern.name
    }

    if(sourceTmp == ""){
      sourceTmp = pattern.parameters.get(0).name
    }
    if(!pattern.parameterPositionsByName.keySet.contains(sourceTmp)){
      if(feedback)
        errorFeedback.reportError(annotation,"No parameter for source " + sourceTmp +" !", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": No parameter for source " + sourceTmp +" !")
    }

    val sourcevar = pattern.parameters.get(pattern.parameterPositionsByName.get(sourceTmp))
    val sourceType = sourcevar.type
    if(!(sourceType instanceof ClassType) || !((sourceType as ClassType).classname instanceof EClass)){
      if(feedback)
        errorFeedback.reportError(sourcevar,"Source " + sourceTmp +" is not EClass!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": Source " + sourceTmp +" is not EClass!")
    }
    var source = (sourceType as ClassType).classname as EClass

    parameters.sourceVar = sourceTmp
    parameters.source = source

    if(source === null || source.EPackage === null){
      if(feedback)
        errorFeedback.reportError(sourcevar,"Source EClass or EPackage not found!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": Source EClass or EPackage not found!")
    }
    val pckg = source.EPackage
    if(pckg === null){
      if(feedback)
        errorFeedback.reportError(sourcevar,"EPackage not found!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": EPackage not found!")
    }
    parameters.ePackage = pckg


    val featureString = featureTmp
    val features = source.EAllStructuralFeatures.filter[it.name == featureString]
    if(features.size != 1){
      if(feedback)
        errorFeedback.reportError(annotation,"Feature " + featureTmp +" not found in class " + source.name +"!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": Feature " + featureTmp +" not found in class " + source.name +"!")
    }
    val feature = features.iterator.next
    parameters.feature = feature
    
    parameters.useAsSurrogate = useAsSurrogate   
    if(! useAsSurrogate){
        if(!(feature.derived && feature.transient && feature.volatile)){
          if(feedback)
            errorFeedback.reportError(annotation,"Feature " + featureTmp +" must be set derived, transient, volatile!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
          throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": Feature " + featureTmp +" must be set derived, transient, volatile!")
        }
    
        // if resource is not writable, the generation will fail
        val resource = pckg.eResource();
        val uri = resource.getURI();
        // only file and platform resource URIs are considered safely writable
        if (!(uri.isFile() || uri.isPlatformResource())) {
          parameters.resourceWritable = false
          val useModelCodeRef = annotation.getFirstAnnotationParameter("generateIntoModelCode");
          var useModelCode = false;
          if(useModelCodeRef !== null){
            useModelCode = useModelCodeRef.getValue(Boolean)
          }
          val annotationsOK = QueryBasedFeatures::checkEcoreAnnotation(pckg, feature, pattern.fullyQualifiedName, useModelCode)
          if(!annotationsOK){
              val message = String.format(
                "Ecore package of %s must be writable by Query-based Feature generator, but resource with URI %s is not!",
                source.getName(),
                uri.toString()
              )
              errorFeedback.reportError(sourcevar, message, DERIVED_ERROR_CODE, Severity::ERROR,
                IErrorFeedback::FRAGMENT_ERROR_TYPE);
              throw new IllegalArgumentException(
                "Query-based feature pattern " + pattern.fullyQualifiedName + ": " + message)
          }
        }   
    }
    
    if(kindTmp == ""){
      if(feature.many){
        kindTmp = "many"
      } else {
        kindTmp = "single"
      }
    }

    if(!kinds.keySet.contains(kindTmp)){
      if(feedback)
        errorFeedback.reportError(annotation,"Kind not set, or not in " + kinds.keySet + "!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
      throw new IllegalArgumentException("Query-based feature pattern "+pattern.fullyQualifiedName+": Kind not set, or not in " + kinds.keySet + "!")
    }
    val kind = kinds.get(kindTmp)
    parameters.kind = kind
    
    if(targetTmp == ""){
      targetTmp = pattern.parameters.get(1).name
    } else {
      if(!pattern.parameterPositionsByName.keySet.contains(targetTmp)){
        if(feedback)
          errorFeedback.reportError(annotation,"Target " + targetTmp +" not set or no such parameter!", DERIVED_ERROR_CODE, Severity::ERROR, IErrorFeedback::FRAGMENT_ERROR_TYPE)
        throw new IllegalArgumentException("Derived feature pattern "+pattern.fullyQualifiedName+": Target " + targetTmp +" not set or no such parameter!")
      }
    }
    parameters.targetVar = targetTmp
    parameters.keepCache = keepCacheTmp
    
    return parameters
  }
}

class QueryBasedFeatureParameters{

  public Pattern pattern
  public Annotation annotation

  public String sourceVar
  public String targetVar

  public EPackage ePackage
  public EClass source
  public EClass target
  public EStructuralFeature feature
  
  public QueryBasedFeatureKind kind
  public boolean keepCache
  public boolean useAsSurrogate
  
  public boolean resourceWritable = true
  
}