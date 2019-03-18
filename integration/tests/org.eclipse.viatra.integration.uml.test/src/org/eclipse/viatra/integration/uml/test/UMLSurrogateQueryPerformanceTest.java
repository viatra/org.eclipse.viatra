/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.uml.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.integration.uml.derivedfeatures.DerivedFeatures;
import org.eclipse.viatra.query.runtime.api.GenericQueryGroup;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.testing.core.QueryPerformanceTest;
import org.junit.Ignore;

/**
 * @author Abel Hegedus
 *
 */
@Ignore("Should not run as part of the build, run on demand")
public class UMLSurrogateQueryPerformanceTest extends QueryPerformanceTest {

    private static final String INPUT_MODEL_PATH = "/org.eclipse.uml2.uml.resources/metamodels/UML.metamodel.uml";

    @Override
    public QueryScope getScope() {
        ResourceSetImpl rs = new ResourceSetImpl();
        URI umlModelUri = URI.createPlatformPluginURI(INPUT_MODEL_PATH, true);
        rs.getResource(umlModelUri, true);
        
        return new EMFScope(rs);
    }

    @Override
    public IQueryGroup getQueryGroup() {
        return GenericQueryGroup.of(DerivedFeatures.instance());
    }

}
