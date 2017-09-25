/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.contentassist;

import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageUiInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageUiInjectorProvider.class)
@SuppressWarnings({ "restriction"})
public class VariableReferenceAssist extends AbstractContentAssistTest {

    private final String line1 = String.format("import \"%s\"\n", PatternLanguagePackage.eNS_URI);
    private final String line2 = "pattern util(p : Pattern) {Pattern (p);}\n";
    private final String line3 = "pattern test(p : Pattern) {\n";
    private final String line4_preassist = "  find util(";
    private final String line4_postassist = "  find util(p";

    @Test
    public void testEPackageImport() throws Exception {
        newBuilder()
          .append(line1)
          .append(line2)
          .append(line3)
          .append(line4_preassist)
          .applyProposal("p")
          .expectContent(line1 + line2 + line3 + line4_postassist);
    }

}
