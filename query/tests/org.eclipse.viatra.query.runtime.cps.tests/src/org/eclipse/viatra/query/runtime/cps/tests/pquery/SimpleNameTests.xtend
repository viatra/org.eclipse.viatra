/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests.pquery;

import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AllDependenciesQuerySpecification;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.eclipse.viatra.query.runtime.cps.tests.queries.resolveordering.util.SecondPatternQuerySpecification
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.junit.runners.Parameterized.Parameter
import java.util.Collection
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.Assert
import org.eclipse.viatra.query.runtime.cps.tests.queries.Dred
import org.eclipse.viatra.query.runtime.api.IQueryGroup
import org.eclipse.viatra.query.runtime.cps.tests.queries.resolveordering.B_second
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry
import org.eclipse.viatra.query.runtime.registry.connector.QueryGroupProviderSourceConnector
import org.eclipse.viatra.query.runtime.extensibility.IQueryGroupProvider
import org.eclipse.viatra.query.runtime.extensibility.SingletonQueryGroupProvider
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries

@RunWith(Parameterized)
class SimpleNameTests {

    @Parameters(name="{index} : {0}")
    static def Collection<Object[]> data() {
        return #[
            #[AllDependenciesQuerySpecification.instance, "allDependencies", Dred.instance],
            #[SecondPatternQuerySpecification.instance, "secondPattern", B_second.instance]
        ]
    }

    @Parameter(0)
    public var IQuerySpecification<?> specification
    @Parameter(1)
    public var String expectedName
    @Parameter(2)
    public var IQueryGroup group
    
    @Test
    def testSimpleQueryName() {
        val simpleName = specification.simpleName
        Assert.assertEquals(expectedName, simpleName)
    }

    @Test
    def testQueryRegistryAccess() {
        val registry = QuerySpecificationRegistry.instance
        val IQueryGroupProvider provider = new SingletonQueryGroupProvider(group)
        registry.addSource(new QueryGroupProviderSourceConnector(group.class.name, provider, true))
        
        val view = registry.createView(entry | PQueries.calculateSimpleName(entry.fullyQualifiedName) == expectedName)
        Assert.assertTrue(view.entries.exists[entry | entry.get == specification])
    }
}
