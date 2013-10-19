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
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.model.ViewersAnnotatedPatternTester;
import org.eclipse.incquery.viewers.tooling.ui.views.tabs.IViewerSandboxTab;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ViewersMultiSandboxViewComponent implements ISelectionProvider {

	private List<IViewerSandboxTab> tabList;
	CTabFolder folder;
	private AdvancedIncQueryEngine engine;
	private ViewerState state;
	private ViewersMultiSandboxView host;
	private ViewersMultiSandoxViewComponentSettings settings;
	
	public ViewersMultiSandboxViewComponent(ViewersMultiSandboxView v) {
		this.host = v;
		createPartControl(host.container);
	}

	public void initializeTabList() {
		tabList = Lists.newArrayList();
		IConfigurationElement[] providers = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						ViewersToolingViewsUtil.SANDBOX_TAB_EXTENSION_ID);
		for (IConfigurationElement provider : providers) {
			IViewerSandboxTab tab;
			try {
				tab = (IViewerSandboxTab) provider.createExecutableExtension("implementation");
				tabList.add(tab);
			} catch (CoreException e) {
				ViewersMultiSandboxView.log("initializeTabList",e);
			}
		}
	}

	private void createSettingsTab() {
		this.settings = new ViewersMultiSandoxViewComponentSettings(this);
		this.settings.createUI();
	}
	
	
	private void createPartControl(Composite parent) {
		initializeTabList();
		
		folder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		folder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		// create settings tab
		createSettingsTab();
				
		for (IViewerSandboxTab tab : tabList) {
			tab.createPartControl(folder);
		}

		folder.setSelection(0);
		folder.addSelectionListener(new SelectionListener() {
			// make sure the contributed menu is refreshed each time the current
			// tab changes

			@Override
			public void widgetSelected(SelectionEvent e) {
				host.fillToolBar(ViewersMultiSandboxViewComponent.this);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				host.fillToolBar(ViewersMultiSandboxViewComponent.this);
			}
		});
		
		
		folder.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				host.setCurrentComponent(ViewersMultiSandboxViewComponent.this);
			}
			
			@Override
			public void mouseDown(MouseEvent e) { }
			
			@Override
			public void mouseDoubleClick(MouseEvent e) { }
		});
		
		setBackGround();
		
	}

	public void dispose() {
		
		for (IViewerSandboxTab tab : tabList) {
			tab.dispose();
		}
		if (state != null) {
			state.dispose();
		}
		if (engine != null) {
			engine.dispose();
		}
		
		if (!folder.isDisposed()) {
			folder.dispose();
		}

	}
	
	
	public void setFocus() {
        if (!tabList.isEmpty()) {
            getCurrentTabItem().getControl().setFocus();
        }
	}

	void setForeground() {
		Color bgColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		folder.setBackground(bgColor);
		getCurrentTabItem().getControl().setBackground(bgColor);
	}
	
	void setBackGround() {
		Color bgColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		folder.setBackground(bgColor);
		getCurrentTabItem().getControl().setBackground(bgColor);
	}
	
	private IViewSite getViewSite() {
		return host.getViewSite();
	}
	
	private IViewerSandboxTab getCurrentContributedTab() {
		if (folder.getSelectionIndex()<=0) return null;
		else return tabList.get(folder.getSelectionIndex()-1);
	}
	
	private CTabItem getCurrentTabItem() {
		return folder.getSelection();
	}
	
	// this should be called whenever the active tab changes
    void fillToolBarBasedOnCurrentTab() 
    {
    	IViewerSandboxTab tab = getCurrentContributedTab();
        if (tab!=null) {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            //mgr.removeAll(); // this is moved to ViewersMultiSandboxView.fillToolBar
            for (IContributionItem item : getToolbarContributions(tab)) {
                if (item instanceof MenuManager) {
                    for (IContributionItem _item : ((MenuManager)item).getItems()) {
                        mgr.add(_item);
                    }
                }
                else {
                    mgr.add(item);
                }
            }
            mgr.update(true);
            
            IMenuManager mmgr = getViewSite().getActionBars().getMenuManager();
            mmgr.removeAll();
            for (IContributionItem item : getDropdownMenuContributions(tab)) {
                mmgr.add(item);
            }
            mmgr.updateAll(true);
            
            // getViewSite().getActionBars().updateActionBars(); // this is moved to ViewersMultiSandboxView.fillToolBar
        }
    }
	
	private List<IContributionItem> getDropdownMenuContributions(IViewerSandboxTab tab) {
        ArrayList<IContributionItem> r = new ArrayList<IContributionItem>();
        if (tab!=null && tab.getDropDownMenuContributions()!=null) {
            r.addAll(tab.getDropDownMenuContributions());
        }
        return r;
    }
    
    private List<IContributionItem> getToolbarContributions(IViewerSandboxTab tab) {
        ArrayList<IContributionItem> r = new ArrayList<IContributionItem>();
        if (tab!=null && tab.getToolBarContributions()!=null) {
            r.addAll(tab.getToolBarContributions());
        }   
        return r;
    }
    
    public class ComponentConfiguration
    {
    	Notifier model;
    	Collection<Pattern> patterns;
    	ViewerDataFilter filter;
    	public ComponentConfiguration(Notifier _model, Collection<Pattern> _patterns, ViewerDataFilter _filter) {
    		this.model = _model;
    		this.patterns = _patterns;
    		this.filter = _filter;
		}
    }
    
    ComponentConfiguration configuration;
    
    public void setContents(ComponentConfiguration c) throws IncQueryException {
    	if (c!=null) {
    		setContents(c.model, c.patterns, c.filter);
    	}
    }

	public void setContents(Notifier model, Collection<Pattern> patterns, ViewerDataFilter filter)
            throws IncQueryException {
        if (model != null) {
        	if (state!=null) {
        		// dispose any previous viewerstate
        		state.dispose();
        	}
        	this.configuration = new ComponentConfiguration(model,patterns,filter);
            state = IncQueryViewerDataModel.newViewerState(getEngine(model), getPatternsWithProperAnnotations(patterns), filter, ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            for (IViewerSandboxTab tab : tabList) {
                tab.bindState(state);
            }
            settings.configurationChanged(this.configuration);
        }
    }

    private AdvancedIncQueryEngine getEngine(Notifier model) throws IncQueryException {
        if (engine != null) {
            engine.dispose();
        }
        // make sure that the engine is initialized in non-wildcard and dynamic EMF mode
        engine = AdvancedIncQueryEngine.createUnmanagedEngine(model, false, true);
        return engine;
    }

    private static Collection<Pattern> getPatternsWithProperAnnotations(Collection<Pattern> input) {
        ArrayList<Pattern> res = new ArrayList<Pattern>();
        for (Pattern p : input) {
            if (Iterables.any(p.getAnnotations(), new ViewersAnnotatedPatternTester())) {
                res.add(p);
            }
        }
        return res;
    }
	
	
	@Override
    public void setSelection(ISelection selection) {
        for (IViewerSandboxTab tab : tabList) {
            tab.setSelection(selection);
        }
    }


    @Override
    public ISelection getSelection() {
       	IViewerSandboxTab tab = getCurrentContributedTab();
       	if (tab!=null) {
            return getCurrentContributedTab().getSelection();
        } else {
            return StructuredSelection.EMPTY;
        }
    }

    Set<ISelectionChangedListener> selectionChangedListeners = Sets.newHashSet();


    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
    	selectionChangedListeners.add(listener);
        for (IViewerSandboxTab tab : tabList) {
            tab.addSelectionChangedListener(listener);
        }
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    	selectionChangedListeners.remove(listener);
        for (IViewerSandboxTab tab : tabList) {
            tab.removeSelectionChangedListener(listener);
        }
    }
	
    Collection<ISelectionChangedListener> getSelectionChangedListeners() {
    	 return selectionChangedListeners;
    }
    
}
