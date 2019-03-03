/*******************************************************************************
 * Copyright (c) 2017, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguagePreferenceConstants;
import org.eclipse.viatra.query.tooling.core.generator.ViatraQueryGeneratorPlugin;
import org.eclipse.viatra.query.tooling.core.preferences.ToolingCorePreferenceConstants;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class ToolingCorePreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    /**
     * NOTE: update {@link org.eclipse.viatra.documentation.help\src\main\asciidoc\tooling\preferences.adoc} after any changes
     */
    private static final String DISABLE_TARGET_PLATFORM_UPDATE_DESCRIPTION = "By default, VIATRA Query will always "
            + "update the state of the target platform metamodel index automatically. This index is used by the query "
            + "editor and code generator to load Ecore models and refer to their elements (such as EClasses or features). "
            + "If you have large number of workspace plugins, setting this feature can improve editor and generator "
            + "performance. If set, you can force an update by unsetting it, applying preference changes, "
            + "then setting again. Note that you may have to reopen Query Editors after changes in this preference "
            + "to remove all markers.";

    public ToolingCorePreferencePage() {
        super(GRID);
        setPreferenceStore(ViatraQueryGeneratorPlugin.INSTANCE.getPreferenceStore());
        setDescription(null);
    }
    
    /**
     * Creates the field editors. Field editors are abstractions of
     * the common GUI blocks needed to manipulate various types
     * of preferences. Each field editor knows how to save and
     * restore itself.
     */
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        addField(
                new BooleanFieldEditor(
                        EMFPatternLanguagePreferenceConstants.P_ENABLE_VQL_CODEMINING,
                        "&[Experimental] Enable &Code Mining in Query Editor",
                        parent));
        
        Label traverseSubPatternCallDescriptionLabel = new Label(parent, SWT.NONE | SWT.WRAP);
        traverseSubPatternCallDescriptionLabel.setText(DISABLE_TARGET_PLATFORM_UPDATE_DESCRIPTION);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.widthHint = 200;
        traverseSubPatternCallDescriptionLabel.setLayoutData(layoutData);
        
        addField(
            new BooleanFieldEditor(
                ToolingCorePreferenceConstants.P_DISABLE_TARGET_PLATFORM_METAMODEL_INDEX_UPDATE,
                "&[Experimental] Disable automatic update of target platform metamodels",
                parent));
    }

    public void init(IWorkbench workbench) {
    }
    
}