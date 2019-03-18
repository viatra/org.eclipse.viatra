/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryregistry;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdater;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdaterFactory;
import org.eclipse.viatra.query.tooling.ui.queryresult.handlers.LoadQueriesHandler;
import org.eclipse.viatra.query.tooling.ui.util.CommandInvokingDoubleClickListener;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Abel Hegedus
 *
 */
public class QueryRegistryView extends ViewPart implements ITabbedPropertySheetPageContributor {

    @Inject
    private Injector injector;
    
    public static final String ID = "org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryView"; //$NON-NLS-1$
    private TreeViewer queryTreeViewer;
    private QueryRegistryTreeInput queryRegistryTreeInput;
    private XtextIndexBasedRegistryUpdater updater;
    private CollapseAllHandler collapseHandler;

    private Job initializerJob = new Job("Initializing Query Registry") {

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            updater = XtextIndexBasedRegistryUpdaterFactory.INSTANCE.getUpdater(QuerySpecificationRegistry.getInstance());
            queryRegistryTreeInput = new QueryRegistryTreeInput(QuerySpecificationRegistry.getInstance());
            
            QueryRegistryView.this.getViewSite().getShell().getDisplay().asyncExec(() -> queryTreeViewer.setInput(queryRegistryTreeInput));
            return Status.OK_STATUS;
        }
        
    };
    
    @Override
    public void dispose() {
        collapseHandler.dispose();
        super.dispose();
    }

    /**
     * Create contents of the view part.
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout(SWT.HORIZONTAL));
        Composite queryRegistryContainer = new Composite(parent, SWT.NONE);
        queryRegistryContainer.setLayout(new GridLayout(1, false));
        
        initializeQueryTreeViewer(queryRegistryContainer);
        
        initializerJob.setUser(false);
        initializerJob.schedule();
    }

    private void initializeQueryTreeViewer(Composite queryRegistryContainer) {
        PatternFilter patternFilter = new PatternFilter();
        patternFilter.setIncludeLeadingWildcard(true);
        FilteredTree filteredTree = new FilteredTree(queryRegistryContainer, SWT.BORDER | SWT.MULTI | SWT.VIRTUAL, patternFilter, true);
        queryTreeViewer = filteredTree.getViewer();
        filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        queryTreeViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof QueryRegistryTreeSource && e2 instanceof QueryRegistryTreeSource) {
                    QueryRegistryTreeSource source1 = (QueryRegistryTreeSource) e1;
                    QueryRegistryTreeSource source2 = (QueryRegistryTreeSource) e2;
                    return source1.getSourceIdentifier().compareTo(source2.getSourceIdentifier());
                }
                return super.compare(viewer, e1, e2);
            }
            
        });
        queryTreeViewer.setLabelProvider(new QueryRegistryTreeLabelProvider());
        queryTreeViewer.setContentProvider(new QueryRegistryTreeContentProvider());
        CommandInvokingDoubleClickListener loadQueriesListener = new CommandInvokingDoubleClickListener(LoadQueriesHandler.COMMAND_ID, "Exception when activating load queries!"){
            @Override
            protected void handleException(CommandException e) {
                LoadQueriesHandler.queryLoadingFailed(getSite().getShell());
            }
        };
        injector.injectMembers(loadQueriesListener);
        queryTreeViewer.addDoubleClickListener(loadQueriesListener);
        
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transferTypes = new Transfer[]{LocalTransfer.getInstance()};
        queryTreeViewer.addDragSupport(operations, transferTypes, new DragSourceAdapter() {
            
            @Override
            public void dragSetData(DragSourceEvent event) {
                ISelection selection = queryTreeViewer.getSelection();
                if(LocalTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = selection;
                }
            }
        });
        
        getSite().setSelectionProvider(queryTreeViewer);
        
        IHandlerService handlerService = getSite().getService(IHandlerService.class);
        collapseHandler = new CollapseAllHandler(queryTreeViewer);
        handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, collapseHandler);
        
        // Create pop-up menu over the tree viewer
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(mgr -> mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS)));
        Control control = queryTreeViewer.getControl();
        control.setMenu(menuManager.createContextMenu(control));
        getSite().registerContextMenu(ID,menuManager, queryTreeViewer);
    }

    @Override
    public void setFocus() {
        // Set the focus
        queryTreeViewer.getControl().setFocus();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return adapter.cast(new TabbedPropertySheetPage(this));
        }
        return super.getAdapter(adapter);
    }

    @Override
    public String getContributorId() {
        return getSite().getId();
    }

    public void resetView() {
        BusyIndicator.showWhile(getSite().getShell().getDisplay(), () -> {
            try {
                // Initializer job cannot really be cancelled; wait for it to finish
                initializerJob.join();
                updater.disconnectIndexFromRegistry();
                updater.connectIndexToRegistry(QuerySpecificationRegistry.getInstance());
            } catch (InterruptedException e) {
                String logMessage = "Error while resetting Query Registry: " + e.getMessage();
                EMFPatternLanguageUIPlugin.getInstance().logException(logMessage, e);
                // Maintaining interrupted state
                Thread.currentThread().interrupt();
            }
        });
    }
    
    public void collapseAll() {
        queryTreeViewer.collapseAll();
    }
}
