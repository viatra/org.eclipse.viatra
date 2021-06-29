/**
 * Copyright (c) 2014-2021 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.runtime.cps.tests;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationInstancesOfApplicationTypeQuerySpecification;
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.ApplicationTypeWithHostedInstancesQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.testing.core.ModelLoadHelper;
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider;
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest;
import org.junit.Assert;
import org.junit.Test;

public class LocalSearchBaseIndexerSettingsTest {
    private final String snapshot = "org.eclipse.viatra.query.runtime.cps.tests/models/snapshots/test.snapshot";
    private final ModelLoadHelper modelLoader = new ModelLoadHelper();

    @Test
    public void baseIndexerDisabledTestSimpleQuery() {
        final QueryEvaluationHint hints = LocalSearchHints.getDefaultNoBase().build();
        ResourceSet rs = new ResourceSetImpl();
        final AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs));
        
        ViatraQueryTest.test(ApplicationInstancesOfApplicationTypeQuerySpecification.instance())
            .on(new EMFScope(rs))
            .withSnapshotMatches(modelLoader.loadExpectedResultsFromUri(rs, snapshot))
            .with(new PatternBasedMatchSetModelProvider(engine, hints))
            .assertEquals(); 
        
        // Check that nothing is indexed here
        final NavigationHelper index = EMFScope.extractUnderlyingEMFIndex(engine);
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE));
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_INSTANCE));
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE__INSTANCES));
        engine.dispose();
    }
    
    @Test
    public void baseIndexerDisabledTestComplexQuery() {
        final QueryEvaluationHint hints = LocalSearchHints.getDefaultNoBase().build();
        ResourceSet rs = new ResourceSetImpl();
        final AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(rs));
        
        ViatraQueryTest.test(ApplicationTypeWithHostedInstancesQuerySpecification.instance())
        .on(new EMFScope(rs))
        .withSnapshotMatches(modelLoader.loadExpectedResultsFromUri(rs, snapshot))
        .with(new PatternBasedMatchSetModelProvider(engine, hints))
        .assertEquals(); 
        
        // Check that nothing is indexed here
        final NavigationHelper index = EMFScope.extractUnderlyingEMFIndex(engine);
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE));
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_INSTANCE));
        Assert.assertEquals(IndexingLevel.NONE,
                index.getIndexingLevel(CyberPhysicalSystemPackage.Literals.APPLICATION_TYPE__INSTANCES));
        engine.dispose();
    }
}
