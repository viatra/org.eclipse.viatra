/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests.performance;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.cps.tests.queries.OtherTests;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.testing.core.QueryPerformanceTest;

public class SimplePerformanceTest extends QueryPerformanceTest {

    @Override
    public QueryScope getScope() throws ViatraQueryException {
        ResourceSet set = new ResourceSetImpl();
        set.getResource(URI.createPlatformPluginURI("org.eclipse.viatra.query.runtime.cps.tests/models/instances/demo.cyberphysicalsystem", false), true);
        return new EMFScope(set);
    }

    @Override
    public IQueryGroup getQueryGroup() throws ViatraQueryException {
        return OtherTests.instance();
    }

}
