/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.contentassist;

import org.eclipse.viatra.query.patternlanguage.emf.ui.tests.EMFPatternLanguageUiInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.ui.testing.AbstractContentAssistTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageUiInjectorProvider.class)
public class ImportAssistTest extends AbstractContentAssistTest {


    @Test
    public void testEPackageImport() throws Exception {
        newBuilder()
          .applyProposal("import")
          .append(" ")
          .append("\"\"")
          .cursorBack(1)
          .applyProposal("\"" + PatternLanguagePackage.eNS_URI)
          .expectContent(String.format("import \"%s\"", PatternLanguagePackage.eNS_URI));
    }
    
    @Test
    public void testEPackageImportWithMissingEndApostrophe() throws Exception {
        newBuilder()
        .applyProposal("import")
        .append(" ")
        .append("\"")
        .applyProposal("\"" + PatternLanguagePackage.eNS_URI + "\"")
        .expectContent(String.format("import \"%s\"", PatternLanguagePackage.eNS_URI));
    }
    


}
