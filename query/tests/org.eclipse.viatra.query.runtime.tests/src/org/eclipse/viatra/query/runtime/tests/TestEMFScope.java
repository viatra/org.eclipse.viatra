/*******************************************************************************
 * Copyright (c) 2010-2012, Denes Harmath, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.vql.LocalVariable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesFactory;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesPackage;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteRecipe;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class TestEMFScope {

    @Test
    public void multipleResourceSets() {
        ResourceSet resourceSet1 = createResourceSet(); 
        ResourceSet resourceSet2 = createResourceSet();
        ReteNodeRecipe object1 = createObject(resourceSet1);
        ReteNodeRecipe object2 = createObject(resourceSet2);
        Set<ReteNodeRecipe> expectedObjects = ImmutableSet.of(object1, object2);
        Pattern pattern = createNodePattern();
        EMFScope scope = new EMFScope(ImmutableSet.of(resourceSet1, resourceSet2));
        Collection<? extends IPatternMatch> matches = getMatches(pattern, scope);
        Set<Object> actualObjects = getFirstParamsInMatchSet(matches);
        assertEquals(expectedObjects, actualObjects);
    }

    @Test
    public void eListSet() {
        ResourceSet resourceSet = createResourceSet(); 
        ReteNodeRecipe object1 = createObject(resourceSet);
        ReteNodeRecipe object2 = createObject(resourceSet);
        ReteRecipe container = createContainer(resourceSet);
        Pattern pattern = createPatternWithEdge();
        EMFScope scope = new EMFScope(container);
        
        Collection<? extends IPatternMatch> matches0 = getMatches(pattern, scope);
        assertEquals(Collections.emptySet(), matches0);
        
        container.getRecipeNodes().add(object1);
        Set<ReteNodeRecipe> expectedObjects1 = ImmutableSet.of(object1);
        Collection<? extends IPatternMatch> matches1 = getMatches(pattern, scope);
        Set<Object> actualObjects1 = getFirstParamsInMatchSet(matches1);
        assertEquals(expectedObjects1, actualObjects1);
        
        container.getRecipeNodes().set(0, object2);
        Set<ReteNodeRecipe> expectedObjects2 = ImmutableSet.of(object2);
        Collection<? extends IPatternMatch> matches2 = getMatches(pattern, scope);
        Set<Object> actualObjects2 = getFirstParamsInMatchSet(matches2);
        assertEquals(expectedObjects2, actualObjects2);
    }

    private ResourceSet createResourceSet() {
        ResourceSetImpl result = new ResourceSetImpl();
        result.createResource(URI.createURI("temp"));
        return result;
    }

    private ReteNodeRecipe createObject(ResourceSet resourceSet) {
        ReteNodeRecipe recipe = RecipesFactory.eINSTANCE.createConstantRecipe();
        resourceSet.getResources().get(0).getContents().add(recipe);
        return recipe;
    }

    private ReteRecipe createContainer(ResourceSet resourceSet) {
        ReteRecipe container = RecipesFactory.eINSTANCE.createReteRecipe();
        resourceSet.getResources().get(0).getContents().add(container);
        return container;
    }

    private Pattern createNodePattern() {
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration(), EMFPatternLanguagePlugin.TEST_INJECTOR_PRIORITY);
    
        Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
        PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
        Variable variable = PatternLanguageFactory.eINSTANCE.createVariable();
        String variableName = "reteNodeRecipe";
        variable.setName(variableName);
        pattern.setName("reteNodeRecipe");
        pattern.getBodies().add(patternBody);
        pattern.getParameters().add(variable);
    
        ParameterRef parameterRef = PatternLanguageFactory.eINSTANCE.createParameterRef();
        parameterRef.setReferredParam(variable);
        parameterRef.setName(variableName);
        VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
        variableReference.setVar(variableName);
        variableReference.setVariable(parameterRef);
        patternBody.getVariables().add(parameterRef);
    
        ClassType classType = PatternLanguageFactory.eINSTANCE.createClassType();
        classType.setClassname(RecipesPackage.Literals.RETE_NODE_RECIPE);
        EClassifierConstraint classifierConstraint = PatternLanguageFactory.eINSTANCE.createEClassifierConstraint();
        classifierConstraint.setVar(variableReference);
        classifierConstraint.setType(classType);
        patternBody.getConstraints().add(classifierConstraint);

        return pattern;
    }

    private Pattern createPatternWithEdge() {
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration(), EMFPatternLanguagePlugin.TEST_INJECTOR_PRIORITY);
    
        Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
        PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
        Variable variable = PatternLanguageFactory.eINSTANCE.createVariable();
        String variableName = "reteNodeRecipe";
        variable.setName(variableName);
        pattern.setName("reteNodeRecipeContained");
        pattern.getBodies().add(patternBody);
        pattern.getParameters().add(variable);
    
        ParameterRef parameterRef = PatternLanguageFactory.eINSTANCE.createParameterRef();
        parameterRef.setReferredParam(variable);
        parameterRef.setName(variableName);
        VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
        variableReference.setVar(variableName);
        variableReference.setVariable(parameterRef);
        patternBody.getVariables().add(parameterRef);
       
        LocalVariable localVariable = PatternLanguageFactory.eINSTANCE.createLocalVariable(); 
        String localVariableName = "container";
        localVariable.setName(localVariableName);
        patternBody.getVariables().add(localVariable);
        VariableReference localVariableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
        localVariableReference.setVar(localVariableName);
        localVariableReference.setVariable(localVariable);

        ClassType classType = PatternLanguageFactory.eINSTANCE.createClassType();
        classType.setClassname(RecipesPackage.Literals.RETE_RECIPE);
        ReferenceType referenceType = PatternLanguageFactory.eINSTANCE.createReferenceType();
        referenceType.setRefname(RecipesPackage.Literals.RETE_RECIPE__RECIPE_NODES);
        PathExpressionConstraint pathExpressionConstraint = PatternLanguageFactory.eINSTANCE.createPathExpressionConstraint();
        patternBody.getConstraints().add(pathExpressionConstraint);
        pathExpressionConstraint.setSrc(localVariableReference);
        pathExpressionConstraint.setDst(variableReference);
        pathExpressionConstraint.setSourceType(classType);
        pathExpressionConstraint.getEdgeTypes().add(referenceType);
        return pattern;
    }

    private Collection<? extends IPatternMatch> getMatches(Pattern pattern, EMFScope scope) {
        ViatraQueryMatcher<? extends IPatternMatch> matcher = ViatraQueryEngine.on(scope).getMatcher(new SpecificationBuilder().getOrCreateSpecification(pattern));
        return matcher.getAllMatches();
    }
    
    private Set<Object> getFirstParamsInMatchSet(Collection<? extends IPatternMatch> matches) {
        Set<Object> actualObjects1 = ImmutableSet.copyOf(Iterables.transform(matches, match -> match.get(0)));
        return actualObjects1;
    }


}
