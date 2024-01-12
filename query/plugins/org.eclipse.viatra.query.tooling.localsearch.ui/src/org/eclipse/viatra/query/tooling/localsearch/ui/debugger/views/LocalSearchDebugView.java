/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.viatra.integration.zest.viewer.ViatraGraphViewer;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchResultProvider;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.FrameViewerContentProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.MatchesTableLabelProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.OperationListContentProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.OperationListLabelProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.IPlanNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal.LocalSearchDebuggerPropertyTester;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugView extends ViewPart {


    public static final String ID = "org.eclipse.viatra.query.tooling.localsearch.ui.LocalSearchDebugView";

    public static final String VIEWER_KEY = "key";
    
    private OperationListContentProvider operationListContentProvider;
    private TreeViewer operationListViewer;

    private GraphViewer graphViewer;
    
    private CTabFolder matchesTabFolder;
    private Map<PQuery, CTabItem> matchesTabIndex = new HashMap<>();

    private Map<String, TableViewer> matchViewersMap = new HashMap<>();

    private LocalSearchDebugger debugger;
    private Thread planExecutorThread = null;

    private LocalSearchBackend localSearchBackend;

    /**
     * @throws ViatraQueryRuntimeException
     */
    public void createDebugger(final AdvancedViatraQueryEngine engine, final IQuerySpecification<?> query, final Object[] adornment) {
        disposeExistingDebugger();
        initializeDebugger(engine, query, adornment);
        closeMatchTabs();
    }
    

    private String getSimpleQueryName(PQuery query) {
        String[] stringTokens = query.getFullyQualifiedName().split("\\.");
        String queryName = stringTokens[stringTokens.length - 1];
        return queryName;
    }
    
    private void initializeDebugger(final AdvancedViatraQueryEngine engine, final IQuerySpecification<?> specification, final Object[] adornment) {
        final IQueryBackend lsBackend = engine.getQueryBackend(LocalSearchEMFBackendFactory.INSTANCE);
        final LocalSearchResultProvider lsResultProvider = (LocalSearchResultProvider) lsBackend
                .getResultProvider(specification.getInternalQueryRepresentation());
        localSearchBackend = (LocalSearchBackend) lsBackend;
        debugger = new LocalSearchDebugger(this, engine, specification, adornment);
        localSearchBackend.addAdapter(debugger);
        
        operationListViewer.setInput(debugger.getViewModel());
        operationListViewer.refresh();
        
        // Create and start the matcher thread
        Runnable planExecutorRunnable = () -> {
            Iterator<Tuple> tuples = lsResultProvider.getAllMatches(adornment).iterator();
            while(!Thread.currentThread().isInterrupted() && tuples.hasNext()) {
                // Right now we are only collecting the result matches but are ignoring them thus the empty body
            }
        };

        // Interrupt old executions
        if (planExecutorThread != null && planExecutorThread.isAlive()) {
            planExecutorThread.interrupt();
            /*
             * XXX not stopping this thread here might cause issues when restarting the debugger because of two separate
             * issues:
             *   (1) it is not possible to shut down local search-based matchers correctly, see
             * https://bugs.eclipse.org/bugs/show_bug.cgi?id=535102
             *   (2) if an interrupted matcher still runs in the
             * background and instantiates a _new_ matcher, the new instance might send notifications to a new set of
             * adapters, thus sending unparseable events to the debugger
             * 
             * Luckily, stopping the thread here should not cause any long-term issues as its entire lifecycle is
             * managed internally. However, if matchers can be stopped correctly, this should be fixed as well.
             */
            planExecutorThread.stop();
        }
        planExecutorThread = new Thread(planExecutorRunnable);
        planExecutorThread.start();
        
        IEvaluationService service = getSite().getService(IEvaluationService.class);
        service.requestEvaluation(LocalSearchDebuggerPropertyTester.DEBUGGER_RUNNING);
    }
    
    public LocalSearchDebugger getDebugger() {
        return this.debugger;
    }

    public CTabFolder getMatchesTabFolder() {
        return matchesTabFolder;
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayoutData(new FillLayout());
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

        SashForm planSashForm = new SashForm(sashForm, SWT.VERTICAL);

        // TreeViewer for the plan
        createTreeViewer(planSashForm);

        matchesTabFolder = new CTabFolder(planSashForm, SWT.MULTI | SWT.CLOSE);
        
        
        // Zest viewer
        createZestViewer(sashForm);
    }

    /**
     * Create the columns for the frame variables
     * 
     * @param colNames the variable names
     * @param parent the parent container
     * @param viewer the table viewer that will show the variable values
     */
    public void recreateColumns(List<String> colNames, int keySize, TableViewer matchesViewer) {
        // TODO solve situations where the variable list changes (also in size)
        TableColumn[] columns = matchesViewer.getTable().getColumns();
        for (TableColumn tableColumn : columns) {
            tableColumn.dispose();
        }
        
        for (int i = 0; i < colNames.size(); i++) {
            // For now the header font style cannot be changed, see bug 63038
            TableViewerColumn col = createTableViewerColumn(colNames.get(i), 100, i, matchesViewer);
            col.setLabelProvider(new MatchesTableLabelProvider(i, i < keySize, matchesViewer));
        }

    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber, TableViewer matchesViewer) {
        TableViewerColumn viewerColumn = new TableViewerColumn(matchesViewer, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }
    
    private void createZestViewer(SashForm sashForm) {
        this.graphViewer = new ViatraGraphViewer(sashForm, SWT.BORDER);
        graphViewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        FrameViewerContentProvider zestContentProvider = new FrameViewerContentProvider();
        this.graphViewer.setContentProvider(zestContentProvider);
  
        ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
        adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
        AdapterFactoryLabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
        this.graphViewer.setLabelProvider(labelProvider);

        this.graphViewer.setLayoutAlgorithm(getLayout());
    }

    private void createTreeViewer(SashForm sashForm) {
        this.operationListViewer = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        this.operationListContentProvider = new OperationListContentProvider();

        this.operationListViewer.setContentProvider(operationListContentProvider);
        this.operationListViewer.setLabelProvider(new OperationListLabelProvider());
        
        this.operationListViewer.addDoubleClickListener(event -> {
            IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();

            try {
                final IWorkbenchPartSite site = getSite();
                Map<String, Object> eventContextParameters = new HashMap<>();
                eventContextParameters.put(ISources.ACTIVE_WORKBENCH_WINDOW_NAME, site.getWorkbenchWindow());
                eventContextParameters.put(ISources.ACTIVE_PART_NAME, this);
                eventContextParameters.put(ISources.ACTIVE_PART_ID_NAME, LocalSearchDebugView.ID);
                eventContextParameters.put(ISources.ACTIVE_CURRENT_SELECTION_NAME, thisSelection);
                ICommandService commandService = site.getService(ICommandService.class);
                commandService.getCommand("org.eclipse.viatra.query.tooling.localsearch.ui.debugger.localsearch.placebreakpoint").executeWithChecks(
                        new ExecutionEvent(null, eventContextParameters, null, null));
            }
            catch (NotHandledException | ExecutionException | NotDefinedException | NotEnabledException e) {
                ViatraQueryLoggingUtil.getLogger(getClass()).error("Error setting up breakpoint", e);
            }

        });

    }

    private LayoutAlgorithm getLayout() {
        return new TreeLayoutAlgorithm();
        // layout = new GridLayoutAlgorithm();
        // layout = new SpringLayoutAlgorithm();
        // layout = new HorizontalTreeLayoutAlgorithm();
        // layout = new RadialLayoutAlgorithm();
    }

    @Override
    public void setFocus() {
        operationListViewer.getControl().setFocus();
    }

    public void refreshView(PQuery currentQuery, IPlanNode currentNode) {
        getViewSite().getShell().getDisplay().syncExec(() -> {
            operationListViewer.refresh();
            graphViewer.refresh();
            
            // Move to currently executed operation (if applicable)
            if (currentNode != null) {
                operationListViewer.expandToLevel(currentNode, 0);
                operationListViewer.reveal(currentNode);
            }
            
            
            Collection<TableViewer> tableViewers = matchViewersMap.values();
            for (TableViewer tableViewer : tableViewers) {
                tableViewer.refresh();
            }
            selectMatchTab(currentQuery);
        });
        
        IEvaluationService service = getSite().getService(IEvaluationService.class);
        service.requestEvaluation(LocalSearchDebuggerPropertyTester.DEBUGGER_RUNNING);
    }

    public TreeViewer getOperationListViewer() {
        return operationListViewer;
    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    public TableViewer getMatchesViewer(PQuery query) {
        String queryName = getSimpleQueryName(query);
        TableViewer viewer = matchViewersMap.get(queryName);
        if(viewer == null){
            getOrCreateMatchesTab(query);
        }
        return matchViewersMap.get(queryName);
    }
    
    public void selectMatchTab(PQuery query) {
        getMatchesTabFolder().setSelection(matchesTabIndex.get(query));
    }
    
    private void getOrCreateMatchesTab(PQuery query) {
        final String tabTitle = getSimpleQueryName(query);
        // This method is called from a non-ui thread so that a syncexec is required here
        getViewSite().getShell().getDisplay().syncExec(() -> {
            CTabItem item = new CTabItem(getMatchesTabFolder(), SWT.NULL);
            item.setText(tabTitle);		

            // Mark as active
            getMatchesTabFolder().setSelection(item);
            matchesTabIndex.put(query, item);
            
            // Table viewer for the matches
            Composite container = new Composite(getMatchesTabFolder(),SWT.NONE);
            container.setLayout(new FillLayout());
            final TableViewer viewer = createTableViewer(container);
            
            viewer.addSelectionChangedListener(event -> {
                if(event.getSelection() instanceof IStructuredSelection){
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    MatchingFrame frame = (MatchingFrame) selection.getFirstElement();
                    graphViewer.setInput(frame);
                    graphViewer.refresh();
                }
            });
            
            matchViewersMap.put(tabTitle, viewer);
            
            viewer.refresh();
            List<MatchingFrame> matchViewerInput = new ArrayList<>();
            viewer.setData(VIEWER_KEY, matchViewerInput);
            viewer.setInput(matchViewerInput);
            
            item.setControl(container);
            item.addListener(SWT.FOCUSED, event -> viewer.setSelection(null)); 
            item.addListener(SWT.FocusIn, event -> viewer.setSelection(null)); 
        });
        
    }

    private TableViewer createTableViewer(Composite parent) {
        TableViewer matchesViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        
        matchesViewer.setContentProvider(ArrayContentProvider.getInstance());

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        matchesViewer.getControl().setLayoutData(gridData);

        final Table table = matchesViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true); 
        
        return matchesViewer;
        
    }
    
    @Override
    public void dispose() {
        disposeExistingDebugger();
        super.dispose();
    }

    private void disposeExistingDebugger() {
        if (debugger != null) {
            localSearchBackend.removeAdapter(debugger);
            debugger.dispose();
            debugger = null;
        }
    }

    private void closeMatchTabs() {
        for (Item item : getMatchesTabFolder().getItems()) {
            item.dispose();
        }
        matchViewersMap.clear();
        matchesTabIndex.clear();
    }
}
