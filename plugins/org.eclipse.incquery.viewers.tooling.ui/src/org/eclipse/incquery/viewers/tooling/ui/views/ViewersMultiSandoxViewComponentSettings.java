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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.viewers.runtime.ViewersRuntimePlugin;
import org.eclipse.incquery.viewers.runtime.model.ViewersRuntimeModelUtil;
import org.eclipse.incquery.viewers.tooling.ui.views.ViewersMultiSandboxViewComponent.ComponentConfiguration;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author istvanrath
 * 
 *
 */
public class ViewersMultiSandoxViewComponentSettings {

	private CheckboxTreeViewer activatedPatternsViewer;
	
	private ViewersMultiSandboxViewComponent owner;
	
	private Map<Pattern, Boolean> checkedPatterns = Maps.newHashMap();
	
	private ComponentConfiguration currentConfiguration;
	
	public ViewersMultiSandoxViewComponentSettings(ViewersMultiSandboxViewComponent c) {
		this.owner = c;
	}
	
	// this is called when the multi sandbox view component is initialized by the user
	public void initialConfigurationChanged(ComponentConfiguration c) {
		this.currentConfiguration = newCopy(c);
		this.checkedPatterns.clear();
		for (Pattern p : this.currentConfiguration.patterns) {
			this.checkedPatterns.put(p,	true);
		}
		this.activatedPatternsViewer.setInput(this.checkedPatterns.keySet());
	}
	
	private ComponentConfiguration newCopy(ComponentConfiguration c) {
		ArrayList<Pattern> r = Lists.newArrayList();
		r.addAll(c.patterns);
		// TODO proper copy support for filters
		return owner.new ComponentConfiguration(c.model, r, c.filter);
	}

	private void applyConfiguration() {
		owner.applyConfiguration(currentConfiguration);
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
	    activatedPatternsViewer = new CheckboxTreeViewer(patternListGroup, SWT.BORDER | SWT.CHECK);
	    activatedPatternsViewer.setLabelProvider(new PatternListLabelProvider());
	    activatedPatternsViewer.setContentProvider(new PatternListContentProvider());
	    activatedPatternsViewer.addCheckStateListener(new PatternListCheckStateListener());
	    activatedPatternsViewer.setCheckStateProvider(new PatternListCheckStateProvider());
	    activatedPatternsViewer.setComparator(new PatternListComparator());
	    
	    
	    // TODO other settings 
	    // filters
	    // switch between resource and resourceset mode (?)
	    // dynamic, wildcard mode switches
	    
	    Button applyButton = new Button(sTabComposite, SWT.PUSH);
	    applyButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	    applyButton.setBounds(0, 0, 94, 28);
	    applyButton.setText("Apply");
	    applyButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyConfiguration();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				applyConfiguration();
			}
		});
	}
	

	
	class PatternListCheckStateProvider implements ICheckStateProvider {

		@Override
		public boolean isChecked(Object element) {
			return checkedPatterns.get(element);
		}

		@Override
		public boolean isGrayed(Object element) {
			// patterns are never grayed in the pattern list
			return false;
		}
		
	}
	
	class PatternListCheckStateListener implements ICheckStateListener {

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			checkedPatterns.put((Pattern) event.getElement(),event.getChecked());
			if (event.getChecked()) {
				currentConfiguration.patterns.add( ((Pattern)event.getElement()) );
			}
			else {
				currentConfiguration.patterns.remove( event.getElement() );
			}
		}
		
	}
	
	class PatternListLabelProvider extends LabelProvider {
		private Image itemIcon;
		private Image edgeIcon; 
		private Image contIcon;
		
		{
			itemIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/item.gif").createImage();
			edgeIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/edge.gif").createImage();
			contIcon = ViewersRuntimePlugin.imageDescriptorFromPlugin(ViewersRuntimePlugin.PLUGIN_ID, "icons/containment.gif").createImage();
		}
		
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
		
		@Override
		public Image getImage(Object element) {
			if (element instanceof Pattern) {
    			Pattern p = (Pattern) element;
    			if (ViewersRuntimeModelUtil.isItemPattern(p)) {
    				return itemIcon;
    			}
    			else if (ViewersRuntimeModelUtil.isEdgePattern(p)) {
    				return edgeIcon;
    			}
    			else if (ViewersRuntimeModelUtil.isContainmentPattern(p)) {
    				return contIcon;
    			}
    		}
			return super.getImage(element);
		}
		
		@Override
		public void dispose() {
			itemIcon.dispose();
	    	edgeIcon.dispose();
	    	contIcon.dispose();
			super.dispose();
		}
	}
	
	class PatternListComparator extends ViewerComparator {
		@Override
    	public int category(Object element) {
			if (element instanceof Pattern) {
    			Pattern p = (Pattern) element;
    			if (ViewersRuntimeModelUtil.isItemPattern(p)) {
    				return 0;
    			}
    			else if (ViewersRuntimeModelUtil.isEdgePattern(p)) {
    				return 1;
    			}
    			else if (ViewersRuntimeModelUtil.isContainmentPattern(p)) {
    				return 2;
    			}
    		}
    		return super.category(element);
    	}

	}
	
	class PatternListContentProvider implements ITreeContentProvider {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		
		@Override
		public void dispose() {}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return ((Collection<?>)inputElement).toArray();
			//return checkedPatterns.keySet().toArray();
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
			return null;
		}
	}
	
}
