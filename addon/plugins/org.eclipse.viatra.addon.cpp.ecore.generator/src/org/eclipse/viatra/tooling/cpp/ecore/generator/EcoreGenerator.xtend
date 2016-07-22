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
package org.eclipse.viatra.tooling.cpp.ecore.generator

import java.nio.file.Path
import java.util.concurrent.TimeUnit
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemAccess
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemTaskHandler
import org.eclipse.viatra.tooling.cpp.ecore.generator.common.PackageGenerator
import org.eclipse.viatra.tooling.cpp.ecore.generator.ecore.EClassGenerator

import static org.eclipse.viatra.tooling.cpp.ecore.generator.ecore.EClassGenerator.*

/**
 * @author Robert Doczi
 */
class EcoreGenerator {

	MakefileGenerator mkgen

	val FileSystemTaskHandler handler
	val FileSystemAccess fsa
	val Resource ecoreModel


	new(Resource ecoreModel, Path root) {
		mkgen = new MakefileGenerator(ecoreModel)
		this.ecoreModel = ecoreModel
		handler = new FileSystemTaskHandler
		fsa = new FileSystemAccess(root, handler)
	}

	def startGeneration() {
		val fullName = ecoreModel.getURI.trimFileExtension.lastSegment
		fsa.deleteFile(fullName)
		// TODO: hack inc
		EClassGenerator::id = 0;
		ecoreModel.contents.forEach[generate(fsa.createInSubfolder(fullName))]

		mkgen.generate(fsa)

		handler.flush(3, TimeUnit.SECONDS)
	}

	def dispatch void generate(EPackage pack, FileSystemAccess fsa) {
		PackageGenerator::generatePackage(pack, fsa)
		pack.eContents.forEach[generate(fsa.createInSubfolder(pack.name))]
	}

	def dispatch void generate(EClass clazz, FileSystemAccess fsa) {
		EClassGenerator::generateClass(clazz, fsa)
	}

	def dispatch void generate(EObject obj, FileSystemAccess fsa) {
	}
}
