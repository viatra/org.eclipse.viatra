/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *   Zoltan Ujhelyi - viewer tab extension support
 *******************************************************************************/

package org.eclipse.incquery.viewers.tooling.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.model.ViewersAnnotatedPatternTester;
import org.eclipse.incquery.viewers.tooling.ui.Activator;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Implementation of the Viewer Sandbox view. It supports displaying models based on the
 * {@value #SANDBOX_TAB_EXTENSION_ID} extension implementations. Selection related requests are forwarded to the tabs.
 * 
 */
public class ViewerSandboxView extends ViewPart implements ISelectionProvider {

    public static final String ID = "org.eclipse.incquery.viewers.tooling.ui.sandbox";
    public static final String SANDBOX_TAB_EXTENSION_ID = "org.eclipse.incquery.viewers.tooling.ui.viewersandboxtab";

    private List<IViewerSandboxTab> tabList;
    private CTabFolder folder;
    private AdvancedIncQueryEngine engine;
	private ViewerState state;

    public static ViewerSandboxView getInstance() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
            return (ViewerSandboxView) activeWorkbenchWindow.getActivePage().findView(ID);
        }
        return null;
    }

    public void initializeTabList() {
        tabList = Lists.newArrayList();
        IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(
                SANDBOX_TAB_EXTENSION_ID);
        for (IConfigurationElement provider : providers) {
            IViewerSandboxTab tab;
            try {
                tab = (IViewerSandboxTab) provider.createExecutableExtension("implementation");
                tabList.add(tab);
            } catch (CoreException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
            }
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        initializeTabList();

        folder = new CTabFolder(parent, SWT.TOP);

        for (IViewerSandboxTab tab : tabList) {
            tab.createPartControl(folder);
        }

        folder.setSelection(0);
        folder.addSelectionListener(new SelectionListener() { 
            // make sure the contributed menu is refreshed each time the current tab changes
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                fillToolBar(tabList.get(folder.getSelectionIndex()));
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                fillToolBar(tabList.get(folder.getSelectionIndex()));
            }
        });
        //fillToolBar();
        
        getSite().setSelectionProvider(this);
    }

    @Override
    public void setFocus() {
        // TODO implement setFocus correctly
        if (!tabList.isEmpty()) {
            tabList.get(0).setFocus();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter.equals(IPropertySheetPage.class)) {
            PropertySheetPage propertySheetPage = new PropertySheetPage();
            propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(new ComposedAdapterFactory(
                    ComposedAdapterFactory.Descriptor.Registry.INSTANCE)));
            return propertySheetPage;
        }
        return super.getAdapter(adapter);
    }

    // this should be called whenever the active tab changes
    private void fillToolBar(IViewerSandboxTab tab) 
    {
        if (tab!=null) {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            mgr.removeAll();
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
            
            getViewSite().getActionBars().updateActionBars();
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

    @Override
    public void setSelection(ISelection selection) {
        for (IViewerSandboxTab tab : tabList) {
            tab.setSelection(selection);
        }
    }


    @Override
    public ISelection getSelection() {
        if (folder.getSelectionIndex() != -1) {
            return tabList.get(folder.getSelectionIndex()).getSelection();
        } else {
            return StructuredSelection.EMPTY;
        }
    }


    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        for (IViewerSandboxTab tab : tabList) {
            tab.addSelectionChangedListener(listener);
        }
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        for (IViewerSandboxTab tab : tabList) {
            tab.removeSelectionChangedListener(listener);
        }
    }

    public void setContents(ResourceSet resourceSet, Collection<Pattern> patterns, ViewerDataFilter filter)
            throws IncQueryException {
        if (resourceSet != null) {
        	if (state!=null) {
        		// dispose any previous viewerstate
        		state.dispose();
        	}
            state = IncQueryViewerDataModel.newViewerState(resourceSet, getEngine(resourceSet), getPatternsWithProperAnnotations(patterns), filter, ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            for (IViewerSandboxTab tab : tabList) {
                tab.bindState(state);
            }
        }
    }

    private AdvancedIncQueryEngine getEngine(ResourceSet resourceSet) throws IncQueryException {
        if (engine != null) {
            engine.dispose();
        }
        // make sure that the engine is initialized in non-wildcard and dynamic EMF mode
        engine = AdvancedIncQueryEngine.createUnmanagedEngine(resourceSet, false, true);
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
    public void dispose() {
    	for (IViewerSandboxTab tab : tabList) {
            tab.dispose();
        }
    	if (state !=null) {
    		state.dispose();
    	}
        if (engine != null) {
            engine.dispose();
        }
        super.dispose();
    }

}
