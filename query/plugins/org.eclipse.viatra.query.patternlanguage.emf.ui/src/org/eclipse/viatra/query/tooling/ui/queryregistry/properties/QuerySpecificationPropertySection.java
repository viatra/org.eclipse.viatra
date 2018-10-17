/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;

/**
 * @author Abel Hegedus
 *
 */
public class QuerySpecificationPropertySection extends AbstractPropertySection {

    private QueryRegistryTreeEntry entry;
    
    private CLabel fqnText;
    private CLabel sourceText;
    private Button loadButton;
    
    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        FormData data;
 
        // FULLY QUALIFIED NAME
        CLabel fqnLabel = factory.createCLabel(composite, "Fully qualified name:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(0, 10);
        fqnLabel.setLayoutData(data);

        fqnText = factory.createCLabel(composite, "");
        data = new FormData();
        data.left = new FormAttachment(fqnLabel, 5, SWT.RIGHT);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(fqnLabel, 0, SWT.CENTER);
        fqnText.setLayoutData(data);
 
        
        // SOURCE IDENTIFIER
        CLabel sourceLabel = factory.createCLabel(composite, "Connector identifier:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(fqnText, 0);
        sourceLabel.setLayoutData(data);
        
        sourceText = factory.createCLabel(composite, "");
        data = new FormData();
        data.left = new FormAttachment(sourceLabel, 5, SWT.RIGHT);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(sourceLabel, 0, SWT.CENTER);
        sourceText.setLayoutData(data);
 
        
        // LOAD BUTTON
        loadButton = factory.createButton(composite, "Load query specification details", SWT.NONE);
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.top = new FormAttachment(sourceText, 0);
        loadButton.setLayoutData(data);
        loadButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(entry.load()){
                    refresh();
                }
            }
        });
    }
    
    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof QueryRegistryTreeEntry);
        this.entry = (QueryRegistryTreeEntry) input;
    }

    @Override
    public void refresh() {
        fqnText.setText(entry.getEntry().getFullyQualifiedName());
        sourceText.setText(entry.getEntry().getSourceIdentifier());
        loadButton.setEnabled(!entry.isLoaded());
    }
}
