/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.testing.core

import com.google.common.base.Preconditions
import com.google.inject.Injector
import org.eclipse.core.resources.IFile
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry
import org.eclipse.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.incquery.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum

/**
 * Helper methods for loading models from files or URIs.
 */
class ModelLoadHelper {
	/**
	 * Load an instance EMF model from the given file to a new resource set.
	 */
	def loadModelFromFile(IFile file) {
		loadModelFromUri(file.fullPath.toString);
	}

	/**
	 * Load an instance EMF model from the given platform URI to a new resource set.
	 */
	def loadModelFromUri(String platformUri){
		val resourceSet = new ResourceSetImpl()
		resourceSet.loadAdditionalResourceFromUri(platformUri)
	}

	/**
	 * Try to resolve a given platform URI first as a resource than as a plugin URI.
	 */
	/*def private resolvePlatformUri(String platformUri){
		val resourceURI = URI::createPlatformResourceURI(platformUri, true)
		if (URIConverter::INSTANCE.exists(resourceURI, null)) {
			return resourceURI
		}
		val pluginURI = URI::createPlatformPluginURI(platformUri, true)
		if (URIConverter::INSTANCE.exists(pluginURI, null)) {
			return pluginURI
		}
	}*/

	/**
	 * Load an additional resource into the resource set from a given file.
	 * Works for both pattern and target model resource sets.
	 */
	def loadAdditionalResourceFromFile(ResourceSet resourceSet, IFile file){
		resourceSet.loadAdditionalResourceFromUri(file.fullPath.toString)
	}

	/**
	 * Load an additional resource into the resource set from a given platform URI.
	 * Works for both pattern and target model resource sets.
	 */
	def loadAdditionalResourceFromUri(ResourceSet resourceSet, String platformUri){
		val modelURI = XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum::BOTH, platformUri)
		if(modelURI != null){
			resourceSet.getResource(modelURI, true)
		}
	}

	/**
	 * Load a pattern model from the given file into a new resource set.
	 */
	def loadPatternModelFromFile(IFile file, Injector injector){
		file.fullPath.toString.loadPatternModelFromUri(injector)
	}

	/**
	 * Load a pattern model from the given platform URI into a new resource set.
	 */
	def loadPatternModelFromUri(String platformUri, Injector injector, String... additionalDependencyPlatformUris){
		val resourceSet = XmiModelUtil.prepareXtextResource(injector)
		for (additionalUri : additionalDependencyPlatformUris)
			resourceSet.loadAdditionalResourceFromUri(additionalUri)
			
		val resource = resourceSet.loadAdditionalResourceFromUri(platformUri)
		if(resource.contents.size > 0){
			if(resource.contents.get(0) instanceof PatternModel){
				resource.contents.get(0) as PatternModel
			}
		}
	}

	/**
	 * Initialize a matcher for the pattern with the given name from the pattern model on the selected EMF root.
	 */
	def initializeMatcherFromModel(PatternModel model, IncQueryEngine engine, String patternName){
		val patterns = model.patterns.filter[
			patternName == CorePatternLanguageHelper.getFullyQualifiedName(it)
		]
		Preconditions.checkState(patterns.size == 1, "No pattern found with name " + patternName)
		val builder = new SpecificationBuilder(engine.registeredQuerySpecifications)
		engine.getMatcher(builder.getOrCreateSpecification(patterns.iterator.next))
	}

	def initializeMatcherFromModel(PatternModel model, Notifier emfRoot, String patternName){
		val engine = IncQueryEngine::on(emfRoot);
		model.initializeMatcherFromModel(engine,patternName)
	}
	/**
	 * Initialize a registered matcher for the pattern FQN on the selected EMF root.
	 */
	def initializeMatcherFromRegistry(Notifier emfRoot, String patternFQN){
		val querySpecification = QuerySpecificationRegistry::getQuerySpecification(patternFQN)
		querySpecification.getMatcher(IncQueryEngine::on(emfRoot))
	}

	/**
	 * Load the recorded match set into an existing resource set form the given file.
	 */
	def loadExpectedResultsFromFile(ResourceSet resourceSet, IFile file){
		resourceSet.loadExpectedResultsFromUri(file.fullPath.toString)
	}

	/**
	 * Load the recorded match set into an existing resource set form the given platform URI.
	 */
	def loadExpectedResultsFromUri(ResourceSet resourceSet, String platformUri){
		val resource = resourceSet.loadAdditionalResourceFromUri(platformUri);
		if(resource != null){
			if(resource.contents.size > 0){
				if(resource.contents.get(0) instanceof IncQuerySnapshot){
					resource.contents.get(0) as IncQuerySnapshot
				}
			}
		}
	}

	/**
	 * Load the recorded match set into a new resource set form the given file.
	 */
	def loadExpectedResultsFromFile(IFile file){
		file.fullPath.toString.loadExpectedResultsFromUri
	}

	/**
	 * Load the recorded match set into a new resource set form the given platform URI.
	 */
	def loadExpectedResultsFromUri(String platformUri){
		val resource = loadModelFromUri(platformUri);
		if(resource != null){
			if(resource.contents.size > 0){
				if(resource.contents.get(0) instanceof IncQuerySnapshot){
					resource.contents.get(0) as IncQuerySnapshot
				}
			}
		}
	}

	/**
	 * Returns the match set record for a given pattern name after it loads the snapshot from the given file.
	 */
	def loadExpectedResultsForPatternFromFile(ResourceSet resourceSet, IFile file, String patternFQN){
		resourceSet.loadExpectedResultsForPatternFromUri(file.fullPath.toString,patternFQN)
	}

	/**
	 * Returns the match set record for a given pattern name after it loads the snapshot from the given platform URI.
	 */
	def loadExpectedResultsForPatternFromUri(ResourceSet resourceSet, String platformUri, String patternFQN){
		val snapshot = resourceSet.loadAdditionalResourceFromUri(platformUri) as IncQuerySnapshot
		val matchsetrecord = snapshot.matchSetRecords.filter[patternQualifiedName.equals(patternFQN)]
		if(matchsetrecord.size == 1){
			return matchsetrecord.iterator.next
		}
	}
}