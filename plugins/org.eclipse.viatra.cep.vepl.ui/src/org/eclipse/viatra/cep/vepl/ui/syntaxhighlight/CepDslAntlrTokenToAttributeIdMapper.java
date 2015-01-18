/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.ui.syntaxhighlight;

import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;

import com.google.common.base.Joiner;

public class CepDslAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper {

    @Override
    protected String calculateId(String tokenName, int tokenType) {
        String calculateId = super.calculateId(tokenName, tokenType);

        if (getApostrophedKeyword(QueryResultChangeType.NEW_MATCH_FOUND.getLiteral()).equals(tokenName)
                || getApostrophedKeyword(QueryResultChangeType.EXISTING_MATCH_LOST.getLiteral()).equals(tokenName)) {
            return CepDslHighlightingConfiguration.EDL_ENUM_ID;
        }

        return calculateId;

    }

    private String getApostrophedKeyword(String keyword) {
        final String APOSTROPHE = "'";
        Joiner joiner = Joiner.on("");
        return joiner.join(APOSTROPHE, keyword, APOSTROPHE);
    }
}
