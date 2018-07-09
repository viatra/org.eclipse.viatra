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

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.patternlanguage.emf.ui.tests.EMFPatternLanguageUiInjectorProvider;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.ui.testing.AbstractContentAssistTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(EMFPatternLanguageUiInjectorProvider.class)
public class TypeReferenceTest extends AbstractContentAssistTest {

    private static final String epackageImport = String.format("import \"%s\"%n", EcorePackage.eNS_URI);
    private static final String header = "pattern test() {\n";

    @Test
    public void testSimpleTargetAssist() throws Exception {
        newBuilder()
          .append(epackageImport)
          .append(header)
          .append("EClassifier.")
          .applyProposal("name")
          .expectContent(epackageImport + header + "EClassifier.name");
    }
    
    @Test
    public void testChainTargetAssist() throws Exception {
        newBuilder()
        .append(epackageImport)
        .append(header)
        .append("EGenericType.eClassifier.")
        .applyProposal("name")
        .expectContent(epackageImport + header + "EGenericType.eClassifier.name");
    }
    
    @Test
    public void testChainTargetAssistWithPrefix() throws Exception {
        newBuilder()
        .append(epackageImport)
        .append(header)
        .append("EGenericType.eClassifier.n")
        .applyProposal("name")
        .expectContent(epackageImport + header + "EGenericType.eClassifier.name");
    }

}
