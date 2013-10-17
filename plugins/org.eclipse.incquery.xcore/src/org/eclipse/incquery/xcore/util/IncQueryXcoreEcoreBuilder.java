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

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xcore.XAttribute;
import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XGenericType;
import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XOperation;
import org.eclipse.emf.ecore.xcore.XReference;
import org.eclipse.emf.ecore.xcore.XTypeParameter;
import org.eclipse.emf.ecore.xcore.util.XcoreEcoreBuilder;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureSettingDelegateFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.xcore.XIncQueryDerivedFeature;
import org.eclipse.incquery.xcore.generator.IncQueryXcoreGenerator;
import org.eclipse.incquery.xcore.mappings.IncQueryXcoreMapper;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class IncQueryXcoreEcoreBuilder extends XcoreEcoreBuilder {
    
    @Inject
    private IncQueryXcoreMapper mapper;

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
    
    
    @Inject
    private IResourceSetProvider resSetProvider;
    
        
    private Pattern createNewResoureSet(Pattern pattern)
            throws IOException {
        URI uri = pattern.eResource().getURI();
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = workspaceRoot.getFile(new Path(uri.toPlatformString(true)));
        ResourceSet resourceSet = null;
        IProject project = file.getProject();
        resourceSet = resSetProvider.get(project);
        URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        Resource resource = resourceSet.createResource(fileURI);
        resource.load(null);
        PatternModel topElement = (PatternModel) resource.getContents().get(0);
        for (Pattern p : topElement.getPatterns()) {
            if(p.getName().equals(pattern.getName())) {
                // found pattern
                return p;
            }
        }
        return null;
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
                org.eclipse.incquery.patternlanguage.patternLanguage.Pattern _pattern = xIncQueryDerivedFeature.getPattern();
                
                
                Pattern pattern = null;
         
                /* 
                 * attempt to fix a lot of weird issues:
                 * instead of using this pattern object, we make another one ala Query Explorer
                 * 
                 * doesn't work, because it will get into an infinite loop during scoping-inference
                 * 
                 */
                
                
                /*
                if (_pattern!=null && _pattern.eResource()!=null) {
                
	                try {
	                	pattern = createNewResoureSet(_pattern);
	                	System.out.println("hack worked");
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
                } else {
                	// fall back to old ways
                	System.out.println("fallback was necessary");
                	pattern = _pattern;
                }
                */
                
                pattern = _pattern;
                
                if (pattern != null && pattern.eContainer() != null) {
                    Object factory = EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.get(IncQueryXcoreGenerator.queryBasedFeatureFactory);
                    if (factory != null && factory instanceof QueryBasedFeatureSettingDelegateFactory) {
                        IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> spec = QuerySpecificationRegistry.getOrCreateQuerySpecification(pattern);
                        ((EStructuralFeature.Internal) eStructuralFeature).
                            setSettingDelegate(((QueryBasedFeatureSettingDelegateFactory) factory).
                                    createSettingDelegate(eStructuralFeature, spec, true, true));
                    } 
                }
            }
        });
    }
}
