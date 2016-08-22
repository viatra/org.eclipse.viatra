/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.util

import com.google.common.base.Preconditions
import com.google.common.base.Splitter
import com.google.inject.Inject
import java.util.regex.Matcher
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.query.patternlanguage.emf.services.EMFPatternLanguageGrammarAccess
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFPatternTypeProvider
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtend2.lib.StringConcatenationClient.TargetStringConcatenation
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 *
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrerUtil {

	@Inject extension TypeReferences
	@Inject Logger logger
	@Inject ITypeInferrer typeInferrer
	@Inject var IJvmModelAssociations associations
    @Inject EMFPatternLanguageGrammarAccess grammar
    @Inject IErrorFeedback feedback
	/**
	 * This method returns the pattern name.
	 * If the pattern name contains the package (any dot),
	 * then removes all segment except the last one.
	 */
	def realPatternName(Pattern pattern) {
		var name = pattern.name
		if (name.contains(".")) {
			return name.substring(name.lastIndexOf(".")+1)
		}
		return name
	}

	def validClassName(String simpleName) {
		Character.isJavaIdentifierStart(simpleName.charAt(0)) && 
		  simpleName.toCharArray.forall[Character.isJavaIdentifierPart(it)]
	}

	def modelFileName(EObject object) {
        val eResource = object.eResource
        if (eResource != null) {
            val name = eResource.URI.trimFileExtension.lastSegment
            if (!(name.validClassName)) {
                feedback.reportErrorNoLocation(object,
                    String.format("The file name %s is not a valid Java type name. Please, rename the file!", name),
                    EMFIssueCodes::OTHER_ISSUE, Severity.ERROR, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
            }
            return name
        } else {
            return ""
        }
    }
    
	/**
	 * Returns the QuerySpecificationClass name based on the Pattern's name
	 */
	def querySpecificationClassName(Pattern pattern) {
		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
		name.toFirstUpper+"QuerySpecification"
	}

	/**
	 * Returns the IQuerySpecificationProvider class name based on the Pattern's name
	 */
	def querySpecificationProviderClassName(Pattern pattern) {
		"Provider"
	}
	/**
	 * Returns the holder class name based on the Pattern's name
	 */
	def querySpecificationHolderClassName(Pattern pattern) {
		"LazyHolder"
	}
	/**
	 * Returns the PQuery class name based on the Pattern's name
	 */
	def querySpecificationPQueryClassName(Pattern pattern) {
		"GeneratedPQuery"
	}

	/**
	 * Returns the MatcherClass name based on the Pattern's name
	 */
   	def matcherClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Matcher"
   	}

	/**
	 * Returns the MatchClass name based on the Pattern's name
	 */
   	def matchClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Match"
   	}

   	def matchImmutableInnerClassName(Pattern pattern) {
   		"Immutable"
   	}
   	def matchMutableInnerClassName(Pattern pattern) {
   		"Mutable"
   	}

	/**
	 * Returns the ProcessorClass name based on the Pattern's name
	 */
   	def processorClassName(Pattern pattern) {
   		var name = pattern.name
		if (name.contains(".")) {
			name = pattern.realPatternName
		}
   		name.toFirstUpper+"Processor"
   	}

   	/**
   	 * Returns field name for Variable
   	 */
   	def fieldName(Variable variable) {
   		"f"+variable?.name.toFirstUpper
   	}

   	/**
   	 * Returns parameter name for Variable
   	 */
   	def parameterName(Variable variable) {
   		"p"+variable?.name?.toFirstUpper
   	}

   	def positionConstant(Variable variable) {
   		"POSITION_"+variable?.name?.toUpperCase;
   	}

   	/**
   	 * Returns correct getter method name for variable.
   	 * For variable with name 'class' returns getValueOfClass, otherwise returns <code>get#variable.name.toFirstUpper#</code>.
   	 */
   	def getterMethodName(Variable variable) {
   		if (variable.name == "class") {
   			return "getValueOfClass"
   		} else {
   			return "get" + variable?.name?.toFirstUpper
   		}
   	}

   	/**
   	 * Returns correct setter method name for variable.
   	 * Currently returns <code>set#variable.name.toFirstUpper#</code>.
   	 */
   	def setterMethodName(Variable variable) {
   		"set" + variable?.name?.toFirstUpper
   	}

	/**
	 * Calls the typeProvider.
	 * @return JvmTypeReference pointing the EClass that defines the Variable's type.
	 * @see ITypeInferrer
	 */
   	def JvmTypeReference calculateType(Variable variable) {
          typeInferrer.getJvmType(variable, variable)
   	}

   	/**
   	 * Serializes the EObject into Java String variable.
   	 */
   	def serializeToJava(EObject eObject) {
		val parseString = eObject.serialize
		if (parseString.nullOrEmpty) {
			return "";
		}
		val splits = parseString.split("[\r\n]+")
		val stringRep = '''String patternString = ""''' as StringConcatenation
	  	stringRep.newLine
	  	for (s : splits) {
	  		// Extra space needed before and after every line,
	  		// otherwise parser parses the entire string (or part of it) as package name).
	  		stringRep.append("+\" " + s + " \"")
	  		stringRep.newLine
		}
	  	stringRep.append(";")
	  	return stringRep
  	}

  	/**
  	 * Serializes the input for Javadoc
  	 */
  	def serializeToJavadoc(Pattern pattern) {
  		var javadocString = pattern.serialize
  		if (javadocString.nullOrEmpty) {
  			return "Serialization error, check Log"
  		}
  		javadocString = javadocString.replaceAll(java.util.regex.Pattern::quote("\\\""),Matcher::quoteReplacement("\""))
  		javadocString = javadocString.replaceAll("@","{@literal @}")
  		javadocString = javadocString.replaceAll("<","{@literal <}")
  		javadocString = javadocString.replaceAll(">","{@literal >}")
  		return javadocString.trim
  	}
  	
  	/**
     * Returns the file header comment at the beginning of the text corresponding
     * to the pattern model.
     * The comment text is escaped, so it does not include stars in multi-line comments.
     * 
     * @since 1.3
     */
  	def getFileComment(PatternModel patternModel) {
  	    val patternNode = NodeModelUtils.getNode(patternModel)
  	    val possibleFileComment = patternNode?.firstChild?.nextSibling
  	    if (possibleFileComment != null) {
            val grammarElement = possibleFileComment.grammarElement
            if (grammarElement == grammar.getML_COMMENTRule) {
                val multiLineCommentText = possibleFileComment.text.escape
                return multiLineCommentText
            } else if (grammarElement == grammar.SL_COMMENTRule) {
                val singleLineCommentText = possibleFileComment.text.escape
                return singleLineCommentText
            }
        }
  	    return '''Generated from «patternModel.eResource?.URI»'''
  	}
  	
  	/**
  	 * Returns the file header comment at the beginning of the text corresponding 
  	 * to the pattern model containing the given pattern.
     * The comment text is escaped, so it does not include stars in multi-line comments.
     * 
  	 * @since 1.3
  	 */
  	def getFileComment(Pattern pattern) {
  	    val patternModel = EcoreUtil2.getContainerOfType(pattern, PatternModel)
  	    return patternModel.fileComment
  	}
  	
  	/**
  	 * Escapes the input to be usable in literal strings
  	 */
  	def escapeToQuotedString(String inputString) {
  		var String string = inputString
  		if (string.nullOrEmpty) {
  			return ""
  		}
  		string = string.replace("\\", "\\\\")
  		string = string.replace("\n", "\\n")
  		string = string.replace("\t", "\\t")
  		string = string.replace('"', "\\\"")
  		return string.trim
  	}

  	/**
  	 * Serializes EObject to a String representation. Escapes only the double qoutes.
  	 */
  	def private serialize(EObject eObject) {
  		try {
  			// This call sometimes causes ConcurrentModificationException
//			val serializedObject = serializer.serialize(eObject)
			// Another way to serialize the eObject, uses the current node model
			// simple getText returns the currently text, that parsed by the editor
			val eObjectNode = NodeModelUtils::getNode(eObject)
			if (eObjectNode != null) {
				return escape(eObjectNode.text)
			}
			// getTokenText returns the string without hidden tokens
//			NodeModelUtils::getTokenText(NodeModelUtils::getNode(eObject)).replaceAll("\"", "\\\\\"")
		} catch (Exception e) {
			if (logger != null) {
				logger.error("Error when serializing " + eObject.eClass.name, e)
			}
		}
		return null
  	}

  	def private escape(String escapable) {
  		if (escapable == null) return null
  		// escape double quotes
  		var escapedString = escapable.replaceAll("\"", "\\\\\"")
		escapedString = escapedString.replaceAll("\\*+/", "")
			.replaceAll("/*\\*", "");
  		return escapedString
  	}

  	/**
  	 * Returns the packageName: PatternModel.packageName or "" when nullOrEmpty.
  	 */
  	def getPackageName(Pattern pattern) {
  		var packageName = (pattern.eContainer as PatternModel).packageName
	   	if (packageName.nullOrEmpty) {
	   		packageName = ""
	   	}
	   	return packageName.toLowerCase
  	}

  	def getUtilPackageName(Pattern pattern) {
  		return getPackageName(pattern)+".util"
  	}

	/**
  	 * Returns the packageName: PatternModel.packageName + Pattern.name, packageName is ignored, when nullOrEmpty.
  	 */
  	def getPackageNameOld(Pattern pattern) {
  		var packageName = (pattern.eContainer as PatternModel).packageName
	   	if (packageName.nullOrEmpty) {
	   		packageName = ""
	   	} else {
	   		packageName = packageName + "."
	   	}
	   	return (packageName + pattern.name).toLowerCase
  	}


	def getPackagePath(Pattern pattern) {
		pattern.packageName.replace(".","/")
	}

	/**
	 * Calculates the correct package path for a selected fqn
	 */
	def getPackagePath(String fqn) {
		val split = Splitter.on(".").split(fqn)

		split.take(split.size - 1).join("/")
	}

	/**
	 * This method returns the pattern name.
	 * If the pattern name contains the package (any dot),
	 * then removes all segment except the last one.
	 */
	def realPatternName(String fqn) {
		Splitter.on(".").split(fqn).last
	}
	
	def findInferredSpecification(Pattern pattern) {
		pattern.findInferredClass(typeof (BaseGeneratedEMFQuerySpecification))
	}
	
	def findInferredClass(EObject pattern, Class<?> clazz) {
		associations.getJvmElements(pattern).filter(typeof(JvmType)).findFirst[
			isCompatibleWith(clazz) 
	
		]
	}
	
	def boolean isCompatibleWith(JvmType type, Class<?> clazz) {
		type.is(clazz) || (
			type instanceof JvmDeclaredType && (type as JvmDeclaredType).superTypes.exists[it.is(clazz)]
		)
	}
	
	def isPublic(Pattern pattern) {
		!CorePatternLanguageHelper::isPrivate(pattern)
	}

    def variables(XExpression ex) {
        val body = EcoreUtil2.getContainerOfType(ex, PatternBody)
        val valNames = (ex.eAllContents + newImmutableList(ex).iterator).
                filter(typeof(XFeatureCall)).map[concreteSyntaxFeatureName].
                toList
        body.variables.filter[valNames.contains(it.name)].sortBy[name]
    }
    
    def expressionMethodName(XExpression ex) {
        "evaluateExpression_" + getExpressionPostfix(ex)
    }

    private static def getExpressionPostfix(XExpression xExpression) {
        val pattern = EcoreUtil2.getContainerOfType(xExpression, typeof(Pattern))
        Preconditions.checkArgument(pattern != null, "Expression is not inside a pattern")
        var bodyNo = 0
        for (patternBody : pattern.getBodies()) {
            bodyNo = bodyNo + 1
            var exNo = 0
            for (xExpression2 : CorePatternLanguageHelper.getAllTopLevelXBaseExpressions(patternBody)) {
                    exNo = exNo + 1
                    if (xExpression.equals(xExpression2)) {
                        return bodyNo + "_" + exNo
                    }
            }
        }
        //Shall never be executed
        throw new RuntimeException("Expression not found in pattern")
    }
    
    /**
     * Output code is intended for generated query specification classes, 
     *  since it depends on 'getFeatureLiteral()' / 'getClassifierLiteral()'
     * 
     * <p> the "safe" classifier lookup is used if the result is used for initializing a PParameter
     */
    public def StringConcatenationClient serializeInputKey(IInputKey key, boolean forParameter) {
        return new StringConcatenationClient() {
            override protected appendTo(TargetStringConcatenation target) {
                target.appendInputKey(key, forParameter)
            }
        }
    }
    
    /**
     * Calculates the name of the variable that stores a PParameter for a pattern
     * @since 1.4
     */
    public def String getPParameterName(Variable parameter) '''parameter_«parameter.parameterName»'''
    
    /**
     * Output code is intended for generated query specification classes, 
     *  since it depends on 'getFeatureLiteral()' / 'getClassifierLiteral()'
     * 
     * <p> the "safe" classifier lookup is used if the result is used for initializing a PParameter
     */
    public def appendInputKey(TargetStringConcatenation target, IInputKey key, boolean forParameter) {
        switch key {
            EStructuralFeatureInstancesKey : {
                val literal = key.emfKey
                val container = literal.EContainingClass
                val packageNsUri = container.EPackage.nsURI
                target.append('''new ''')
                target.append(EStructuralFeatureInstancesKey)
                target.append('''(getFeatureLiteral("«packageNsUri»", "«container.name»", "«literal.name»"))''')
            }
            EClassTransitiveInstancesKey : {
                val literal = key.emfKey
                val packageNsUri = literal.EPackage.nsURI
                target.append('''new ''')
                target.append(EClassTransitiveInstancesKey)
                target.append('''((''')
                target.append(EClass)
                target.append(''')«classifierGetterName(forParameter)»("«packageNsUri»", "«literal.name»"))''')
            }
            EDataTypeInSlotsKey : {
                val literal = key.emfKey
                val packageNsUri = literal.EPackage.nsURI
                target.append('''new ''')
                target.append(EDataTypeInSlotsKey)
                target.append('''((''')
                target.append(EDataType)
                target.append(''')«classifierGetterName(forParameter)»("«packageNsUri»", "«literal.name»"))''')
            }
            JavaTransitiveInstancesKey : {
                val clazz = key.prettyPrintableName
                target.append('''new ''')
                target.append(JavaTransitiveInstancesKey)
                target.append('''(«clazz»)''')
            }
            case null : {
                target.append('''null''')
            }
        }            
    }
    private def classifierGetterName(boolean forParameter) {
        if (forParameter)
            '''getClassifierLiteralSafe'''
        else
            '''getClassifierLiteral'''            
    }
}
