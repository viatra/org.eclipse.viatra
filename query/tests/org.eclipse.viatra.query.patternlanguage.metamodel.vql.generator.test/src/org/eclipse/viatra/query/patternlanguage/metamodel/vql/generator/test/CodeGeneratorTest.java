/**
 * Copyright (c) 2010-2018, Mocsai Krisztain, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.metamodel.vql.generator.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.metamodel.code.generator.VqlCodeGenerator;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.PatternPackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CodeGeneratorTest {

    static final String PROJECT_NAME = "org.eclipse.viatra.query.patternlanguage.metamodel.vql.generator.test";

    static final String TEST_All_CASES = "testAllCases.vgql";
    static final String PATTERN_WITHOUT_BODY = "patternWithoutBody.vgql";
    static final String PATTERN_WITH_PATH_EXPRESSION = "patternWithPathExpression.vgql";
    static final String PATTERN_WITH_STRING_LITERAL = "patternWithStringLiteral.vgql";

    @Parameters(name = "{0}")
    public static List<Object[]> testData() {
        return Arrays.<Object[]> asList(
                new Object[] { TEST_All_CASES, ExpectedOutputs.testAllCases },
                new Object[] { PATTERN_WITHOUT_BODY, ExpectedOutputs.patternWithoutBody },
                new Object[] { PATTERN_WITH_PATH_EXPRESSION, ExpectedOutputs.patternWithPathExpression },
                new Object[] { PATTERN_WITH_STRING_LITERAL, ExpectedOutputs.patternWithStringLiteral });
    }

    @Parameter(0)
    public String fileName;

    @Parameter(1)
    public String expectedOutput;

    @Test
    public void test() {
        Resource vgql = getResource(PROJECT_NAME, fileName);
        VqlCodeGenerator vqlGenerator = new VqlCodeGenerator();
        EObject root = vgql.getContents().get(0);
        String vql = vqlGenerator.generate((PatternPackage) root);

        vql = removeWhitespace(vql);
        String cleanExpectedOutput = removeWhitespace(expectedOutput);

        assertEquals(cleanExpectedOutput, vql);
    }

    private String removeWhitespace(String s) {
        return s.replaceAll("\\s+", "");
    }

    private Resource getResource(String projectName, String vqlModelName) {
        ResourceSet resSet = new ResourceSetImpl();
        String relativePath = projectName + File.separator + vqlModelName;
        URI resourceUri = URI.createPlatformPluginURI(relativePath, true);
        Resource vgql = resSet.getResource(resourceUri, true);
        return vgql;
    }

}
