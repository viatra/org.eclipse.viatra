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
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.model.TransformationStackFrame;
import org.eclipse.viatra.transformation.debug.model.TransformationThread;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.activator.TransformationDebugUIActivator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class AdaptableTransformationBrowser extends ViewPart
        implements IDebuggerHostAgentListener {
    public static final String ID = "org.eclipse.viatra.transformation.debug.ui.AdaptableTransformationBrowser";
    
    private TransformationThread currentThread;
    
    private Multimap<Class<?>, Object> expandedElementsMap = ArrayListMultimap.create();
    private TreeViewer treeViewer;
    private Object selection;
    
    @Override
    public void createPartControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        ISelectionService sService = getSite().getWorkbenchWindow().getSelectionService();
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        treeViewer = new TreeViewer(composite, SWT.BORDER);

        treeViewer.setContentProvider(new RuleBrowserContentProvider(this));
        treeViewer.setLabelProvider(new RuleBrowserLabelProvider(this));

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                }
            }
        });
        
        ISelectionListener listener = new ISelectionListener() {
            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (!selection.isEmpty() && selection instanceof StructuredSelection) {
                    Object firstElement = ((StructuredSelection) selection).getFirstElement();
                    try {
                        if (firstElement instanceof TransformationThread) {
                            currentThread = (TransformationThread) firstElement;
                            Object[] expandedElements = treeViewer.getExpandedElements();
                            if(!currentThread.isTerminated()){
                                treeViewer.setInput(currentThread);
                            }
                            treeViewer.setExpandedElements(expandedElements);
                            currentThread.getHostAgent().registerDebuggerHostAgentListener(AdaptableTransformationBrowser.this);
                        } else if(firstElement instanceof TransformationStackFrame){
                            TransformationThread thread = (TransformationThread) ((TransformationStackFrame) firstElement).getThread();
                            currentThread =  thread;
                            Object[] expandedElements = treeViewer.getExpandedElements();
                            if(!currentThread.isTerminated()){
                                treeViewer.setInput(currentThread);
                            }
                            treeViewer.setExpandedElements(expandedElements);
                            currentThread.getHostAgent().registerDebuggerHostAgentListener(AdaptableTransformationBrowser.this);
                        }
                    } catch (Exception e) {
                        TransformationDebugUIActivator.getDefault().logException(e.getMessage(), e);
                        ErrorDialog.openError(composite.getShell(), "An error has occured", e.getMessage(),
                                new Status(IStatus.ERROR, TransformationDebugUIActivator.PLUGIN_ID, e.getMessage()));
                    }
                }

            }
        };
        
        sService.addSelectionListener("org.eclipse.debug.ui.DebugView", listener);
        getSite().setSelectionProvider(treeViewer);
    }

    @Override
    public void dispose() {
        super.dispose();
        expandedElementsMap.clear();
        currentThread.getHostAgent().unRegisterDebuggerHostAgentListener(this);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    public Object getSelection() {
        return selection;
    }

    public void setViewConfiguration(final TransformationViewConfiguration config) {
        treeViewer.getControl().getDisplay().syncExec(new ConfigurationApplication(config, this));
    }
 
    private final class ConfigurationApplication implements Runnable {
        private final TransformationViewConfiguration config;
        private final AdaptableTransformationBrowser view;

        private ConfigurationApplication(TransformationViewConfiguration config, AdaptableTransformationBrowser view) {
            this.config = config;
            this.view = view;
        }

        @Override
        public void run() {
            switch (config) {
            case RULE_BROWSER:
                saveExpandedElements();
                treeViewer.setContentProvider(new RuleBrowserContentProvider(view));
                treeViewer.setLabelProvider(new RuleBrowserLabelProvider(view));
                treeViewer.refresh();
                treeViewer.setExpandedElements(loadExpandedElements());
                break;

            case CONFLICTSET_BROWSER:
                saveExpandedElements();
                treeViewer.setContentProvider(new ConflictSetContentProvider(view));
                treeViewer.setLabelProvider(new ConflictSetLabelProvider(view));
                treeViewer.refresh();
                treeViewer.setExpandedElements(loadExpandedElements());
                break;
            default:
                break;
            }
        }

        private void saveExpandedElements() {
            Object[] expandedElements = treeViewer.getExpandedElements();
            IContentProvider contentProvider = treeViewer.getContentProvider();
            for (Object element : expandedElements) {
                expandedElementsMap.put(contentProvider.getClass(), element);
            }

        }

        private Object[] loadExpandedElements() {
            IContentProvider contentProvider = treeViewer.getContentProvider();
            Collection<Object> elements = expandedElementsMap.get(contentProvider.getClass());

            return elements.toArray(new Object[elements.size()]);

        }
    }

    @Override
    public void transformationStateChanged(final TransformationState state) {
        if(currentThread.getTransformationState().equals(state)){
            treeViewer.getControl().getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    Object[] expandedElements = treeViewer.getExpandedElements();
                    treeViewer.setInput(currentThread);
                    treeViewer.setExpandedElements(expandedElements);
                }
            });
        }
    }

    @Override
    public void terminated(final IDebuggerHostAgent agent) throws CoreException {
        if(currentThread.getHostAgent().equals(agent)){
            treeViewer.getControl().getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    treeViewer.setInput(new Object[0]);
                    currentThread = null;
                }
            });
        }
    }
        
}
