/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.tooling.ui.views;

import java.util.Collection;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.viewers.runtime.model.ViewersRuntimeModelUtil;
import org.eclipse.incquery.viewers.tooling.ui.views.ViewersMultiSandboxViewComponent.ComponentConfiguration;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * @author istvanrath
 * 
 * TODO
 *  - checkstatelistener, checkstateprovider
 *  - handle apply button
 *
 */
public class ViewersMultiSandoxViewComponentSettings {

	private CheckboxTreeViewer activatedQueriesList;
	
	private ViewersMultiSandboxViewComponent owner;
	
	public ViewersMultiSandoxViewComponentSettings(ViewersMultiSandboxViewComponent c) {
		this.owner = c;
	}
	
	public void configurationChanged(ComponentConfiguration c) {
		// TODO
		activatedQueriesList.setInput(c.patterns);
	}
	
	public void createUI() {
		CTabItem settingsTab = new CTabItem(owner.folder, SWT.NONE);
	    settingsTab.setText("Settings");
	    
	    Composite sTabComposite = new Composite(owner.folder, SWT.NONE);
	    settingsTab.setControl(sTabComposite);
	    sTabComposite.setLayout(new GridLayout(1, false));
	    
		// pattern list
	    Group patternListGroup = new Group(sTabComposite, SWT.BORDER);
	    patternListGroup.setLayout(new FillLayout());
	    patternListGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    patternListGroup.setText("Activated queries");
	    activatedQueriesList = new CheckboxTreeViewer(patternListGroup, SWT.BORDER | SWT.CHECK);
	    activatedQueriesList.setLabelProvider(new LabelProvider(){
	    	@Override
	    	public String getText(Object element) {
	    		if (element instanceof Pattern) {
	    			Pattern p = (Pattern) element;
	    			if (ViewersRuntimeModelUtil.isItemPattern(p)) {
	    				return "Item : " + CorePatternLanguageHelper.getFullyQualifiedName(p);
	    			}
	    			else if (ViewersRuntimeModelUtil.isEdgePattern(p)) {
	    				return "Edge : " + CorePatternLanguageHelper.getFullyQualifiedName(p);
	    			}
	    			else if (ViewersRuntimeModelUtil.isContainmentPattern(p)) {
	    				return "Containment : " + CorePatternLanguageHelper.getFullyQualifiedName(p);
	    			}
	    		}
	    		return super.getText(element);
	    	}
	    });
	    activatedQueriesList.setContentProvider(new ITreeContentProvider() {
			
	    	@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			@Override
			public void dispose() {}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((Collection<?>)inputElement).toArray();
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	    activatedQueriesList.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    
	    // TODO other settings (e.g. filters etc)
	    
	    Button applyButton = new Button(sTabComposite, SWT.PUSH);
	    applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	    applyButton.setBounds(0, 0, 94, 28);
	    applyButton.setText("Apply");
	}
	
}
