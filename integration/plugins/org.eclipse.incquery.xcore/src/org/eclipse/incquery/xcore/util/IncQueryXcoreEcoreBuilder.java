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
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
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

/**
 * The Ecore builder is responsible to create the Ecore model for the IncQuery & Xcore specific metamodel. Apart from
 * mapping all Xcore related model elements to Ecore specific model elements, it also needs to register the setting
 * delegates related annotations on both the {@link EPackage} and the {@link EStructuralFeature}s. This class will be
 * used in the dynamic instance model use case. <br>
 * <br>
 * The specified runtime annotations will then be used in the runtime Eclipse for the loaded Dynamic Instance Model. <br>
 * (1) The {@link EPackage} will have an annotation with the source EcorePackage.eNS_URI, key "settingDelegates" and
 * value "org.eclipse.incquery.querybasedfeature". <br>
 * (2) The {@link EStructuralFeature}s will have an annotation with the source "org.eclipse.incquery.querybasedfeature",
 * key "patternFQN" and the value will be the fully qualified name of the EIQ pattern which provides the value of the
 * feature. The {@link SettingDelegate}s will be loaded with the use of the special editor, specifically tailored for
 * this scenario.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class IncQueryXcoreEcoreBuilder extends XcoreEcoreBuilder {

    @Inject
    private IncQueryXcoreMapper mapper;

    @Override
    public EPackage getEPackage(XPackage xPackage) {
        EPackage pack = super.getEPackage(xPackage);
        EcoreUtil.setAnnotation(pack, EcorePackage.eNS_URI, "settingDelegates",
                IncQueryXcoreGenerator.queryBasedFeatureFactory);
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
            } else if (xMember instanceof XIncQueryDerivedFeature) {
                EStructuralFeature eStructuralFeature = getIncQueryDerivedFeature((XIncQueryDerivedFeature) xMember);
                eStructuralFeatures.add(eStructuralFeature);
            }
        }
        return eClass;
    }

    protected EStructuralFeature getIncQueryDerivedFeature(XIncQueryDerivedFeature xIncQueryDerivedFeature) {
        final EStructuralFeature eStructuralFeature = (xIncQueryDerivedFeature.isReference() ? EcoreFactory.eINSTANCE
                .createEReference() : EcoreFactory.eINSTANCE.createEAttribute());
        mapper.getMapping(xIncQueryDerivedFeature).setEStructuralFeature(eStructuralFeature);
        mapper.getToXcoreMapping(eStructuralFeature).setXcoreElement(xIncQueryDerivedFeature);
        handleIncQueryDerivedFeature(eStructuralFeature, xIncQueryDerivedFeature);
        return eStructuralFeature;
    }

    /**
     * As a side effect of this call, the SettingDelegateFactory will be registered to handle things for dynamic
     * resources.
     * 
     * @param eStructuralFeature
     * @param xIncQueryDerivedFeature
     */
    protected void handleIncQueryDerivedFeature(final EStructuralFeature eStructuralFeature,
            final XIncQueryDerivedFeature xIncQueryDerivedFeature) {
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
                org.eclipse.incquery.patternlanguage.patternLanguage.Pattern pattern = xIncQueryDerivedFeature
                        .getPattern();

                if (pattern != null && pattern.eContainer() != null) {
                    EcoreUtil.setAnnotation(eStructuralFeature, IncQueryXcoreGenerator.queryBasedFeatureFactory,
                            "patternFQN", CorePatternLanguageHelper.getFullyQualifiedName(pattern));

                    // mark the feature as well-behaving
                    WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedFeature(eStructuralFeature);
                }
            }
        });
    }
}
