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
package org.eclipse.viatra.tooling.cpp.ecore.generator.ui

import org.eclipse.viatra.tooling.cpp.ecore.generator.EcoreGenerator
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil

/**
 * @author Robert Doczi
 */
class GenerateCommand extends AbstractHandler {
	final static String GEN_ROOT = "./cpp-gen".replace(Character.valueOf('/').charValue, File::separatorChar)

	override Object execute(ExecutionEvent event) throws ExecutionException {
		var ISelection sel = HandlerUtil::getActiveMenuSelection(event)
		var IStructuredSelection selection = sel as IStructuredSelection

		var IResource parent = getRoot(selection.getFirstElement() as IFile)
		var Path rootPath = Paths::get(parent.getLocation().toOSString, GEN_ROOT).normalize

		var ResourceSet loader = new ResourceSetImpl

		var Resource ecore = loader.getResource(
			URI::createFileURI((selection.firstElement as IFile).rawLocation.toOSString),
			true
		)

		// Resolve proxies
		ecore.getContents().forEach[EcoreUtil::resolveAll(it)]

		generateCPPModel(ecore, rootPath)

		parent.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		return null
	}

	def private void generateCPPModel(Resource ecore, Path rootPath) {
		new EcoreGenerator(ecore, rootPath).startGeneration()
	}

	def private IResource getRoot(IFile element) {
		var IResource parent = element
		while (!(parent instanceof IProject)) {
			parent = parent.getParent()
		}
		return parent
	}

}
