/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.extensions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.IPureElementProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist.PureElement;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class OtherLibraryWhitelistProvider implements IPureElementProvider {

    @Override
    public Collection<PureElement> getPureElements() {
        return Arrays.asList(
                pureClass(BigDecimal.class)
        );
    }

}
