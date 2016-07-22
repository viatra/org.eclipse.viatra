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
package org.eclipse.viatra.query.tooling.cpp.localsearch.ui.handler

import com.google.inject.Injector
import javax.inject.Inject
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.tooling.cpp.localsearch.LocalSearchCppGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.api.ILocalsearchGeneratorOutputProvider
import org.eclipse.viatra.query.tooling.cpp.localsearch.serializer.DefaultSerializer
import org.eclipse.viatra.query.tooling.cpp.localsearch.ui.XTextFileAccessor
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.FileSystemAccess
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2
import org.eclipse.xtext.generator.IContextualOutputConfigurationProvider
import org.eclipse.xtext.resource.XtextResourceSet

/**
 * This class is a generic generate command handler called by both {@link IteratorGeneratorHandler} and {@link RuntimeGeneratorHandler}. 
 * It returns the .vql resource based on the current selection, creates the code generator and it also sets up the Xtext based {@link FileSystemAccess}.  
 * 
 * @author Robert Doczi
 */
class GeneratorHandler {

	@Inject Injector injector

	@Inject IContextualOutputConfigurationProvider outputConfigurationProvider

	def generate(ExecutionEvent event, Class<? extends ILocalsearchGeneratorOutputProvider> generatorClass) {
		val selection = HandlerUtil.getCurrentSelection(event)

		if (selection instanceof IStructuredSelection) {
			val xtextResourceSet = new XtextResourceSet
			val patternDefinitionResource = selection.getPatternDefinitionResource(xtextResourceSet)
			patternDefinitionResource.contents.forEach[EcoreUtil::resolveAll(it)]
			
			val patternDefinitionResourceUri = patternDefinitionResource.getURI.toString
			val extensionlessUri = patternDefinitionResourceUri.substring(0, patternDefinitionResourceUri.lastIndexOf('.'))
			val fileName = extensionlessUri.substring(extensionlessUri.lastIndexOf('/') + 1)
			
			val ast = patternDefinitionResource.getContents().get(0) as PatternModel

			val fileSystemAccess = patternDefinitionResource.fileSystemAccess

			val generator = new LocalSearchCppGenerator(generatorClass)
			val pQueries = ASTtoPQueriesHelper::astToPQueries(ast)
			val outputProvider = generator.generate(fileName, patternDefinitionResource, pQueries)

			val serializer = new DefaultSerializer
			serializer.serialize("", outputProvider, new XTextFileAccessor(fileSystemAccess))
		}

		return null
	}

	def getFileSystemAccess(Resource resource) {
		val fileSystemAccess = new EclipseResourceFileSystemAccess2

		injector.injectMembers(fileSystemAccess)

		val workspaceRoot = ResourcesPlugin.workspace.root

		val project = workspaceRoot.getProject(resource.getURI.segment(1))
		fileSystemAccess.project = project
		fileSystemAccess.monitor = new NullProgressMonitor

		val outputConfigurations = outputConfigurationProvider.getOutputConfigurations(resource)

		val outputConfigurationsMap = newHashMap
		outputConfigurations.forEach[
			it.outputDirectory = '''./cpp-gen'''
			outputConfigurationsMap.put(name, it)
		]

		fileSystemAccess.outputConfigurations = outputConfigurationsMap

		return fileSystemAccess
	}

	def getPatternDefinitionResource(IStructuredSelection selection, ResourceSet loader) {
		loader.getResource(
			URI::createPlatformResourceURI((selection.getFirstElement() as IFile).getFullPath().toPortableString(), true),
			true
		)
	}
}