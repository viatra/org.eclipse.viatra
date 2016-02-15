/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.examples.bpm.tests;

import org.eclipse.viatra.examples.bpm.tests.QueryBasedFeatureTest;
import org.eclipse.viatra.examples.bpm.tests.RuntimeListenerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Abel Hegedus
 *
 */
@RunWith(value = Suite.class)
@SuiteClasses({
    RuntimeListenerTest.class,
    QueryBasedFeatureTest.class
})
public class BPMTestsAll {

}
