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
package org.eclipse.viatra.addon.viewers.tooling.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.viatra.addon.viewers.runtime.extensions.SelectionHelper;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.addon.viewers.runtime.model.ViatraViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewersAnnotatedPatternTester;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs.IViewerSandboxTab;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.preference.PreferenceConstants;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A component for the {@link ViewersMultiSandboxView}.
 * 
 * Responsible for hosting plugin-contributed {@link IViewerSandboxTab}s,
 * and delegating business logic calls to them.
 * 
 * Maintains a common {@link ViewerState} that is shared for each tab.
 * 
 * It is initialized using a {@link ViewersComponentConfiguration} that is
 * managed in a separate, special tab supported by a {@link ViewersMultiSandoxViewComponentSettings}.
 * 
 * 
 * 
 * @author istvanrath
 *
 */
public class ViewersMultiSandboxViewComponent implements ISelectionProvider {

	private List<IViewerSandboxTab> tabList;
	CTabFolder folder;
	private AdvancedViatraQueryEngine engine;
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
			// initialize our tricky listener to punch through unwrapped selections in a 2nd round
			tab.addSelectionChangedListener(selectionHelper.trickyListener);
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
			tab.removeSelectionChangedListener(selectionHelper.trickyListener);
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
        List<IContributionItem> r = new ArrayList<IContributionItem>();
        if (tab!=null && tab.getDropDownMenuContributions()!=null) {
            r.addAll(tab.getDropDownMenuContributions());
        }
        return r;
    }
    
    private List<IContributionItem> getToolbarContributions(IViewerSandboxTab tab) {
        List<IContributionItem> r = new ArrayList<IContributionItem>();
        if (tab!=null && tab.getToolBarContributions()!=null) {
            r.addAll(tab.getToolBarContributions());
        }   
        return r;
    }
    
    ViewersComponentConfiguration initialConfiguration;
    
    // this is called by the settings tab
    void applyConfiguration(ViewersComponentConfiguration c) {
    	try {
			doSetContents(c.getScope(), c.getPatterns(), c.getFilter());
		} catch (ViatraQueryException e) {
			ViewersMultiSandboxView.log("applyConfiguration", e);
		}
    }
    
    public void initializeContents(ViewersComponentConfiguration c) throws ViatraQueryException {
    	if (c!=null) {
    		initializeContents(c.getScope(), c.getPatterns(), c.getFilter());
    	}
    }

	public void initializeContents(EMFScope model, Collection<IQuerySpecification<?>> _patterns, ViewerDataFilter filter)
            throws ViatraQueryException {
        if (model != null) {
        	Collection<IQuerySpecification<?>> patterns = getPatternsWithProperAnnotations(_patterns);
        	this.initialConfiguration = new ViewersComponentConfiguration(model,patterns,filter);
        	doSetContents(model, patterns, filter);
            settings.initialConfigurationChanged(this.initialConfiguration);
        }
    }

	private void doSetContents(EMFScope scope, Collection<IQuerySpecification<?>> patterns, ViewerDataFilter filter) throws ViatraQueryException {
		if (state!=null) {
    		// dispose any previous viewerstate
    		state.dispose();
    	}
        state = ViatraViewerDataModel.newViewerState(getEngine(scope), getPatternsWithProperAnnotations(patterns), filter, ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
        for (IViewerSandboxTab tab : tabList) {
            tab.bindState(state);
        }
	}
	
	
    private AdvancedViatraQueryEngine getEngine(EMFScope scope) throws ViatraQueryException {
        if (engine != null) {
            engine.dispose();
        }
        // make sure that the engine is initialized according to how the Query Explorer is set up through preferences
        boolean wildcardMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.WILDCARD_MODE);
        boolean dynamicEMFMode = ViatraQueryGUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(PreferenceConstants.DYNAMIC_EMF_MODE);
        
        engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(scope.getScopeRoots(),
                new BaseIndexOptions().withDynamicEMFMode(dynamicEMFMode).withWildcardMode(wildcardMode)));
        ViewersMultiSandboxView.log("Viewers initialized a new VIATRA Query engine with wildcardMode: "+wildcardMode+", dynamicMode: "+dynamicEMFMode);
        return engine;
    }

    private static Collection<IQuerySpecification<?>> getPatternsWithProperAnnotations(Collection<IQuerySpecification<?>> input) {
        List<IQuerySpecification<?>> res = Lists.newArrayList();
        for (IQuerySpecification<?> p : input) {
            if (Iterables.any(p.getAllAnnotations(), new ViewersAnnotatedPatternTester())) {
                res.add(p);
            }
        }
        return res;
    }
	
	
	@Override
    public void setSelection(ISelection selection) {
        for (IViewerSandboxTab tab : tabList) {
        	// unwrap for forward selection synchronization
            tab.setSelection(selectionHelper.unwrapElements_EObjectsToViewersElements(selection, state));
        }
    }


    @Override
    public ISelection getSelection() {
       	IViewerSandboxTab tab = getCurrentContributedTab();
       	if (tab!=null) {
            // unwrap VIATRA viewers model elements to EObjects
            return selectionHelper.unwrapElements_ViewersElementsToEObjects( getCurrentContributedTab().getSelection() );
        } else {
            return StructuredSelection.EMPTY;
        }
    }

    SelectionHelper selectionHelper = new SelectionHelper();

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
    	selectionHelper.selectionChangedListeners.add(listener);
        for (IViewerSandboxTab tab : tabList) {
            tab.addSelectionChangedListener(listener);
        }
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    	selectionHelper.selectionChangedListeners.remove(listener);
        for (IViewerSandboxTab tab : tabList) {
            tab.removeSelectionChangedListener(listener);
        }
    }
    
}
