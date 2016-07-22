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
package org.eclipse.viatra.tooling.cpp.ecore.generator.ecore

import com.google.common.base.Joiner
import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemAccess
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.NamespaceHelper

import static extension org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.PathUtils.*

/**
 * @author Robert Doczi
 */
class EClassGenerator {
	
	public static int id = 0;
	
	static def generateClass(EClass clazz, FileSystemAccess fsa) {
		fsa.generateFile(clazz.name.h, clazz.compileHeader)
		fsa.generateFile(clazz.name.cpp, clazz.compileSource)
	}

	static def compileHeader(EClass clazz) '''
		«val guard = CppHelper::getGuardHelper(Joiner.on('_').join(NamespaceHelper::getNamespaceHelper(clazz)) + '_' + clazz.name)»
		«guard.start»
		
		«val ns = NamespaceHelper::getNamespaceHelper(clazz)»
		#include "«ns.toString("/")»_decl.h"
		
		«FOR parent : clazz.getEGenericSuperTypes.map[getEClassifier]»
			#include "«NamespaceHelper::getNamespaceHelper(parent).toString("/")»/«parent.name».h"
		«ENDFOR»
		
		#include <string>
		#include <list>
		#include <vector>
		
		«FOR namespaceFragment : ns»
			namespace «namespaceFragment» {
		«ENDFOR»	
		
		«val List<EReference> assoc = clazz.getEReferences.toList»
		
		class «clazz.name» «FOR parent : clazz.getEGenericSuperTypes.map[getEClassifier] BEFORE ": " SEPARATOR ", "»public «parent.name»«ENDFOR» {
		public:
			«clazz.name»();
			virtual ~«clazz.name»();
			static std::list<«clazz.name»*> _instances;
			static const unsigned short type_id = «id++»;
			
			virtual unsigned short get_type_id() const {
				return type_id;
			}
		
			«FOR a : clazz.getEAttributes»
				«val ah = CppHelper::getAttributeHelper(a)»
				«ah.declaration»
			«ENDFOR»
			
			«FOR a : assoc»
				«val ah = CppHelper::getAssociationHelper(a)»
				«ah.declaration»
			«ENDFOR»
		};
		
		«FOR namespaceFragment : ns»
			} /* namespace «namespaceFragment» */
		«ENDFOR»
		
		«guard.end»
	'''

	static def compileSource(EClass clazz) '''
		#include "«clazz.name».h"
		
		«val ns = NamespaceHelper::getNamespaceHelper(clazz)»
		«val assoc = clazz.getEReferences»
		«FOR includedClass : assoc.map[getEReferenceType].toSet»
			#include "«NamespaceHelper.getNamespaceHelper(includedClass).toString("/")»/«includedClass.name».h"
		«ENDFOR»
		
		#include <algorithm>
		
		«FOR namespaceFragment : ns»
			namespace «namespaceFragment» {
		«ENDFOR»	
		
		std::list<«clazz.name»*> «clazz.name»::_instances;
		
		«clazz.name»::«clazz.name»()«IF !assoc.empty»
				«FOR a : assoc.filter[it.upperBound == 1] BEFORE ': ' SEPARATOR ','»«a.name»(NULL)«ENDFOR»«ENDIF» {
			_instances.push_back(this);
		}
		
		«clazz.name»::~«clazz.name»() {
			_instances.remove(this);
		}
		
		«FOR namespaceFragment : ns»
			} /* namespace «namespaceFragment» */
		«ENDFOR»
	'''
}
