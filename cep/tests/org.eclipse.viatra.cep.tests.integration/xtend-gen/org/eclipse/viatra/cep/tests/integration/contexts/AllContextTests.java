/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.tests.integration.contexts;

import org.eclipse.viatra.cep.tests.integration.contexts.ChronicleTests;
import org.eclipse.viatra.cep.tests.integration.contexts.ImmediateTests;
import org.eclipse.viatra.cep.tests.integration.contexts.StrictTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ChronicleTests.class, ImmediateTests.class, StrictTests.class })
@SuppressWarnings("all")
public class AllContextTests {
}
