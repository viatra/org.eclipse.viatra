/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage;
import org.eclipse.viatra.gui.tests.queries.IdIsNotUnique;
import org.eclipse.viatra.gui.tests.queries.IdIsNotUnique.Match;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas;
import org.eclipse.viatra.transformation.evm.specific.Jobs;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.evm.specific.Schedulers;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.junit.BeforeClass;
import org.junit.Test;

public class LifecycleTest {

    @BeforeClass
    public static void initializeTest() {
        // This is required to allow the test to execute outside OSGi
        CyberPhysicalSystemPackage.eINSTANCE.getNsURI();
    }
    
    private AdvancedViatraQueryEngine initializeQueryEngine() {
        ResourceSet set = new ResourceSetImpl();
        EMFScope scope = new EMFScope(set);
        return AdvancedViatraQueryEngine.createUnmanagedEngine(scope);
    }
    
    private ExecutionSchema initializeExecutionSchema(AdvancedViatraQueryEngine queryEngine) {
        ExecutionSchema schema = ExecutionSchemas.createViatraQueryExecutionSchema(queryEngine,
                Schedulers.getQueryEngineSchedulerFactory(queryEngine));
        Set<Job<IdIsNotUnique.Match>> jobs = Stream
                .of(Jobs.<IdIsNotUnique.Match> newStatelessJob(CRUDActivationStateEnum.CREATED, Match::prettyPrint))
                .collect(Collectors.toSet());
        final RuleSpecification<IdIsNotUnique.Match> rule = Rules.newMatcherRuleSpecification(IdIsNotUnique.instance(),
                Lifecycles.getDefault(false, false), jobs);
        schema.addRule(rule);

        return schema;
    }
    
    @Test
    public void initAndShutdown() {
        AdvancedViatraQueryEngine queryEngine = initializeQueryEngine();
        ExecutionSchema schema = initializeExecutionSchema(queryEngine);
        schema.dispose();
        queryEngine.dispose();
    }
    
    @Test
    public void initAndShutdownOutOfOrder() {
        AdvancedViatraQueryEngine queryEngine = initializeQueryEngine();
        ExecutionSchema schema = initializeExecutionSchema(queryEngine);
        queryEngine.dispose();
        schema.dispose();
    }

    
}
