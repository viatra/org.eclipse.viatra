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

package org.eclipse.incquery.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import java.util.Arrays
import java.util.List
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator

/**
 * {@link IPatternMatch} implementation inferer.
 *
 * @author Mark Czotter
 */
class PatternMatchClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension IQualifiedNameProvider
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject TypeReferences typeReference
	@Inject extension IJvmModelAssociator associator
	@Inject extension JavadocInferrer
	
   	/**
   	 * Infers fields for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassFields(JvmDeclaredType matchClass, Pattern pattern) {
   		for (Variable variable : pattern.parameters) {
   			matchClass.members += variable.toField(variable.fieldName, variable.calculateType)
   		}
		matchClass.members += matchClass.toField("parameterNames", pattern.newTypeRef(typeof (List), pattern.newRawTypeRef(typeof (String)))) [
 			static = true
   			initializer = [append('''makeImmutableList(«FOR variable : pattern.parameters SEPARATOR ', '»"«variable.name»"«ENDFOR»)''')]
   		]
   	}

   	/**
   	 * Infers constructors for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassConstructors(JvmDeclaredType matchClass, Pattern pattern) {
   		matchClass.members += matchClass.toConstructor() [
   			simpleName = pattern.matchClassName
   			visibility = JvmVisibility::PRIVATE //DEFAULT
   			for (Variable variable : pattern.parameters) {
   				val javaType = variable.calculateType
   				parameters += variable.toParameter(variable.parameterName, javaType)
   			}
   			body = [append('''
   				«FOR variable : pattern.parameters»
   				this.«variable.fieldName» = «variable.parameterName»;
   				«ENDFOR»
   			''')]
   		]
   	}

   	/**
   	 * Infers getters for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassGetters(JvmDeclaredType matchClass, Pattern pattern) {
		matchClass.members += matchClass.toMethod("get", pattern.newTypeRef(typeof (Object))) [
   			annotations += matchClass.toAnnotation(typeof (Override))
   			parameters += matchClass.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   			body = [append('''
   				«FOR variable : pattern.parameters»
   				if ("«variable.name»".equals(parameterName)) return this.«variable.fieldName»;
   				«ENDFOR»
   				return null;
   			''')]
   		]
   		for (Variable variable : pattern.parameters) {
			 val getter = variable.toMethod(variable.getterMethodName, variable.calculateType) [
	   			body = [append('''
	   				return this.«variable.fieldName»;
	   			''')]
	   		]
	   		matchClass.members += getter
	   		associator.associatePrimary(variable, getter)
   		}
   	}

   	/**
   	 * Infers setters for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassSetters(JvmDeclaredType matchClass, Pattern pattern) {
   		matchClass.members += matchClass.toMethod("set", pattern.newTypeRef(typeof (boolean))) [
   			returnType = matchClass.newTypeRef(Boolean::TYPE)
   			annotations += matchClass.toAnnotation(typeof (Override))
   			parameters += matchClass.toParameter("parameterName", pattern.newTypeRef(typeof (String)))
   			parameters += matchClass.toParameter("newValue", pattern.newTypeRef(typeof (Object)))
   			body = [append('''
   				if (!isMutable()) throw new java.lang.UnsupportedOperationException();
   				«FOR variable : pattern.parameters»
   				«val type = variable.calculateType»
   				«val typeName = type.qualifiedName»
   				if ("«variable.name»".equals(parameterName) «IF typeReference.is(type, typeof(Object))»&& newValue instanceof «typeName»«ENDIF») {
   					this.«variable.fieldName» = («typeName») newValue;
   					return true;
   				}
   				«ENDFOR»
   				return false;
   			''')]
   		]
   		for (Variable variable : pattern.parameters) {
   			matchClass.members += matchClass.toMethod(variable.setterMethodName, null) [
   				returnType = matchClass.newTypeRef(Void::TYPE)
   				parameters += matchClass.toParameter(variable.parameterName, variable.calculateType)
   				body = [append('''
   					if (!isMutable()) throw new java.lang.UnsupportedOperationException();
   					this.«variable.fieldName» = «variable.parameterName»;
   				''')]
   			]
   		}
   	}

	/**
   	 * Infers methods for Match class based on the input 'pattern'.
   	 */
   	def inferMatchClassMethods(JvmDeclaredType matchClass, Pattern pattern, JvmParameterizedTypeReference querySpecificationClassRef) {
   		matchClass.members += matchClass.toMethod("patternName", pattern.newTypeRef(typeof(String))) [
   			annotations += matchClass.toAnnotation(typeof (Override))
   			body = [append('''
   				return "«pattern.fullyQualifiedName»";
   			''')]
   		]
		// add extra methods like equals, hashcode, toArray, parameterNames
		matchClass.members += matchClass.toMethod("parameterNames", pattern.newTypeRef(typeof (List), pattern.newRawTypeRef(typeof (String)))) [
   			annotations += matchClass.toAnnotation(typeof (Override))
   			body = [append('''
   				return «pattern.matchClassName».parameterNames;
   			''')]
   		]
   		matchClass.members += matchClass.toMethod("toArray", pattern.newTypeRef(typeof (Object)).addArrayTypeDimension) [
   			annotations += matchClass.toAnnotation(typeof (Override))
   			body = [append('''
   				return new Object[]{«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR»};
   			''')]
   		]
   		matchClass.members += matchClass.toMethod("toImmutable", typeReference.createTypeRef(matchClass)) [
   			annotations += matchClass.toAnnotation(typeof (Override))
   			body = [append('''
   				return isMutable() ? newMatch(«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR») : this;
   			''')]
   		]
		matchClass.members += matchClass.toMethod("prettyPrint", pattern.newTypeRef(typeof (String))) [
			annotations += matchClass.toAnnotation(typeof (Override))
			setBody = [
				if (pattern.parameters.empty)
					append('''return "[]";''')
				else
					append('''
				StringBuilder result = new StringBuilder();
				«FOR variable : pattern.parameters SEPARATOR " + \", \");\n" AFTER ");\n"»
					result.append("\"«variable.name»\"=" + prettyPrintValue(«variable.fieldName»)«ENDFOR»
				return result.toString();
			''')]
		]
		matchClass.members += matchClass.toMethod("hashCode", pattern.newTypeRef(typeof (int))) [
			annotations += matchClass.toAnnotation(typeof (Override))
			body = [append('''
				final int prime = 31;
				int result = 1;
				«FOR variable : pattern.parameters»
				result = prime * result + ((«variable.fieldName» == null) ? 0 : «variable.fieldName».hashCode());
				«ENDFOR»
				return result;
			''')]
		]
		matchClass.members += matchClass.toMethod("equals", pattern.newTypeRef(typeof (boolean))) [
			annotations += matchClass.toAnnotation(typeof (Override))
			parameters += matchClass.toParameter("obj", pattern.newTypeRef(typeof (Object)))
			body = [append('''
				if (this == obj)
					return true;
				if (!(obj instanceof «pattern.matchClassName»)) { // this should be infrequent
					if (obj == null)
						return false;
					if (!(obj instanceof ''')
				referClass(pattern, typeof(IPatternMatch))
				append('''
				))
						return false;
				''')
				append("	")
				referClass(pattern, typeof(IPatternMatch))
				append(" ") append('''
				otherSig  = (''')
				referClass(pattern, typeof(IPatternMatch))
				append('''
				) obj;
					if (!specification().equals(otherSig.specification()))
						return false;
					return ''')
				referClass(pattern, typeof(Arrays))
				append('''
				.deepEquals(toArray(), otherSig.toArray());
				}
				«IF !pattern.parameters.isEmpty»
				«pattern.matchClassName» other = («pattern.matchClassName») obj;
				«FOR variable : pattern.parameters»
				if («variable.fieldName» == null) {if (other.«variable.fieldName» != null) return false;}
				else if (!«variable.fieldName».equals(other.«variable.fieldName»)) return false;
				«ENDFOR»
				«ENDIF»
				return true;''')]
		]
		matchClass.members += matchClass.toMethod("specification", querySpecificationClassRef) [
			annotations += matchClass.toAnnotation(typeof (Override))
			body = [
				append('''
				try {
					return «querySpecificationClassRef.type.simpleName».instance();
				} catch (''')
				referClass(pattern, typeof (IncQueryException))
				append(" ")
				append('''
				ex) {
				 	// This cannot happen, as the match object can only be instantiated if the query specification exists
				 	throw new ''')
				referClass(pattern, typeof (IllegalStateException))
				append('''
					(ex);
				}
			''')]
		]
		matchClass.members += matchClass.toMethod("newEmptyMatch", typeReference.createTypeRef(matchClass)) [
   			static = true
   			documentation = pattern.javadocNewEmptyMatchMethod.toString
   			body = [append('''
   				return new Mutable(«FOR p : pattern.parameters SEPARATOR ", "»null«ENDFOR»);
   			''')]
   		]
		matchClass.members += matchClass.toMethod("newMutableMatch", typeReference.createTypeRef(matchClass)) [
   			static = true
   			parameters += pattern.parameters.map[matchClass.toParameter(parameterName, calculateType)]
   			documentation = pattern.javadocNewMutableMatchMethod.toString
   			body = [append('''
   				return new Mutable(«FOR p : pattern.parameters SEPARATOR ", "»«p.parameterName»«ENDFOR»);
   			''')]
   		]
		matchClass.members += matchClass.toMethod("newMatch", typeReference.createTypeRef(matchClass)) [
   			static = true
   			parameters += pattern.parameters.map[matchClass.toParameter(parameterName, calculateType)]
   			documentation = pattern.javadocNewMatchMethod.toString
   			body = [append('''
   				return new Immutable(«FOR p : pattern.parameters SEPARATOR ", "»«p.parameterName»«ENDFOR»);
   			''')]
   		]
  	}

 	/**
   	 * Infers inner classes for Match class based on the input 'pattern'.
   	 */
  	def inferMatchInnerClasses(JvmDeclaredType matchClass, Pattern pattern) {
  		matchClass.members += matchClass.makeMatchInnerClass(pattern, pattern.matchMutableInnerClassName, true);
  		matchClass.members += matchClass.makeMatchInnerClass(pattern, pattern.matchImmutableInnerClassName, false);
	}

 	/**
   	 * Infers a single inner class for Match class
   	 */
	def makeMatchInnerClass(JvmDeclaredType matchClass, Pattern pattern, String innerClassName, boolean isMutable) {
		matchClass.toClass(innerClassName) [
			visibility = JvmVisibility::PRIVATE
			static = true
			final = true
			superTypes += typeReference.createTypeRef(matchClass)

			members+= matchClass.toConstructor() [
	   			simpleName = innerClassName
	   			visibility = JvmVisibility::DEFAULT
	   			for (Variable variable : pattern.parameters) {
	   				val javaType = variable.calculateType
	   				parameters += variable.toParameter(variable.parameterName, javaType)
	   			}
	   			body = [append('''
	   				super(«FOR variable : pattern.parameters SEPARATOR ", "»«variable.parameterName»«ENDFOR»);
	   			''')]
			]
			members += matchClass.toMethod("isMutable", pattern.newTypeRef(typeof (boolean))) [
				visibility = JvmVisibility::PUBLIC
				annotations += pattern.toAnnotation(typeof (Override))
				body = [append('''return «isMutable»;''')]
			]
		]
	}



}