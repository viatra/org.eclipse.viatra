/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.ui.handlers;

import org.eclipse.jdt.internal.ui.compare.ResizableDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;

import com.google.inject.Injector;

@SuppressWarnings("restriction")
public class ConditionalBreakpointDialog extends ResizableDialog{
    private Injector injector;
    private static final String TITLE = "Define VIATRA Transformation Conditional Breakpoint Query";
    private EmbeddedEditorModelAccess model; 
    private String result;

    protected ConditionalBreakpointDialog(Shell parentShell, Injector injector) {
        super(parentShell, null);
        this.injector = injector;
    }
    
    
    @Override
    protected Control createContents(Composite parent) {
        
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);
        // initialize the dialog units
        initializeDialogUnits(composite);
        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);
        buttonBar = createButtonBar(composite);
        
        
        
        IEditedResourceProvider provider = injector.getInstance(IEditedResourceProvider.class);
        EmbeddedEditorFactory factory = injector.getInstance(EmbeddedEditorFactory.class);
           
        EmbeddedEditor editor = factory.newEditor(provider).withParent((Composite) dialogArea);
        model = editor.createPartialEditor("", "Enter VIATRA Query Here", "", false);
        
        return composite;
    }
    
    @Override
    protected void okPressed() {
        result = model.getEditablePart();
        super.okPressed();
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(TITLE);
        newShell.setImage(ResourceManager.getPluginImage("org.eclipse.viatra.transformation.debug.ui","icons/rsz_viatra_logo.png"));
    }
    
    public String getResults(){
        return result;
    }
    
    
}
