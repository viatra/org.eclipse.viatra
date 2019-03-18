/*******************************************************************************
 * Copyright (c) 2010-2019, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.internal;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguagePreferenceConstants;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;

public class EMFPatternLanguageEditorPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public EMFPatternLanguageEditorPreferencePage() {
        super(GRID);
        setPreferenceStore(EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore());
        setDescription(null);
    }

    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        addField(new BooleanFieldEditor(EMFPatternLanguagePreferenceConstants.P_ENABLE_VQL_CODEMINING,
                "&[Experimental] Enable &Code Mining in Query Editor", parent));
    }

    public void init(IWorkbench workbench) {
    }

}
