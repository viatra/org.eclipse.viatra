/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators

import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import java.util.concurrent.TimeUnit
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EReference

import static extension org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.PathUtils.*

/**
 * @author Robert Doczi
 */
class CppHelper {

	static val LoadingCache<String, GuardHelper> guardCache = CacheBuilder.newBuilder.maximumSize(1000).
		expireAfterWrite(1, TimeUnit.MINUTES).build(
			[
				new GuardHelper(it)
			])

	static val LoadingCache<EAttribute, AttributeHelper> attrCache = CacheBuilder.newBuilder.maximumSize(1000).
		expireAfterWrite(1, TimeUnit.MINUTES).build(
			[
				new AttributeHelper(it)
			])

	static val LoadingCache<EReference, AssociationHelper> assocCache = CacheBuilder.newBuilder.maximumSize(1000).
		expireAfterWrite(1, TimeUnit.MINUTES).build(
			[
				new AssociationHelper(it)
			])
			
	static val LoadingCache<EClass, IncludeHelper> includeCache = CacheBuilder.newBuilder.maximumSize(1000).
		expireAfterWrite(1, TimeUnit.MINUTES).build(
			[
				new IncludeHelper(it)
			])
			
	static val LoadingCache<EClassifier, TypeHelper> typeCache = CacheBuilder.newBuilder.maximumSize(1000).
		expireAfterWrite(1, TimeUnit.MINUTES).build(
			[
				switch it {
					EClass: new ClassHelper(it)
					EDataType: new PrimitiveTypeHelper(it)
				}
			])

	def static getGuardHelper(String name) {
		guardCache.get(name)
	}

	def static getAttributeHelper(EAttribute attr) {
		attrCache.get(attr)
	}

	def static getAssociationHelper(EReference assoc) {
		assocCache.get(assoc)
	}
	
	def static getIncludeHelper(EClass eClass) {
		includeCache.get(eClass)
	}
	
	def static getTypeHelper(EClassifier classifier) {
		typeCache.get(classifier)
	}
}

class GuardHelper {
	val String guard;

	new(String name) {
		this.guard = name.toUpperCase + "_H_"
	}

	def start() '''
		#ifndef «guard»
		#define «guard»
	'''

	def end() '''
		#endif /*  «guard» */
	'''
}

class AttributeHelper {
	val EAttribute attr
	val boolean multiple
	
	val TypeHelper typeHelper
	
	new(EAttribute attr) {
		this.attr = attr
		multiple = attr.upperBound > 1 || attr.upperBound == -1
		
		typeHelper = CppHelper::getTypeHelper(attr.getEType)
	}

	def declaration() '''
		«IF multiple»std::vector< «typeHelper.getFQN»>«ELSE»«typeHelper.getFQN»«ENDIF» «attr.name» = «typeHelper.defaultValue»;
	'''

	def get() '''«attr.name»'''

	def get(int idx) '''«attr.name»[«idx»]'''

	def set(CharSequence value) '''«IF multiple»«attr.name».push_back(«value»)«ELSE»«attr.name»=«value»«ENDIF»;'''

	def set(CharSequence value, int idx) '''«attr.name»[«idx»]=«value»);'''
}

class AssociationHelper {
	val EReference association
	val boolean multiple

	val TypeHelper typeHelper

	new(EReference association) {
		this.association = association
		multiple = association.upperBound > 1 || association.upperBound == -1
		
		typeHelper = CppHelper::getTypeHelper(association.getEReferenceType)
	}

	def declaration() '''
		«IF multiple»std::vector< «typeHelper.getFQN»*>«ELSE»«typeHelper.getFQN»*«ENDIF» «association.name»«IF !multiple» = «typeHelper.defaultValue»«ENDIF»;
	'''

	def get() '''«association.name»'''

	def get(int idx) '''«association.name»[«idx»]'''

	def CharSequence set(String instanceName, String targetInstanceName, boolean recurse) '''
		«instanceName».«IF multiple»«association.name».push_back(&«targetInstanceName»)«ELSE»«association.name»=&«targetInstanceName»«ENDIF»;
		«IF association.getEOpposite != null && recurse»
			«val ah = CppHelper::getAssociationHelper(association.getEOpposite)»
			«ah.set(targetInstanceName, instanceName, false)»
		«ENDIF»
	'''

	def set(CharSequence value, int idx) '''«association.name»[«idx»]=«value»);'''
}

class IncludeHelper {
	
	val EClass namedElement
	val String stringRepresentation
	
	new(EClass namedElement) {
		this.namedElement = namedElement
		
		val ns = NamespaceHelper::getNamespaceHelper(namedElement)
		stringRepresentation = '''«ns.toString('/')»/«namedElement.name.h»'''
	}
	
	override toString() {
		stringRepresentation
	}
}

interface TypeHelper {
	
	def String getName()
	def String getFQN()
	def String getDefaultValue()
	
}

class ClassHelper implements TypeHelper {
	
	val EClass eClass
	
	val String name
	val String fqn
	
	new(EClass eClass) {
		this.eClass = eClass
		
		val ns = NamespaceHelper::getNamespaceHelper(eClass)
		this.name = eClass.name
		this.fqn = '''::«ns.toString»::«name»'''
	}
	
	override getName() {
		name
	}	
	
	override getFQN() {
		fqn
	}
	
	override getDefaultValue() {
		'''nullptr'''
	}
	
}

class PrimitiveTypeHelper implements TypeHelper {
	
	static val PRIMITIVE_MAP = #{
		"EString" -> "::std::string",
		"EInt" -> "int",
		"ELong" -> "long",
		"EDouble" -> "double",
		"EFloat" -> "float",
		"EShort" -> "short",
		"EChar" -> "char"
	}
	
	static val PRIMITIVE_DEFAULT_VALUES = #{
		"::std::string" -> "\"\"",
		"int" -> "0",
		"long" -> "0l",
		"double" -> "0.0",
		"float" -> "0.0f",
		"short" -> "0",
		"char" -> '\'\\0\''
	}
	
	val EDataType eDataType
	
	val String name
	val String defaultValue
	
	new(EDataType eDataType) {
		this.eDataType = eDataType;
		
		this.name = PRIMITIVE_MAP.get(eDataType.name)
		this.defaultValue = PRIMITIVE_DEFAULT_VALUES.get(name)
	}
	
	override getName() {
		name
	}	
	
	override getFQN() {
		name
	}
	
	override getDefaultValue() {
		defaultValue
	}
}
