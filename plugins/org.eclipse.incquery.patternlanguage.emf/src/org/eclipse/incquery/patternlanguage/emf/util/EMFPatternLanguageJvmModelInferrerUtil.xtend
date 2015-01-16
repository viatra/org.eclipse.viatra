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

package org.eclipse.incquery.patternlanguage.emf.util

import com.google.common.base.Splitter
import com.google.inject.Inject
import java.util.regex.Matcher
import org.apache.log4j.Logger
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.patternlanguage.emf.types.EMFPatternTypeProvider
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.xtend2.lib.StringConcatenation
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

/**
 * Utility class for the EMFPatternLanguageJvmModelInferrer.
 *
 * @author Mark Czotter
 */
class EMFPatternLanguageJvmModelInferrerUtil {

	@Inject extension TypeReferences
	Logger logger = Logger::getLogger(getClass())
	@Inject IEMFTypeProvider emfTypeProvider
	@Inject TypeReferenceSerializer typeReferenceSerializer
	@Inject var IJvmModelAssociations associations

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
		  simpleName.split("\\.").tail.forall[ch |Character.isJavaIdentifierPart(ch.charAt(0))]
	}

	def modelFileName(EObject object) {
		val name = object.eResource?.URI.trimFileExtension.lastSegment
		if (!(name.validClassName)) {
			throw new IllegalAccessError("The file name " + name + " is not a valid Java type name. Please, rename the file!")
		}
		name
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
	 * See the XBaseUsageCrossReferencer class, possible solution for local variable usage
	 * TODO: improve type calculation
	 * @return JvmTypeReference pointing the EClass that defines the Variable's type.
	 * @see ITypeProvider
	 * @see EMFPatternTypeProvider
	 */
   	def JvmTypeReference calculateType(Variable variable) {
   		emfTypeProvider.getVariableType(variable)
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
}
