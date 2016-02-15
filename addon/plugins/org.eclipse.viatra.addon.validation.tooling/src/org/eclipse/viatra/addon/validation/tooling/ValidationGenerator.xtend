/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.tooling

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.google.inject.Inject
import org.eclipse.core.runtime.Path
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ListValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue
import org.eclipse.viatra.query.tooling.core.generator.ExtensionGenerator
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragment
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IEiqGenmodelProvider
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.util.Strings

import static extension org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper.*

class ValidationGenerator
implements IGenerationFragment {

	@Inject extension EMFPatternLanguageJvmModelInferrerUtil

	@Inject
	private IEiqGenmodelProvider eiqGenModelProvider

	@Inject
	private IErrorFeedback feedback
	
	@Inject extension ExtensionGenerator exGen

	private static String VALIDATIONEXTENSION_PREFIX = "validation.constraint."
	private static String UI_VALIDATION_MENUS_PREFIX = "generated.incquery.validation.menu."
	private static String VALIDATION_EXTENSION_POINT = "org.eclipse.viatra.addon.validation.runtime.constraint"
	private static String ECLIPSE_MENUS_EXTENSION_POINT = "org.eclipse.ui.menus"
	private static String annotationLiteral = "Constraint"
	private static String VALIDATION_ERROR_CODE = "org.eclipse.incquery.validation.error"

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {

		for(ann : pattern.annotations){
		  if(ann.name == annotationLiteral){
  			fsa.generateFile(pattern.constraintClassJavaFile(ann), pattern.patternHandler(ann))
		  }
		}
	}

	override cleanUp(Pattern pattern, IFileSystemAccess fsa) {
		for(ann : pattern.annotations){
		  if(ann.name == annotationLiteral){
	     	fsa.deleteFile(pattern.constraintClassJavaFile(ann))
  		}
		}
	}

	override removeExtension(Pattern pattern) {
		val p = Pair::of(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT)
		val extensionList = newArrayList(p)

		val patternModel = pattern.eContainer as PatternModel;
    for (imp : EMFPatternLanguageHelper::getPackageImportsIterable(patternModel)) {
      val pack = imp.EPackage;
      val genPackage = eiqGenModelProvider.findGenPackage(pattern, pack);

      if (genPackage != null) {
        val editorId = genPackage.qualifiedEditorClassName+"ID";
        if (!editorId.nullOrEmpty) {
          extensionList.add(Pair::of(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT))
        }
      }
    }

    for(ann : pattern.annotations){
      if(ann.name == annotationLiteral){
        val editorIds = ann.getAnnotationParameterValue("targetEditorId")
        for (id : editorIds){
          val editorId = (id as StringValue).value
          extensionList.add(Pair::of(menuContributionId(editorId), ECLIPSE_MENUS_EXTENSION_POINT))
        }
      }
    }
		return extensionList
	}

	override getRemovableExtensions() {
		newArrayList(
			Pair::of(VALIDATIONEXTENSION_PREFIX, VALIDATION_EXTENSION_POINT),
			Pair::of(UI_VALIDATION_MENUS_PREFIX, ECLIPSE_MENUS_EXTENSION_POINT)
		)
	}

	override getProjectDependencies() {
		newArrayList("com.google.guava",
		    "org.eclipse.viatra.query.runtime",
		    "org.eclipse.viatra.addon.validation.core"
		)
	}

	override getProjectPostfix() {
		"validation"
	}

	override extensionContribution(Pattern pattern) {
		val extensionList = newArrayList(
      contribExtension(pattern.constraintContributionId, VALIDATION_EXTENSION_POINT) [
        for(ann : pattern.annotations){
          if(ann.name == annotationLiteral){
            contribElement(it, "constraint") [
              contribAttribute(it, "class", pattern.constraintClassName(ann))
              contribAttribute(it, "name", pattern.fullyQualifiedName)

              val editorIds = ann.getAnnotationParameterValue("targetEditorId")
              for (id : editorIds){
                val editorId = (id as StringValue).value
                contribElement(it, "enabledForEditor")[
                  contribAttribute(it, "editorId", editorId)
                ]
              }

              val patternModel = pattern.eContainer as PatternModel;
              for (imp : EMFPatternLanguageHelper::getPackageImportsIterable(patternModel)) {
                val pack = imp.EPackage;
                val genPackage = eiqGenModelProvider.findGenPackage(pattern, pack);

                if (genPackage != null) {
                  val editorId = genPackage.qualifiedEditorClassName+"ID";
                  contribElement(it, "enabledForEditor")[
                    contribAttribute(it, "editorId", editorId)
                  ]
                }
              }
            ]
          }
        }
      ]
    )

		return extensionList
	}

	def constraintClassName(Pattern pattern, Annotation annotation) {
		String::format("%s.%s%s%s", pattern.packageName, pattern.realPatternName.toFirstUpper, annotationLiteral,pattern.annotations.indexOf(annotation))
	}

	def constraintClassPath(Pattern pattern, Annotation annotation) {
		String::format("%s/%s%s%s", pattern.packagePath, pattern.realPatternName.toFirstUpper, annotationLiteral,pattern.annotations.indexOf(annotation))
	}

	def constraintClassJavaFile(Pattern pattern, Annotation annotation) {
		pattern.constraintClassPath(annotation) + ".java"
	}

	def constraintContributionId(Pattern pattern) {
		return VALIDATIONEXTENSION_PREFIX+CorePatternLanguageHelper::getFullyQualifiedName(pattern)
	}

	def menuContributionId(String editorId) {
		return String::format("%s%s", UI_VALIDATION_MENUS_PREFIX, editorId)
	}

	def getElementOfConstraintAnnotation(Annotation annotation, String elementName) {
    	val ap = CorePatternLanguageHelper::getFirstAnnotationParameter(annotation, elementName)
    	return switch(ap) {
    		StringValue case true: ap.value
    		VariableValue case true: ap.value.^var
    		default: null
    	}
  	}

	def getAnnotationParameterValue(Annotation annotation, String elementName) {
	  val values = newArrayList()
    for (ap : annotation.parameters) {
      if (ap.name.matches(elementName)) {
        values.add(ap.value)
      }
    }
    return values
	}

	override getAdditionalBinIncludes() {
		return newArrayList(new Path("plugin.xml"))
	}

    def patternHandler(Pattern pattern, Annotation annotation){
        val className = pattern.name.toFirstUpper + annotationLiteral + pattern.annotations.indexOf(annotation)
        '''
        package «pattern.packageName»;

        import java.util.List;
        import java.util.Map;
        import java.util.Set;
        import com.google.common.collect.ImmutableList;
        import com.google.common.collect.ImmutableMap;
        import com.google.common.collect.ImmutableSet;
        
        import org.eclipse.viatra.addon.validation.core.api.Severity;
        import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
        import org.eclipse.viatra.query.runtime.api.IPatternMatch;
        import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
        import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
        import org.eclipse.viatra.query.runtime.exception.IncQueryException;
        
        import «pattern.utilPackageName + "." + pattern.querySpecificationClassName»;
        
        public class «className» implements IConstraintSpecification {
        
            private «pattern.querySpecificationClassName» querySpecification;
        
            public «className»() throws IncQueryException {
                querySpecification = «pattern.querySpecificationClassName».instance();
            }
        
            @Override
            public String getMessageFormat() {
                return "«Strings::convertToJavaString(getElementOfConstraintAnnotation(annotation, "message"))»";
            }
        
        
            @Override
            public Map<String,Object> getKeyObjects(IPatternMatch signature) {
                Map<String,Object> map = ImmutableMap.of(
                    «FOR key : pattern.getKeyList(annotation) SEPARATOR ","»
                        "«key»",signature.get("«key»")
                    «ENDFOR»
                );
                return map;
            }
        
            @Override
            public List<String> getKeyNames() {
                List<String> keyNames = ImmutableList.of(
                    «FOR key : pattern.getKeyList(annotation) SEPARATOR ","»
                        "«key»"
                    «ENDFOR»
                );
                return keyNames;
            }
        
            @Override
            public List<String> getPropertyNames() {
                List<String> propertyNames = ImmutableList.of(
                    «FOR property : pattern.getPropertyList(annotation) SEPARATOR ","»
                        "«property»"
                    «ENDFOR»
                );
                return propertyNames;
            }
        
            @Override
            public Set<List<String>> getSymmetricPropertyNames() {
                Set<List<String>> symmetricPropertyNamesSet = ImmutableSet.<List<String>>of(
                    «val symmetricProperties = pattern.getSymmetricList(annotation).filter[
                        !pattern.getKeyList(annotation).containsAll(it)
                    ]»
                    «FOR propertyList : symmetricProperties SEPARATOR ","»
                        ImmutableList.of(
                        «FOR property : propertyList SEPARATOR ","»
                            "«property»"
                        «ENDFOR»
                        )
                    «ENDFOR»
                );
                return symmetricPropertyNamesSet;
            }
        
            @Override
            public Set<List<String>> getSymmetricKeyNames() {
                Set<List<String>> symmetricKeyNamesSet = ImmutableSet.<List<String>>of(
                    «val symmetricKeys = pattern.getSymmetricList(annotation).filter[
                        pattern.getKeyList(annotation).containsAll(it)
                    ]»
                    «FOR symmetricKeyList : symmetricKeys SEPARATOR ","»
                        ImmutableList.of(
                        «FOR key : symmetricKeyList SEPARATOR ","»
                            "«key»"
                        «ENDFOR»
                        )
                    «ENDFOR»
                );
                return symmetricKeyNamesSet;
            }
        
            @Override
            public Severity getSeverity() {
                return Severity.«getElementOfConstraintAnnotation(annotation, "severity").toUpperCase»;
            }
        
            @Override
            public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
                return querySpecification;
            }
        
        }
        '''
    }
    
    def getKeyList(Pattern pattern, Annotation annotation) {
        val locationParam = annotation.getFirstAnnotationParameter("location")
        if(locationParam == null){
            val keyParamValues = (annotation.getFirstAnnotationParameter("key") as ListValue).values
            ImmutableList.builder.addAll(keyParamValues.map[(it as StringValue).value]).build
        } else {
            #[(locationParam as VariableValue).value.variable.name]
        }
    }
    
    def getPropertyList(Pattern pattern, Annotation annotation) {
        val parameters = pattern.parameters.map[name]
        val locationParam = annotation.getFirstAnnotationParameter("location")
        if(locationParam == null){
            val keyParamValues = (annotation.getFirstAnnotationParameter("key") as ListValue).values
            val keys = keyParamValues.map[(it as StringValue).value]
            ImmutableList.copyOf(parameters.filter[!keys.contains(it)])
        } else {
            val location = (locationParam as VariableValue).value.variable.name
            ImmutableList.copyOf(parameters.filter[it != location])
        }
    }
    
    def getSymmetricList(Pattern pattern, Annotation annotation) {
        val symmetricParams = annotation.getAnnotationParameters("symmetric")
        val symmetryLists = symmetricParams.map[
            ImmutableList.copyOf((it as ListValue).values.map[
                (it as StringValue).value
            ])
        ]
        ImmutableSet.copyOf(symmetryLists)
    }
}
