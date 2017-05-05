/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.tests.integration

import org.eclipse.viatra.cep.core.api.engine.CEPEngine
import org.eclipse.viatra.cep.core.streams.EventStream
import org.eclipse.viatra.cep.tests.integration.contexts.TestResultHelper
import org.eclipse.viatra.cep.tests.integration.internal.DefaultRealm
import org.eclipse.viatra.cep.tests.integration.model.CepFactory
import org.eclipse.xtend.lib.annotations.Accessors
import org.junit.After
import org.junit.Before

abstract class BaseIntegrationTest {

    protected extension CepFactory cf = CepFactory.getInstance

    private DefaultRealm defaultRealm
    @Accessors(PROTECTED_GETTER, PROTECTED_SETTER) EventStream eventStream
    @Accessors(PROTECTED_GETTER, PROTECTED_SETTER) CEPEngine engine

    @Before
    def void setUp() throws Exception {
        defaultRealm = new DefaultRealm()
        TestResultHelper.instance.results.clear
    }

    @After
    def void tearDown() throws Exception {
        eventStream = null
        engine = null
        defaultRealm.dispose
        TestResultHelper.instance.results.clear
    }
}
