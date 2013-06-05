/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.builder.xmi

import com.google.inject.Inject
import java.util.ArrayList
import java.util.HashSet
import java.util.Map
import org.apache.log4j.Logger
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.helper.EMFPatternLanguageHelper
import org.eclipse.incquery.patternlanguage.emf.validation.PatternSetValidator
import org.eclipse.incquery.patternlanguage.emf.validation.PatternValidationStatus
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCall
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternURIHandler
import org.eclipse.xtext.common.types.JvmFormalParameter
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall

/**
 * @author Mark Czotter
 */
class XmiModelBuilder {
	
	@Inject Logger logger
	@Inject PatternSetValidator validator
	
	/**
	 * Builds one model file (XMI) from the input into the folder.
	 */
	def build(ResourceSet resourceSet, String fileFullPath) {
		try {
			// create the model in memory
			val xmiModelRoot = EMFPatternLanguageFactory::eINSTANCE.createPatternModel
			xmiModelRoot.setImportPackages(EMFPatternLanguageFactory::eINSTANCE.createXImportSection)
			val xmiResource = resourceSet.createResource(URI::createPlatformResourceURI(fileFullPath, true))
			xmiResource.contents.add(xmiModelRoot)
			// add import declarations 
			val HashSet<EPackage> importDeclarations = newHashSet()
			/*
			 * The following change avoids two different errors:
			 *  * concurrentmodification of the growing list of resources
			 *  * and a bug wrt Guice flatten and EMF BasicEList
			 */ 
			//val packageImports = resourceSet.resources.map(r | r.allContents.toIterable.filter(typeof (PackageImport))).flatten
			val resources = new ArrayList(resourceSet.resources)
			for (r : resources) {
				for (obj : r.contents) {
					if (obj instanceof PatternModel && !obj.equals(xmiModelRoot)) {
						for (importDecl : EMFPatternLanguageHelper::getPackageImportsIterable(obj as PatternModel)){
							val ePackage = importDecl.EPackage
							if (ePackage != null && !ePackage.eIsProxy && !importDeclarations.contains(ePackage)) {
								importDeclarations.add(ePackage)
							}
						}
					}
				}
//				val packageImports = r.allContents.toIterable.filter(typeof (PackageImport))
//				if (!packageImports.empty) {
//					for (importDecl : packageImports) {
//						if (!importDeclarations.contains(importDecl.EPackage)) {
//							importDeclarations.add(importDecl.EPackage)
//							xmiModelRoot.importPackages.add(importDecl)
//						}
//					}
//				}
			}
			xmiModelRoot.importPackages?.packageImport.addAll(importDeclarations.map[
				val imp = EMFPatternLanguageFactory::eINSTANCE.createPackageImport
				imp.setEPackage(it)
				return imp
			])
			// first add all error-free patterns
			val newParameters = new ArrayList
			val Map<String, Pattern> fqnToPatternMap = newHashMap();
			for (pattern : resources.map(r | r.allContents.toIterable.filter(typeof (Pattern))).flatten) {
				if (validator.validateTransitively(pattern).status != PatternValidationStatus::ERROR){
					newParameters += pattern.copyPattern(fqnToPatternMap, xmiModelRoot)
				}
			}
			xmiModelRoot.eResource.contents += newParameters
			// then iterate over all added PatternCall and change the patternRef
			for (call : xmiModelRoot.eAllContents.toIterable.filter(typeof (PatternCall))) {
				val fqn = CorePatternLanguageHelper::getFullyQualifiedName(call.patternRef)
				val p = fqnToPatternMap.get(fqn)
				if (p == null) {
					logger.error("Pattern not found: " +fqn)
				} else {
					call.setPatternRef(p as Pattern)
				}
			}
			// save the xmi file 
			val options = newHashMap(XMLResource::OPTION_URI_HANDLER -> new EMFPatternURIHandler(importDeclarations))
			xmiResource.save(options) 
		} catch(Exception e) {
			logger.error("Exception during XMI build!", e)
		}
	}
	
	def copyPattern(Pattern pattern, Map<String, Pattern> fqnToPatternMap, PatternModel xmiModelRoot) {
		
		val copier = new EcoreUtil$Copier();
    	val p = copier.copy(pattern) as Pattern;
    	copier.copyReferences();
    
		val fqn = CorePatternLanguageHelper::getFullyQualifiedName(pattern)
		p.name = fqn
		p.fileName = pattern.eResource.URI.toString
		if (fqnToPatternMap.get(fqn) != null) {
			logger.error("Pattern already set in the Map: " + fqn)
		} else {
			fqnToPatternMap.put(fqn, p)
			xmiModelRoot.patterns.add(p)
		}
		
		val newParameters = new ArrayList
				
		// iterate over each body
		val iterator = p.eAllContents
		while(iterator.hasNext) {
			val next = iterator.next
			if (next instanceof XExpression) {
				val expr = next as XExpression

				for (expression : expr.eAllContents.toIterable.filter(typeof(XFeatureCall))) {
						val f = expression.feature
						if (f instanceof JvmFormalParameter) {
							val target = copier.copy(f) as JvmFormalParameter
							expression.feature = target
							newParameters += target
						}
					}
				// Avoid traversing further into the expression
				iterator.prune
			}
		}
		return newParameters
	}
}
