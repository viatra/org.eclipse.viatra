/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.RecipesPackage;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class TestEMFScope {

    @Test
    public void multipleResourceSets() throws IncQueryException {
        ResourceSet resourceSet1 = createResourceSet(); 
        ResourceSet resourceSet2 = createResourceSet();
        ReteNodeRecipe object1 = createObject(resourceSet1);
        ReteNodeRecipe object2 = createObject(resourceSet2);
        Set<ReteNodeRecipe> expectedObjects = ImmutableSet.of(object1, object2);
        Pattern pattern = createPattern();
        EMFScope scope = new EMFScope(ImmutableSet.of(resourceSet1, resourceSet2));
        Collection<? extends IPatternMatch> matches = getMatches(pattern, scope);
        Set<Object> actualObjects = ImmutableSet.copyOf(Iterables.transform(matches, new Function<IPatternMatch, Object>() {
            @Override
            public Object apply(IPatternMatch match) {
                return match.get(0);
            }
        }));
        assertEquals(expectedObjects, actualObjects);
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

    private Pattern createPattern() {
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
        parameterRef.getReferences().add(variableReference);
        patternBody.getVariables().add(parameterRef);
    
        ClassType classType = EMFPatternLanguageFactory.eINSTANCE.createClassType();
        classType.setClassname(RecipesPackage.Literals.RETE_NODE_RECIPE);
        EClassifierConstraint classifierConstraint = EMFPatternLanguageFactory.eINSTANCE.createEClassifierConstraint();
        classifierConstraint.setVar(variableReference);
        classifierConstraint.setType(classType);
        patternBody.getConstraints().add(classifierConstraint);

        return pattern;
    }

    private Collection<? extends IPatternMatch> getMatches(Pattern pattern, EMFScope scope) throws IncQueryException {
        IncQueryMatcher<? extends IPatternMatch> matcher = IncQueryEngine.on(scope).getMatcher(new SpecificationBuilder().getOrCreateSpecification(pattern));
        return matcher.getAllMatches();
    }

}
