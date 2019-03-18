/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.base.itc;

import org.eclipse.viatra.query.runtime.base.itc.counting.CountingCompleteGraphTestCase;
import org.eclipse.viatra.query.runtime.base.itc.dfs.DFSCompleteGraphTestCase;
import org.eclipse.viatra.query.runtime.base.itc.dred.DRedCompleteGraphTestCase;
import org.eclipse.viatra.query.runtime.base.itc.dred.DRedGraphsTestCase;
import org.eclipse.viatra.query.runtime.base.itc.incscc.IncSCCCompleteGraphTestCase;
import org.eclipse.viatra.query.runtime.base.itc.incscc.IncSCCGraphsTestCase;
import org.eclipse.viatra.query.runtime.base.itc.incscc.IncSCCPathConstructionTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
        DRedGraphsTestCase.class,
        DRedCompleteGraphTestCase.class, 
        DFSCompleteGraphTestCase.class, 
        CountingCompleteGraphTestCase.class,
        IncSCCGraphsTestCase.class,
        IncSCCCompleteGraphTestCase.class,
        IncSCCPathConstructionTestCase.class
})
public class TransitiveClosureAlgorithmTestSuite {

}
