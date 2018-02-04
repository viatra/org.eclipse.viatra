/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.whitelist;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.viatra.query.patternlanguage.emf.tests.DummyClass;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.IPureElementProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist.PureElement;

public class TestWhitelistProvider implements IPureElementProvider {

    @Override
    public Collection<PureElement> getPureElements() {
        return Arrays.asList(
                pureClass(DummyClass.class),
                new PureElement("package.Class.method()", PureElement.Type.METHOD)
        );
    }

}
