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
package org.eclipse.viatra.tooling.cpp.ecore.generator.common

import com.google.common.base.Joiner
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemAccess
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.NamespaceHelper

import static extension org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.PathUtils.*

/**
 * @author Robert Doczi
 */
class PackageGenerator {
	static def void generatePackage(EPackage pack, FileSystemAccess fsa) {
		if (pack.eContents.exists[it instanceof EClass]) {
			fsa.generateFile(pack.name.definition, pack.compileDef)
			fsa.generateFile(pack.name.declaration, pack.compileDecl)
		}
	}

	private static def compileDef(EPackage pack) '''
		«val guard = CppHelper::getGuardHelper(
			Joiner.on('_').join(NamespaceHelper::getNamespaceHelper(pack)) + '_' + pack.name + "_DEF")»
		«guard.start»
		
		«FOR clazz : pack.eContents.filter(EClass)»
			#include "«NamespaceHelper::getNamespaceHelper(clazz).toString("/")»/«clazz.name».h"
		«ENDFOR»
		
		«guard.end»
	'''

	private static def compileDecl(EPackage pack) '''
		«val guard = CppHelper::getGuardHelper(
			Joiner.on('_').join(NamespaceHelper::getNamespaceHelper(pack)) + '_' + pack.name + "_DECL")»
		«guard.start»
		
		«val ns = NamespaceHelper::getNamespaceHelper(pack)»
		«FOR namespace : ns»
			namespace «namespace» {
		«ENDFOR»
		namespace «pack.name» {
		
		«FOR clazz : pack.eContents.filter(EClass)»
			class «clazz.name»;
		«ENDFOR»
		
		} /* namespace «pack.name» */
		«FOR namespace : ns»
			} /* namespace «namespace» */
		«ENDFOR»
		
		«guard.end»
	'''

}
