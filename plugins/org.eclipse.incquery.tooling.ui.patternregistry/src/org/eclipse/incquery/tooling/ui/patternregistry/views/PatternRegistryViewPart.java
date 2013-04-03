/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.views;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.incquery.runtime.patternregistry.IPatternRegistryListener;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

public class PatternRegistryViewPart extends ViewPart {

    private CheckboxTreeViewer checkboxTreeViewer;

    @Inject
    public PatternRegistryViewPart(IResourceSetProvider resourceSetProvider) {
        super();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceChangeListener(resourceSetProvider),
                IResourceChangeEvent.PRE_BUILD);
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void createPartControl(Composite parent) {
        checkboxTreeViewer = new CheckboxTreeViewer(parent);
        checkboxTreeViewer.setContentProvider(new PatternRegistryTreeContentProvider());
        checkboxTreeViewer.setLabelProvider(new PatternRegistryTreeLabelProvider());
        checkboxTreeViewer.setCheckStateProvider(new ICheckStateProvider() {
            @Override
            public boolean isChecked(Object element) {
                if (element instanceof IPatternInfo) {
                    IPatternInfo patternInfo = (IPatternInfo) element;
                    return patternInfo.isActive();
                } else {
                    return false;
                }
            }

            @Override
            public boolean isGrayed(Object element) {
                return false;
            }
        });

        checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();
                if (element instanceof IPatternInfo) {
                    IPatternInfo patternInfo = (IPatternInfo) element;
                    patternInfo.setActive(!patternInfo.isActive());
                }
            }
        });

        checkboxTreeViewer.setInput("unused_input");

        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(checkboxTreeViewer.getControl());
        checkboxTreeViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, checkboxTreeViewer);
        getSite().setSelectionProvider(checkboxTreeViewer);

        updateCheckboxTreeViewer();
        PatternRegistry.INSTANCE.registerListener(new IPatternRegistryListener() {
            @Override
            public void patternRemoved(IPatternInfo patternInfo) {
                updateCheckboxTreeViewer();
            }

            @Override
            public void patternAdded(IPatternInfo patternInfo) {
                updateCheckboxTreeViewer();
            }

            @Override
            public void patternActivated(IPatternInfo patternInfo) {
                updateCheckboxTreeViewer();
            }

            @Override
            public void patternDeactivated(IPatternInfo patternInfo) {
                updateCheckboxTreeViewer();
            }
        });
    }

    private void updateCheckboxTreeViewer() {
        Display display = checkboxTreeViewer.getTree().getDisplay();
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                checkboxTreeViewer.refresh();
            }
        });
    }

}
