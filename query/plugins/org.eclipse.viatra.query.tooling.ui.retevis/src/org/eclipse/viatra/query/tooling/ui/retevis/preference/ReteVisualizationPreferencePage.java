/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.retevis.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;

/**
 * @author Abel Hegedus
 *
 */
public class ReteVisualizationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String DISPLAY_CALLED_NETWORKS_MODE_DESCRIPTION = "When enabled the Rete visualization will "
            + "show the Rete network of called patterns (find constraints) in addition to the Rete network of selected patterns.";
    
    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        final IPreferenceStore store = EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore();
        Composite control = new Composite(parent, SWT.NONE);
        
        Label traverseSubPatternCallDescriptionLabel = new Label(control, SWT.NONE | SWT.WRAP);
        traverseSubPatternCallDescriptionLabel.setText(DISPLAY_CALLED_NETWORKS_MODE_DESCRIPTION);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.widthHint = 200;
        traverseSubPatternCallDescriptionLabel.setLayoutData(layoutData);
        final BooleanFieldEditor traverseSubpatternCallModeEditor = new BooleanFieldEditor(ReteVisualizationPreferenceConstants.DISPLAY_CALLED_NETWORKS_MODE,
                "&Display networks for called patterns", control);
        traverseSubpatternCallModeEditor.setPreferenceStore(EMFPatternLanguageUIPlugin.getInstance().getPreferenceStore());
        traverseSubpatternCallModeEditor.load();
        traverseSubpatternCallModeEditor.setPropertyChangeListener(event -> store.setValue(ReteVisualizationPreferenceConstants.DISPLAY_CALLED_NETWORKS_MODE, traverseSubpatternCallModeEditor.getBooleanValue()));
        
        return control;
    }

}
