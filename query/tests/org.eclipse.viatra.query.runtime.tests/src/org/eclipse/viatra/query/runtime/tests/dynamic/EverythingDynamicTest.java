/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests.dynamic;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Modifiers;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EverythingDynamicTest {

    // Metamodel fields
    private EPackage bookStoreEPackage;
    private EClass bookStoreEClass;
    private EClass bookEClass;
    private EAttribute bookName;
    private EAttribute bookStoreOwner;
    private EReference bookStore_Books;
    
    // Instance model fields
    private EObject bookStoreObject;
    
    private IQuerySpecification<?> querySpecification;

    @BeforeClass
    public static void registerInjector() {
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration(), EMFPatternLanguagePlugin.TEST_INJECTOR_PRIORITY);
    }
    
    @Before
    public void initializeDynamicInstances() {
        initializeMetamodel();
        initializeInstanceModel();
        initializePatternDefinition();
    }
    
    private void initializeMetamodel() {
        EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
        EcorePackage theCorePackage = EcorePackage.eINSTANCE;

        bookStoreEClass = theCoreFactory.createEClass();
        bookStoreEClass.setName("BookStore");
        bookStoreOwner = theCoreFactory.createEAttribute();
        bookStoreOwner.setName("owner");
        bookStoreOwner.setEType(theCorePackage.getEString());
        bookStoreEClass.getEStructuralFeatures().add(bookStoreOwner);

        bookEClass = theCoreFactory.createEClass();
        bookEClass.setName("Book");
        bookName = theCoreFactory.createEAttribute();
        bookName.setName("name");
        bookName.setEType(theCorePackage.getEString());
        bookEClass.getEStructuralFeatures().add(bookName);

        bookStore_Books = theCoreFactory.createEReference();
        bookStore_Books.setName("books");
        bookStore_Books.setEType(bookEClass);
        bookStore_Books.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
        bookStore_Books.setContainment(true);
        bookStoreEClass.getEStructuralFeatures().add(bookStore_Books);

        bookStoreEPackage = theCoreFactory.createEPackage();
        bookStoreEPackage.setName("BookStorePackage");
        bookStoreEPackage.setNsPrefix("bookStore");
        bookStoreEPackage.setNsURI("http:///org.example.viatra.bookstore");
        bookStoreEPackage.getEClassifiers().add(bookStoreEClass);
        bookStoreEPackage.getEClassifiers().add(bookEClass);
    }
    
    @SuppressWarnings("unchecked")
    private void initializeInstanceModel() {
        EFactory bookFactoryInstance = bookStoreEPackage.getEFactoryInstance();

        EObject bookObject = bookFactoryInstance.create(bookEClass);
        bookObject.eSet(bookName, "Harry Potter and the Deathly Hallows");

        bookStoreObject = bookFactoryInstance.create(bookStoreEClass);
        bookStoreObject.eSet(bookStoreOwner, "Somebody");
        ((List<EObject>) bookStoreObject.eGet(bookStore_Books)).add(bookObject);
    }
    
    private void initializePatternDefinition() {
        PatternModel patternModel = PatternLanguageFactory.eINSTANCE.createPatternModel();
        patternModel.setPackageName("TestPatternPackage");
        PackageImport packageImport = PatternLanguageFactory.eINSTANCE.createPackageImport();
        packageImport.setEPackage(bookStoreEPackage);
        VQLImportSection importSection = PatternLanguageFactory.eINSTANCE.createVQLImportSection();
        patternModel.setImportPackages(importSection);
        importSection.getPackageImport().add(packageImport);

        Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
        Modifiers modifiers = PatternLanguageFactory.eINSTANCE.createModifiers();
        pattern.setModifiers(modifiers);
        PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
        Variable variable = PatternLanguageFactory.eINSTANCE.createVariable();
        variable.setName("X");
        pattern.setName("plainPattern");
        pattern.getBodies().add(patternBody);
        pattern.getParameters().add(variable);

        ParameterRef parameterRef = PatternLanguageFactory.eINSTANCE.createParameterRef();
        parameterRef.setReferredParam(variable);
        parameterRef.setName("X");
        VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
        variableReference.setVar("X");
        variableReference.setVariable(parameterRef);
        patternBody.getVariables().add(parameterRef);

        ClassType classType = PatternLanguageFactory.eINSTANCE.createClassType();
        classType.setClassname(bookEClass);
        EClassifierConstraint classifierConstraint = PatternLanguageFactory.eINSTANCE.createEClassifierConstraint();
        classifierConstraint.setVar(variableReference);
        classifierConstraint.setType(classType);
        patternBody.getConstraints().add(classifierConstraint);

        patternModel.getPatterns().add(pattern);
        
        querySpecification = new SpecificationBuilder().getOrCreateSpecification(pattern);
    }
    
    private void match(ViatraQueryEngineOptions options) {
        final ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(bookStoreObject), options);
        ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(querySpecification);
        Collection<? extends IPatternMatch> matches = matcher.getAllMatches();
        
        Assert.assertNotNull(matches);
        Assert.assertSame(1, matches.size());
        IPatternMatch match = matches.iterator().next();
        Assert.assertEquals("\"X\"=Harry Potter and the Deathly Hallows", match.prettyPrint());
    }
    
    @Test
    public void dynamicRete() {
        match(ViatraQueryEngineOptions.getDefault());
    }
    
    @Test
    public void dynamicLS() {
        final ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder()
                .withDefaultHint(LocalSearchHints.getDefault().build()).build();
        match(options);
    }
    
    @Test
    public void dynamicLSNoBase() {
        final ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder()
                .withDefaultHint(LocalSearchHints.getDefaultNoBase().build()).build();
        match(options);
    }

}
