/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.xcore.generator

import com.google.common.collect.Lists
import com.google.inject.Inject
import java.util.Collections
import java.util.HashSet
import java.util.List
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.xcore.XClass
import org.eclipse.emf.ecore.xcore.XDataType
import org.eclipse.emf.ecore.xcore.XPackage
import org.eclipse.emf.ecore.xcore.XStructuralFeature
import org.eclipse.emf.ecore.xcore.generator.XcoreGenerator
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel
import org.eclipse.incquery.tooling.core.generator.ExtensionGenerator
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper
import org.eclipse.incquery.xcore.XIncQueryDerivedFeature
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper
import org.eclipse.pde.core.plugin.IPluginExtension
import org.eclipse.xtext.common.types.JvmFormalParameter
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.xbase.compiler.XbaseCompiler

class IncQueryXcoreGenerator extends XcoreGenerator {
	
	@Inject
	XbaseCompiler compiler

	@Inject
 	private extension IncQueryXcoreMapper mappings

	public static String queryBasedFeatureFactory = "org.eclipse.incquery.querybasedfeature"
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		val pack = resource.contents.head as XPackage

		val EPackage ePackage = pack.mapping.EPackage
		val List<IPluginExtension> extensions = Lists.newArrayList()
		val GenModel genModel = resource.contents.filter(typeof(GenModel)).head
		val IProject project = ResourcesPlugin.workspace.root.getProject(genModel.modelPluginID);
		val ExtensionGenerator exGen = new ExtensionGenerator
		exGen.setProject(project)
		
		//Set annotation for the query based feature factory
		EcoreUtil::setAnnotation(
			ePackage,
			EcorePackage.eNS_URI,
			"settingDelegates",
			queryBasedFeatureFactory
		)

		val processed = newHashSet();
		for (xClassifier : pack.classifiers) {
			if (xClassifier instanceof XDataType) {
				val xDataType = xClassifier as XDataType;
				val eDataType = xDataType.mapping.EDataType
				val createBody = xDataType.createBody
				val creator = xDataType.mapping.creator
				if (createBody != null && creator != null) {
					val appendable = createAppendable
					appendable.declareVariable(creator.parameters.get(0), "it")
					compiler.compile(createBody, appendable, creator.returnType, Collections::emptySet)
					EcoreUtil::setAnnotation(eDataType, GenModelPackage::eNS_URI, "create",
						extractBody(appendable.toString))
				}
				val convertBody = xDataType.convertBody
				val converter = xDataType.mapping.converter
				if (convertBody != null && converter != null) {
					val appendable = createAppendable
					appendable.declareVariable(converter.parameters.get(0), "it")
					compiler.compile(convertBody, appendable, converter.returnType, Collections::emptySet)
					EcoreUtil::setAnnotation(eDataType, GenModelPackage::eNS_URI, "convert",
						extractBody(appendable.toString))
				}
			} else {
				val xClass = xClassifier as XClass;
				val eClass = xClass.mapping.EClass;
				for (eStructuralFeature : eClass.EAllStructuralFeatures) {
					if (processed.add(eStructuralFeature)) {
						val xFeature = mappings.getXcoreElement(eStructuralFeature);
						if (xFeature != null) {
							if (xFeature instanceof XStructuralFeature) {
								val getBody = (xFeature as XStructuralFeature).getBody
								if (getBody != null) {
									val getter = mappings.getMapping(xFeature as XStructuralFeature).getter
									val appendable = createAppendable
									compiler.compile(getBody, appendable, getter.returnType, Collections::emptySet)
									EcoreUtil::setAnnotation(eStructuralFeature, GenModelPackage::eNS_URI, "get",
										extractBody(appendable.toString))
								}
							} else if (xFeature instanceof XIncQueryDerivedFeature) {
								val XIncQueryDerivedFeature feature = xFeature as XIncQueryDerivedFeature

								//Set annotation for the query based feature factory
								EcoreUtil::setAnnotation(eStructuralFeature, queryBasedFeatureFactory,
									"patternFQN",
									(feature.pattern.eContainer as PatternModel).packageName + "." +
										feature.pattern.name)
										
								extensions.add(WellBehavingFeatureDefinitionGenerator.generateExtension(eStructuralFeature, exGen))
							}
						}
					}
				}
				for (eOperation : eClass.EAllOperations) {
					if (processed.add(eOperation)) {
						val xOperation = mappings.getXOperation(eOperation);
						if (xOperation != null) {
							val body = xOperation.body
							if (body != null) {
								val jvmOperation = mappings.getMapping(xOperation).jvmOperation
								if (jvmOperation != null) {
									val appendable = createAppendable
									for (JvmFormalParameter parameter : jvmOperation.parameters) {
										appendable.declareVariable(parameter, parameter.getName())
									}
									compiler.compile(body, appendable, jvmOperation.returnType,
										new HashSet<JvmTypeReference>(jvmOperation.exceptions))
									EcoreUtil::setAnnotation(eOperation, GenModelPackage::eNS_URI, "body",
										extractBody(appendable.toString))
								}
							}
						}
					}
				}
			}
		}

		generateGenModel(genModel, fsa)
		ProjectGenerationHelper.ensureExtensions(project, extensions, WellBehavingFeatureDefinitionGenerator.removableExtensionIdentifiers)
	}
}
