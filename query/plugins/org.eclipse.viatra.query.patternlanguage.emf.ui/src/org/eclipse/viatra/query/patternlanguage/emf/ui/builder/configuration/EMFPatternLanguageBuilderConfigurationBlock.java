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
package org.eclipse.viatra.query.patternlanguage.emf.ui.builder.configuration;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.xbase.ui.builder.XbaseBuilderConfigurationBlock;

/**
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageBuilderConfigurationBlock extends XbaseBuilderConfigurationBlock {

    @Override
    protected void createGeneralSectionItems(Composite composite) {
        super.createGeneralSectionItems(composite);

        addCheckBox(composite, "Update MANIFEST.MF file during code generation",
                EMFPatternLanguageBuilderPreferenceAccess.PREF_GENERATE_MANIFEST_ENTRIES, BOOLEAN_VALUES, 0);
        addCheckBox(composite, "Generate query specification extensions during code generation",
                EMFPatternLanguageBuilderPreferenceAccess.PREF_GENERATE_ECLIPSE_EXTENSIONS, BOOLEAN_VALUES, 0);
    }

}
