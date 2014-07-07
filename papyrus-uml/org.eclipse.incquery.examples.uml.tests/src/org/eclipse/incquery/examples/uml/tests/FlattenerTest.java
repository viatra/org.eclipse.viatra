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
package org.eclipse.incquery.examples.uml.tests;


import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.examples.uml.queries.util.EmptyClassQuerySpecification;
import org.eclipse.incquery.examples.uml.queries.util.HasPropertyOrOperationQuerySpecification;
import org.eclipse.incquery.examples.uml.queries.util.HasPropertyQuerySpecification;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PQueryFlattener;
import org.eclipse.incquery.testing.core.base.CompareQueryTester;
import org.eclipse.incquery.testing.core.base.DisjunctionBasedQuerySpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FlattenerTest {

    @Parameters
    public static Collection<Object[]> querySpecifications() throws IncQueryException {
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
        // TODO eliminate URI
        Resource model = rs.getResource(URI.createPlatformPluginURI("org.eclipse.incquery.examples.uml.evm/testmodels/Testmodel.uml", false), true);
        // Create an engine
        IncQueryEngine engine = IncQueryEngine.on(model);
        
        // Do the flattening
        PDisjunction flatDisjunction = flatten(querySpecification);
        // Create a new query specification with the flattened body
        IQuerySpecification<?> flattenedQuerySpecification = new DisjunctionBasedQuerySpecification(querySpecification, flatDisjunction);
        
        CompareQueryTester.assertQueriesEquivalent(engine, querySpecification, flattenedQuerySpecification);
    }

    /**
     * Helper method to print the result of a flattening - no matching is done
     * 
     * @throws Exception
     */
    public void printFlattenedDisjunction() throws Exception {
        IQuerySpecification<?> query = querySpecification;
        PDisjunction flattenedDisjunction = flatten(query);
        printDisjunction(flattenedDisjunction);
    }

    private PDisjunction flatten(PQuery query) throws Exception {
        PQueryFlattener flattener = new PQueryFlattener();
        PDisjunction flattenedDisjunction = flattener.flatten(query);
        return flattenedDisjunction;
    }

    private void printDisjunction(PDisjunction flattenedDisjunction) {
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
        
        System.out.println("Constarints");
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
