/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.uml.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.runtime.api.GenericPatternGroup;
import org.eclipse.incquery.runtime.api.IQueryGroup;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.core.QueryPerformanceTest;
import org.eclipse.incquery.uml.derivedfeatures.DerivedFeatures;
import org.junit.Ignore;

/**
 * @author Abel Hegedus
 *
 */
@Ignore("Should not run as part of the build, run on demand")
public class UMLSurrogateQueryPerformanceTest extends QueryPerformanceTest {

    private static final String INPUT_MODEL_PATH = "/org.eclipse.uml2.uml.resources/metamodels/UML.metamodel.uml";

    @Override
    public IncQueryScope getScope() throws IncQueryException {
        ResourceSetImpl rs = new ResourceSetImpl();
        URI umlModelUri = URI.createPlatformPluginURI(INPUT_MODEL_PATH, true);
        rs.getResource(umlModelUri, true);
        
        return new EMFScope(rs);
    }

    @Override
    public IQueryGroup getQueryGroup() throws IncQueryException {
        return GenericPatternGroup.of(
                DerivedFeatures.instance()
                );
    }

}
