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
package org.eclipse.incquery.xcore.util

import com.google.inject.Inject
import java.util.HashSet
import java.util.List
import org.eclipse.emf.codegen.ecore.genmodel.GenClass
import org.eclipse.emf.codegen.ecore.genmodel.GenDataType
import org.eclipse.emf.codegen.ecore.genmodel.GenEnumLiteral
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenOperation
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.codegen.ecore.genmodel.GenParameter
import org.eclipse.emf.codegen.ecore.genmodel.GenTypeParameter
import org.eclipse.emf.common.util.UniqueEList
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.xcore.XClass
import org.eclipse.emf.ecore.xcore.XDataType
import org.eclipse.emf.ecore.xcore.XEnumLiteral
import org.eclipse.emf.ecore.xcore.XOperation
import org.eclipse.emf.ecore.xcore.XPackage
import org.eclipse.emf.ecore.xcore.XParameter
import org.eclipse.emf.ecore.xcore.XStructuralFeature
import org.eclipse.emf.ecore.xcore.XTypeParameter
import org.eclipse.emf.ecore.xcore.XcoreExtensions
import org.eclipse.emf.ecore.xcore.util.XcoreGenModelBuilder
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper
import org.eclipse.incquery.xcore.model.XIncQueryDerivedFeature

class IncQueryXcoreGenModelBuilder extends XcoreGenModelBuilder {

	@Inject
	protected extension IncQueryXcoreMapper mapper

	override initializeUsedGenPackages(GenModel genModel) {
		val referencedEPackages = new HashSet<EPackage>()
		val List<EPackage> ePackages = new UniqueEList<EPackage>()
		for (genPackage : genModel.genPackages) {
			val ePackage = genPackage.getEcorePackage
			if (ePackage != null) {
				ePackages.add(genPackage.getEcorePackage)
			}
		}

		var int i = 0
		while (i < ePackages.size()) {
			val ePackage = ePackages.get(i)
			i = i + 1
			val allContents = ePackage.eAllContents
			while (allContents.hasNext()) {
				val eObject = allContents.next()
				if (eObject instanceof EPackage) {
					allContents.prune()
				} else {
					for (eCrossReference : eObject.eCrossReferences) {
						switch eCrossReference {
							EClassifier: {
								val EPackage referencedEPackage = eCrossReference.getEPackage
								ePackages.add(referencedEPackage)
								referencedEPackages.add(referencedEPackage)
							}
							EStructuralFeature: {
								val eContainingClass = eCrossReference.getEContainingClass()
								if (eContainingClass != null) {
									val EPackage referencedEPackage = eContainingClass.getEPackage
									ePackages.add(referencedEPackage)
									referencedEPackages.add(referencedEPackage)
								}
							}
						}
					}
				}
			}
		}

		for (referencedEPackage : referencedEPackages) {
			if (genModel.findGenPackage(referencedEPackage) == null) {
				var usedGenPackage = mapper.getGen(mapper.getToXcoreMapping(referencedEPackage).xcoreElement) as GenPackage
				if (usedGenPackage == null) {
					usedGenPackage = findLocalGenPackage(referencedEPackage)
				}
				if (usedGenPackage != null) {
					genModel.usedGenPackages.add(usedGenPackage)
				} else {
					val resources = genModel.eResource.resourceSet.resources
					i = 1
					var boolean found = false
					while (i < resources.size && !found) {
						val resource = resources.get(i)
						val contents = resource.contents;
						if (!contents.empty) {
							val fileExtension = resource.getURI.fileExtension
							if ("xcoreiq".equals(fileExtension)) {
								val GenModel usedGenModel = resource.contents.get(1) as GenModel
								usedGenPackage = usedGenModel.findGenPackage(referencedEPackage)
								if (usedGenPackage != null) {
									genModel.usedGenPackages.add(usedGenPackage)
									found = true
								}
							} else if ("genmodel".equals(fileExtension)) {
								val GenModel usedGenModel = resource.contents.get(0) as GenModel
								usedGenModel.reconcile
								usedGenPackage = usedGenModel.findGenPackage(referencedEPackage)
								if (usedGenPackage != null) {
									genModel.usedGenPackages.add(usedGenPackage)
									found = true
								}
							}
						}
						i = i + 1
					}
					if (!found) {
						throw new RuntimeException("No GenPackage found for " + referencedEPackage)
					}
				}
			}
		}
	}

	override buildMap(GenModel genModel) {
		for (genElement : XcoreExtensions.allContentsIterable(genModel)) {
			switch genElement {
				GenPackage: {
					val xPackage = genElement.getEcorePackage.toXcoreMapping.xcoreElement as XPackage
					if (xPackage != null) {
						xPackage.mapping.genPackage = genElement
						genElement.toXcoreMapping.xcoreElement = xPackage
					}
				}
				GenClass: {
					val xClass = genElement.ecoreClass.toXcoreMapping.xcoreElement as XClass
					if (xClass != null) {
						xClass.mapping.genClass = genElement
						genElement.toXcoreMapping.xcoreElement = xClass
					}
				}
				GenDataType: {
					val xDataType = genElement.ecoreDataType.toXcoreMapping.xcoreElement as XDataType
					if (xDataType != null) {
						xDataType.mapping.genDataType = genElement
						genElement.toXcoreMapping.xcoreElement = xDataType
					}
				}
				GenFeature: {
					val xFeature = genElement.ecoreFeature.toXcoreMapping.xcoreElement
					if (xFeature != null) {
						if (xFeature instanceof XIncQueryDerivedFeature) {
							(xFeature as XIncQueryDerivedFeature).mapping.genFeature = genElement
							genElement.toXcoreMapping.xcoreElement = xFeature
						} else if (xFeature instanceof XStructuralFeature) {
							(xFeature as XStructuralFeature).mapping.genFeature = genElement
							genElement.toXcoreMapping.xcoreElement = xFeature
						}
					}

				}
				GenOperation: {
					val xOperation = genElement.ecoreOperation.toXcoreMapping.xcoreElement as XOperation
					if (xOperation != null) {
						xOperation.mapping.genOperation = genElement
						genElement.toXcoreMapping.xcoreElement = xOperation
					}
				}
				GenParameter: {

					val xParameter = genElement.ecoreParameter.toXcoreMapping.xcoreElement as XParameter
					if (xParameter != null) {
						xParameter.mapping.genParameter = genElement
						genElement.toXcoreMapping.xcoreElement = xParameter
					}
				}
				GenTypeParameter: {
					val xTypeParameter = genElement.ecoreTypeParameter.toXcoreMapping.xcoreElement as XTypeParameter
					if (xTypeParameter != null) {
						xTypeParameter.mapping.genTypeParameter = genElement
						genElement.toXcoreMapping.xcoreElement = xTypeParameter
					}
				}
				GenEnumLiteral: {
					val xEnumLiteral = genElement.ecoreEnumLiteral.toXcoreMapping.xcoreElement as XEnumLiteral
					if (xEnumLiteral != null) {
						xEnumLiteral.mapping.genEnumLiteral = genElement
						genElement.toXcoreMapping.xcoreElement = xEnumLiteral
					}
				}
			}
		}
	}
}
