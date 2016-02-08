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

package org.eclipse.viatra.addon.viewers.tooling.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
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
import org.eclipse.viatra.addon.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewersAnnotatedPatternTester;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.addon.viewers.tooling.ui.ViewersToolingPlugin;
import org.eclipse.viatra.addon.viewers.tooling.ui.views.tabs.IViewerSandboxTab;
import org.eclipse.viatra.query.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Implementation of the Viewer Sandbox view. It supports displaying models based on the
 * {@value ViewersToolingViewsUtil#SANDBOX_TAB_EXTENSION_ID} extension implementations. Selection related requests are forwarded to the tabs.
 * 
 * @deprecated superseded by {@link ViewersMultiSandboxView}
 */
public class ViewersSandboxView extends ViewPart implements ISelectionProvider {

    public static final String ID = "org.eclipse.viatra.addon.viewers.tooling.ui.sandbox";
    
    
    private List<IViewerSandboxTab> tabList;
    private CTabFolder folder;
    private AdvancedIncQueryEngine engine;
	private ViewerState state;

    public static ViewersSandboxView getInstance() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
            return (ViewersSandboxView) activeWorkbenchWindow.getActivePage().findView(ID);
        }
        return null;
    }

    public void initializeTabList() {
        tabList = Lists.newArrayList();
        IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(
                ViewersToolingViewsUtil.SANDBOX_TAB_EXTENSION_ID);
        for (IConfigurationElement provider : providers) {
            IViewerSandboxTab tab;
            try {
                tab = (IViewerSandboxTab) provider.createExecutableExtension("implementation");
                tabList.add(tab);
            } catch (CoreException e) {
                ViewersToolingPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ViewersToolingPlugin.PLUGIN_ID, e.getLocalizedMessage(), e));
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
        if (!tabList.isEmpty()) {
//            tabList.get(0).setFocus();
            tabList.get(folder.getSelectionIndex()).setFocus();
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

    public void setContents(Notifier model, Collection<IQuerySpecification<?>> patterns, ViewerDataFilter filter)
            throws IncQueryException {
        if (model != null) {
        	if (state!=null) {
        		// dispose any previous viewerstate
        		state.dispose();
        	}
            state = IncQueryViewerDataModel.newViewerState(getEngine(model), getPatternsWithProperAnnotations(patterns), filter, ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
            for (IViewerSandboxTab tab : tabList) {
                tab.bindState(state);
            }
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

    private static Collection<IQuerySpecification<?>> getPatternsWithProperAnnotations(Collection<IQuerySpecification<?>> input) {
        List<IQuerySpecification<?>> res = new ArrayList<IQuerySpecification<?>>();
        for (IQuerySpecification<?> p : input) {
            if (Iterables.any(p.getAllAnnotations(), new ViewersAnnotatedPatternTester())) {
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
