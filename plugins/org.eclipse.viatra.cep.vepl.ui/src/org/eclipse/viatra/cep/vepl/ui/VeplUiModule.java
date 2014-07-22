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

package org.eclipse.viatra.cep.vepl.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra.cep.vepl.ui.syntaxhighlight.CepDslAntlrTokenToAttributeIdMapper;
import org.eclipse.viatra.cep.vepl.ui.syntaxhighlight.CepDslHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

/**
 * Use this class to register components to be used within the IDE.
 */
public class VeplUiModule extends org.eclipse.viatra.cep.vepl.ui.AbstractVeplUiModule {
    public VeplUiModule(AbstractUIPlugin plugin) {
        super(plugin);
    }

    public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
        return CepDslHighlightingConfiguration.class;
    }

    public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
        return CepDslAntlrTokenToAttributeIdMapper.class;
    }
}
