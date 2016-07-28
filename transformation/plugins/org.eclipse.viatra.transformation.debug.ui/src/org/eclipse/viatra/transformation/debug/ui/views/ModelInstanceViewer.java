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
package org.eclipse.viatra.transformation.debug.ui.views;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ModelInstanceViewer extends ViewPart implements IDebuggerHostAgentListener {
//TODO Adaptation of this feature is non trivial may require serious changes in the Model Instance Viewer
    
    
//    public static final String ID = "org.eclipse.viatra.transformation.ui.debug.TransformationViewer";
//
//    private CTabFolder tabFolder;
//
//    private Composite composite;
//
//    private AdapterFactoryLabelProvider adapterFactoryLabelProvider;
//
//    private AdapterFactoryContentProvider adapterFactoryContentProvider;
//
//    private Map<CTabItem, TreeViewer> tabMap = Maps.newHashMap();
//
//    private TabbedSelectionProviderWrapper selectionProviderWrapper;
//
//    @Override
//    public void createPartControl(Composite parent) {
////        ReflectiveItemProviderAdapterFactory adapterFactory = new ReflectiveItemProviderAdapterFactory();
////        ISelectionService sService = getSite().getWorkbenchWindow().getSelectionService();
////
////        adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
////        adapterFactoryContentProvider = new AdapterFactoryContentProvider(adapterFactory);
////
////        composite = new Composite(parent, SWT.NONE);
////        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
////
////        tabFolder = new CTabFolder(composite, SWT.BORDER);
////        tabFolder.setBorderVisible(true);
////        tabFolder.setSelectionBackground(
////                Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
////
////        selectionProviderWrapper = new TabbedSelectionProviderWrapper();
////        getSite().setSelectionProvider(selectionProviderWrapper);
////        tabFolder.addSelectionListener(new SelectionListener() {
////
////            @Override
////            public void widgetSelected(SelectionEvent e) {
////                if (e.item instanceof CTabItem) {
////                    selectionProviderWrapper.setActiveProvider(tabMap.get(tabFolder.getSelection()));
////                }
////            }
////
////            @Override
////            public void widgetDefaultSelected(SelectionEvent e) {
////            }
////
////        });
////        
////        ISelectionListener listener = new ISelectionListener() {
////            @Override
////            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
////                if (!selection.isEmpty() && selection instanceof StructuredSelection) {
////                    Object firstElement = ((StructuredSelection) selection).getFirstElement();
////                    try {
////                        if (firstElement instanceof AdaptableEVM) {
////                            TransformationThread thread = TransformationThreadFactory.getInstance()
////                                    .getTransformationThread(((AdaptableEVM) firstElement).getIdentifier());
////                            if (thread != null) {
////                                maintainTabs(getResources(thread.getEngine()));
////                               TransformationThreadFactory.getInstance().unRegisterListener(ModelInstanceViewer.this);
////                               TransformationThreadFactory.getInstance().registerListener(ModelInstanceViewer.this,
////                                        ((AdaptableEVM) firstElement).getIdentifier());
////                            }
////                        }
////                    } catch (Exception e) {
////                        TransformationDebugUIActivator.getDefault().logException(e.getMessage(), e);
////                        ErrorDialog.openError(composite.getShell(), "An error has occured", e.getMessage(), new Status(IStatus.ERROR, TransformationDebugUIActivator.PLUGIN_ID, e.getMessage()));
////                    }
////                }
////
////            }
////        };
////
////        sService.addSelectionListener(AdaptableTransformationBrowser.ID, listener);
//        
//    }
//
//    private void maintainTabs(ResourceSet[] resourceSets) {
//        tabMap.clear();
//        for (CTabItem item : tabFolder.getItems()) {
//            item.getControl().dispose();
//            item.dispose();
//        }
//        for (ResourceSet rs : resourceSets) {
//            EList<Resource> resources = rs.getResources();
//            for (Resource resource : resources) {
//                CTabItem ritem = new CTabItem(tabFolder, SWT.NONE);
//                ritem.setText(resource.getURI().lastSegment());
//
//                TreeViewer treeViewer = new TreeViewer(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.VIRTUAL);
//
//                ritem.setControl(treeViewer.getTree());
//                tabMap.put(ritem, treeViewer);
//
//                treeViewer.setContentProvider(adapterFactoryContentProvider);
//                treeViewer.setLabelProvider(adapterFactoryLabelProvider);
//
//                if (resource.getContents().size() == 1) {
//                    treeViewer.setInput(resource.getContents().get(0));
//                    ritem.setControl(treeViewer.getControl());
//                }
//
//            }
//        }
//        if (tabFolder.getItems().length > 0) {
//            tabFolder.setSelection(tabFolder.getItems()[0]);
//            selectionProviderWrapper.setActiveProvider(tabMap.get(tabFolder.getSelection()));
//        }
//
//    }
//
//    private ResourceSet[] getResources(ViatraQueryEngine engine) {
//        List<ResourceSet> retVal = Lists.newArrayList();
//        QueryScope scope = engine.getScope();
//        if (scope instanceof EMFScope) {
//            for (Notifier notifier : ((EMFScope) scope).getScopeRoots()) {
//                if (notifier instanceof ResourceSet) {
//                    retVal.add((ResourceSet) notifier);
//                }
//            }
//        }
//        return retVal.toArray(new ResourceSet[retVal.size()]);
//    }
//
//    @Override
//    public void setFocus() {
//        tabFolder.setFocus();
//    }
//
//    @Override
//    public void dispose() {
//        selectionProviderWrapper.dispose();
//        super.dispose();
//    }
//
//    @Override
//    public void transformationStateChanged(final TransformationState state, String id) {
//        tabFolder.getDisplay().syncExec(new Runnable() {
//            @Override
//            public void run() {
//                maintainTabs(getResources(state.getEngine()));
//            }
//
//        });
//
//    }
//
//    @Override
//    public void transformationTerminated(TransformationState state, String id) {
//        tabFolder.getDisplay().syncExec(new Runnable() {
//
//            @Override
//            public void run() {
//                for (CTabItem item : tabFolder.getItems()) {
//                    item.getControl().dispose();
//                    item.dispose();
//                }
//            }
//
//        });
//        
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    @Override
//    public Object getAdapter(Class adapter) {
//        if (adapter.equals(IPropertySheetPage.class)) {
//              return getPropertySheetPage();
//        }
//        return super.getAdapter(adapter);
//
//    }
//
//    public IPropertySheetPage getPropertySheetPage() {
//        PropertySheetPage propertySheetPage = new PropertySheetPage();
//        propertySheetPage.setPropertySourceProvider(adapterFactoryContentProvider);
//        return propertySheetPage;
//    }
//    
//    
//    /**
//     * Based on org.eclipse.debug.internal.ui.views.variables.VariablesView
//     * @author Peter Lunk
//     *
//     */
//    private static class TabbedSelectionProviderWrapper implements ISelectionProvider {
//        private final ListenerList fListenerList;
//        
//        private final ISelectionChangedListener fListener;
//        private ISelectionProvider fActiveProvider;
//        
//        private TabbedSelectionProviderWrapper() {
//            fListenerList = new ListenerList(ListenerList.IDENTITY);
//            fListener = new ISelectionChangedListener() {
//                @Override
//                public void selectionChanged(SelectionChangedEvent event) {
//                    fireSelectionChanged(event);
//                }
//            };
//        }
//
//        public void setActiveProvider(ISelectionProvider provider) {
//            if (fActiveProvider != null && (fActiveProvider.equals(provider)  || this.equals(provider))) {
//                return;
//            }
//            if (fActiveProvider != null) {
//                fActiveProvider.removeSelectionChangedListener(fListener);
//            }
//            if (provider != null) {
//                provider.addSelectionChangedListener(fListener);
//            }
//            fActiveProvider = provider;
//            fireSelectionChanged(new SelectionChangedEvent(this, getSelection()));
//        }
//
//        private void dispose() {
//            fListenerList.clear();
//            setActiveProvider(null);
//        }
//
//        private void fireSelectionChanged(SelectionChangedEvent event) {
//            Object[] listeners = fListenerList.getListeners();
//            for (int i = 0; i < listeners.length; i++) {
//                ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
//                listener.selectionChanged(event);
//            }
//        }
//
//        @Override
//        public void addSelectionChangedListener(ISelectionChangedListener listener) {
//            fListenerList.add(listener);            
//        }
//
//        @Override
//        public ISelection getSelection() {
//            if (fActiveProvider != null) {
//                return fActiveProvider.getSelection();
//            }
//            return StructuredSelection.EMPTY;
//        }
//
//        @Override
//        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
//            fListenerList.remove(listener);
//        }
//
//        @Override
//        public void setSelection(ISelection selection) {
//            if (fActiveProvider != null) {
//                fActiveProvider.setSelection(selection);
//            }
//        }
//    }


    @Override
    public void transformationStateChanged(TransformationState state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void terminated(IDebuggerHostAgent agent) throws CoreException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }
}
