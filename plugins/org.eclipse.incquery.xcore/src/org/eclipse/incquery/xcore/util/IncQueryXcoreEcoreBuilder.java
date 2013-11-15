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
package org.eclipse.incquery.xcore.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xcore.XAttribute;
import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XGenericType;
import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XOperation;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.XReference;
import org.eclipse.emf.ecore.xcore.XTypeParameter;
import org.eclipse.emf.ecore.xcore.util.XcoreEcoreBuilder;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.eclipse.incquery.xcore.model.XIncQueryDerivedFeature;
import org.eclipse.incquery.xcore.generator.IncQueryXcoreGenerator;
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper;

import com.google.inject.Inject;

public class IncQueryXcoreEcoreBuilder extends XcoreEcoreBuilder {
    
    @Inject
    private IncQueryXcoreMapper mapper;

    @Override
    public EPackage getEPackage(XPackage xPackage) {
        EPackage pack =  super.getEPackage(xPackage);
        EcoreUtil.setAnnotation(
                pack,
                EcorePackage.eNS_URI,
                "settingDelegates",
                IncQueryXcoreGenerator.queryBasedFeatureFactory
        );
        return pack;
    }
    
    @Override
    protected EClass getEClass(final XClass xClass) {
        final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        mapper.getMapping(xClass).setEClass(eClass);
        mapper.getToXcoreMapping(eClass).setXcoreElement(xClass);
        if (xClass.isInterface()) {
            eClass.setInterface(true);
            eClass.setAbstract(true);
        } else if (xClass.isAbstract()) {
            eClass.setAbstract(true);
        }
        EList<EGenericType> eGenericSuperTypes = eClass.getEGenericSuperTypes();
        for (XGenericType superType : xClass.getSuperTypes()) {
            eGenericSuperTypes.add(getEGenericType(superType));
        }
        EList<ETypeParameter> eTypeParameters = eClass.getETypeParameters();
        for (XTypeParameter xTypeParameter : xClass.getTypeParameters()) {
            ETypeParameter eTypeParameter = getETypeParameter(xTypeParameter);
            eTypeParameters.add(eTypeParameter);
        }
        EList<EOperation> eOperations = eClass.getEOperations();
        EList<EStructuralFeature> eStructuralFeatures = eClass.getEStructuralFeatures();
        for (XMember xMember : xClass.getMembers()) {
            if (xMember instanceof XOperation) {
                EOperation eOperation = getEOperation((XOperation) xMember);
                eOperations.add(eOperation);
            } else if (xMember instanceof XReference) {
                EReference eReference = getEReference((XReference) xMember);
                eStructuralFeatures.add(eReference);
            } else if (xMember instanceof XAttribute) {
                EAttribute eAttribute = getEAttribute((XAttribute) xMember);
                eStructuralFeatures.add(eAttribute);
            } else if (xMember instanceof XIncQueryDerivedFeature){
                EStructuralFeature eStructuralFeature = getIncQueryDerivedFeature((XIncQueryDerivedFeature) xMember);
                eStructuralFeatures.add(eStructuralFeature);
            }
        }
        return eClass;
    }

    protected EStructuralFeature getIncQueryDerivedFeature(XIncQueryDerivedFeature xIncQueryDerivedFeature) {
        final EStructuralFeature eStructuralFeature = (xIncQueryDerivedFeature.isReference() ? EcoreFactory.eINSTANCE.createEReference() : EcoreFactory.eINSTANCE.createEAttribute());
        mapper.getMapping(xIncQueryDerivedFeature).setEStructuralFeature(eStructuralFeature);
        mapper.getToXcoreMapping(eStructuralFeature).setXcoreElement(xIncQueryDerivedFeature);
        handleIncQueryDerivedFeature(eStructuralFeature, xIncQueryDerivedFeature);
        return eStructuralFeature;
    }
    
    /**
     * As a side effect of this call, the SettingDelegateFactory will be registered to handle things for dynamic resources.
     * @param eStructuralFeature
     * @param xIncQueryDerivedFeature
     */
    protected void handleIncQueryDerivedFeature(final EStructuralFeature eStructuralFeature, final XIncQueryDerivedFeature xIncQueryDerivedFeature) {
        eStructuralFeature.setName(nonNullName(xIncQueryDerivedFeature.getName()));
        handleETypedElement(eStructuralFeature, xIncQueryDerivedFeature);
        eStructuralFeature.setChangeable(false);
        eStructuralFeature.setTransient(true);
        eStructuralFeature.setVolatile(true);
        eStructuralFeature.setUnsettable(true);
        eStructuralFeature.setDerived(true);
        
        runnables.add(new Runnable() {
            @Override
            public void run() {
                org.eclipse.incquery.patternlanguage.patternLanguage.Pattern pattern = xIncQueryDerivedFeature.getPattern();
                
                if (pattern != null && pattern.eContainer() != null) {
                    EcoreUtil.setAnnotation(
                            eStructuralFeature, 
                            IncQueryXcoreGenerator.queryBasedFeatureFactory, 
                            "patternFQN", 
                            CorePatternLanguageHelper.getFullyQualifiedName(pattern)
                    );
                    
                    // mark the feature as well-behaving
                    WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedFeature(eStructuralFeature);
                }
            }
        });
    }
}
