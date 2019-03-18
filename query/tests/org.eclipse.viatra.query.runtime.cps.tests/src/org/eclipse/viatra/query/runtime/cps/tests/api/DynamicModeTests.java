/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests.api;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.api.IndexingLevel;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DynamicModeTests {
    
    private EPackage epackage;
    private ResourceSet rs;
    
    @Before
    public void initializeEngine() {
        rs = new ResourceSetImpl();
        rs.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/dynamic/example.xmi", false), true);
    }
    
    @Before
    public void registerEPackage() {
        final Resource packageResource = new ResourceSetImpl().getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/dynamic/model.ecore", false), true);
        epackage = (EPackage) packageResource.getContents().get(0);
        EPackage.Registry.INSTANCE.put(epackage.getNsURI(), epackage); // This is required for pattern parser to find the results
    }
    
    @After
    public void unregisterEPackage() {
        EPackage.Registry.INSTANCE.remove(epackage.getNsURI(), epackage);
    }
   
    @Test
    public void testDynamicMatching() {
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs, new BaseIndexOptions(true, IndexingLevel.NONE)));
        final IQuerySpecification<?> query = initializeQuerySpecification();
        assertEquals(1, engine.getMatcher(query).countMatches());
    }

    
    @Test
    public void testNonDynamicMatching() {
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs, new BaseIndexOptions(false, IndexingLevel.NONE)));
        final IQuerySpecification<?> query = initializeQuerySpecification();
        assertEquals(0, engine.getMatcher(query).countMatches());
    }
    
    private IQuerySpecification<?> initializeQuerySpecification() throws AssertionError {
        final PatternParsingResults results = PatternParserBuilder.instance().withInjector(new EMFPatternLanguageStandaloneSetup().createStandaloneInjector())
                .parse("import \"http://www.example.org/model\"\n" + 
                        "\n" + 
                        "pattern BwithC(b:B,c:C){\n" + 
                        "    B.c(b,c);\n" + 
                        "}");
        final IQuerySpecification<?> query = results.getQuerySpecification("BwithC").orElseThrow(AssertionError::new);
        return query;
    }

}
