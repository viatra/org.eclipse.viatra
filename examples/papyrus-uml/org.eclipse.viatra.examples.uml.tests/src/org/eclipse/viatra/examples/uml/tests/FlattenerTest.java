/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.examples.uml.tests;


import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.uml.queries.util.EmptyClassQuerySpecification;
import org.eclipse.viatra.examples.uml.queries.util.HasPropertyOrOperationQuerySpecification;
import org.eclipse.viatra.examples.uml.queries.util.HasPropertyQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PQueryFlattener;
import org.eclipse.viatra.query.testing.core.base.CompareQueryTester;
import org.eclipse.viatra.query.testing.core.base.DisjunctionBasedQuerySpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FlattenerTest {

    @Parameters
    public static Collection<Object[]> querySpecifications() throws ViatraQueryException {
        return Arrays.asList(new Object[][] { 
                { HasPropertyOrOperationQuerySpecification.instance() },
                { HasPropertyQuerySpecification.instance() }, 
                { EmptyClassQuerySpecification.instance() } 
        });
    }
    
    @Parameter
    public IQuerySpecification<?> querySpecification;

    @Test
    public void compareMatches() throws Exception{
        // Load the UML model
        ResourceSet rs = new ResourceSetImpl();
        Resource model = rs.getResource(URI.createPlatformPluginURI("org.eclipse.incquery.examples.uml.evm/testmodels/Testmodel.uml", false), true);
        // Create an engine
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(model));
        
        // Do the flattening
        PDisjunction flatDisjunction = flatten(querySpecification.getInternalQueryRepresentation());
        // Create a new query specification with the flattened body
        IQuerySpecification<?> flattenedQuerySpecification = new DisjunctionBasedQuerySpecification(querySpecification, flatDisjunction);
        
        
        CompareQueryTester.assertQueriesEquivalent(engine, querySpecification, flattenedQuerySpecification);
    }

    private PDisjunction flatten(PQuery query) throws Exception {
        PQueryFlattener flattener = new PQueryFlattener(new DefaultFlattenCallPredicate());
        PDisjunction flattenedDisjunction = flattener.rewrite(query);
        return flattenedDisjunction;
    }

    /**
     * Helper method to print the result of a flattening - no matching is done
     * 
     * @throws Exception
     */
    public static void printDisjunction(PDisjunction flattenedDisjunction) {
        Set<PBody> bodies = flattenedDisjunction.getBodies();
        int i = 0;
        
        System.out.println("Variables");
        for (PBody pBody : bodies) {
            System.out.println("Body " + (i++));
            
            Set<PVariable> variables = pBody.getAllVariables();
            for (PVariable pVariable : variables) {
                System.out.println(pVariable.toString());
            }
        }
        
        System.out.println("Constraints");
        i = 0;
        for (PBody pBody : bodies) {
            System.out.println("Body " + (i++));
            
            Set<PConstraint> constraints = pBody.getConstraints();
            for (PConstraint pConstraint : constraints) {
                System.out.println(pConstraint.toString());
            }
        }
        
        System.out.println("Exported Parameters:");
        for (PBody pBody : bodies) {
            System.out.println(pBody.getSymbolicParameters());
        }
    }
}
