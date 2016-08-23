/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.EMFModelConnector;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.CommandConstants;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;
import org.eclipse.viatra.query.tooling.ui.queryresult.internal.ActiveEnginePropertyTester;
import org.eclipse.viatra.query.tooling.ui.queryresult.util.QueryResultViewUtil;
import org.eclipse.viatra.query.tooling.ui.queryresult.util.ViatraQueryEngineContentProvider;
import org.eclipse.viatra.query.tooling.ui.queryresult.util.ViatraQueryEngineLabelProvider;
import org.eclipse.viatra.query.tooling.ui.util.CommandInvokingDoubleClickListener;
import org.eclipse.viatra.query.tooling.ui.util.IModelConnectorListener;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Abel Hegedus
 *
 */
public class QueryResultView extends ViewPart {
    
    @Inject
    private Injector injector;
    
    private static final String SCOPE_UNINITIALIZED_MSG = "Scope uninitialized!\r\nPress the \"Load model from active editor\" button on the toolbar!";
    public static final String ID = "org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView"; //$NON-NLS-1$
    private TreeViewer queryResultTreeViewer;
    private QueryResultTreeInput input;
    private Label lblScopeDescription;
    private ITabbedPropertySheetPageContributor propertyPageContributor;
    private QueryEvaluationHint hint;
    private CollapseAllHandler collapseHandler;
    private IModelConnectorListener connectorListener;

    private TreeViewer engineDetailsTreeViewer;

    private StackLayout engineDetailsStackLayout;

    public QueryResultView() {
        this.propertyPageContributor = new ITabbedPropertySheetPageContributor(){
            @Override
            public String getContributorId() {
                return getSite().getId();
            }
        };
        this.hint = new QueryEvaluationHint(new ReteBackendFactory(), new HashMap<String, Object>());
        this.connectorListener = new IModelConnectorListener() {
            @Override
            public void modelUnloaded(IModelConnector modelConnector) {
                unloadModel();
            }
        };
    }

    /**
     * Create contents of the view part.
     * @param parent
     */
    @Override
    public void createPartControl(final Composite parent) {
        CommandInvokingDoubleClickListener showLocationListener = new CommandInvokingDoubleClickListener(CommandConstants.SHOW_LOCATION_COMMAND_ID, "Exception when activating show location!");
        injector.injectMembers(showLocationListener);
        
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transferTypes = new Transfer[]{LocalTransfer.getInstance()};
        
        SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
        
        Group grpScope = new Group(sashForm, SWT.NONE);
        grpScope.setText("Engine details");
        engineDetailsStackLayout = new StackLayout();
        grpScope.setLayout(engineDetailsStackLayout);

        engineDetailsTreeViewer = new TreeViewer(grpScope, SWT.BORDER);
        engineDetailsTreeViewer.setLabelProvider(new ViatraQueryEngineLabelProvider());
        engineDetailsTreeViewer.setContentProvider(new ViatraQueryEngineContentProvider());
        
        lblScopeDescription = new Label(grpScope, SWT.NONE | SWT.WRAP);
        lblScopeDescription.setText(SCOPE_UNINITIALIZED_MSG);
        engineDetailsStackLayout.topControl = lblScopeDescription;
        
        queryResultTreeViewer = new TreeViewer(sashForm, SWT.BORDER | SWT.MULTI);
        queryResultTreeViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if(e1 instanceof QueryResultTreeMatcher && e2 instanceof QueryResultTreeMatcher){
                    return super.compare(viewer, e1, e2);
                } else if(e1 instanceof IPatternMatch && e2 instanceof IPatternMatch){
                    return super.compare(viewer, e1, e2);
                } else {
                    // pattern parameters should be in their original order
                    return 0;
                }
            }
        });
        queryResultTreeViewer.setLabelProvider(new QueryResultTreeLabelProvider());
        queryResultTreeViewer.setContentProvider(new QueryResultTreeContentProvider());
        queryResultTreeViewer.addDoubleClickListener(showLocationListener);
        queryResultTreeViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if(parentElement instanceof QueryResultTreeMatcher && element instanceof IPatternMatch) {
                    QueryResultTreeMatcher queryResultTreeMatcher = (QueryResultTreeMatcher) parentElement;
                    IPatternMatch patternMatch = (IPatternMatch) element;
                    boolean compatibleWith = queryResultTreeMatcher.getFilterMatch().isCompatibleWith(patternMatch);
                    return compatibleWith;
                }
                return true;
            }
        });
        queryResultTreeViewer.addDropSupport(operations, transferTypes, new ViewerDropAdapter(queryResultTreeViewer) {

            @Override
            public boolean performDrop(Object data) {
                if(data instanceof IStructuredSelection){
                    boolean active = hasActiveEngine();
                    if (active) {
                        Set<QueryRegistryTreeEntry> selectedQueries = QueryResultViewUtil.getRegistryEntriesFromSelection((IStructuredSelection) data);
                        for (QueryRegistryTreeEntry queryRegistryTreeEntry : selectedQueries) {
                            queryRegistryTreeEntry.load();
                        }
                        loadQueriesIntoActiveEngine(QueryResultViewUtil.unwrapEntries(selectedQueries));
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean validateDrop(Object target, int operation, TransferData transferType) {
                boolean active = hasActiveEngine();
                boolean supportedType = LocalTransfer.getInstance().isSupportedType(transferType);
                return active && supportedType;
            }
        });
        sashForm.setWeights(new int[] {1, 4});
        
        getSite().setSelectionProvider(queryResultTreeViewer);
        Control control = queryResultTreeViewer.getControl();
        // Create pop-up menu over the tree viewer
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        control.setMenu(menuManager.createContextMenu(control));
        getSite().registerContextMenu(ID,menuManager, queryResultTreeViewer);
        collapseHandler = new CollapseAllHandler(queryResultTreeViewer);
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
        handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, collapseHandler);
    }

    @Override
    public void dispose() {
        collapseHandler.dispose();
        super.dispose();
    }
    
    @Override
    public void setFocus() {
        // Set the focus
        queryResultTreeViewer.getTree().setFocus();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return adapter.cast(new TabbedPropertySheetPage(this.propertyPageContributor));
        }
        return super.getAdapter(adapter);
    }

    private void loadEngineDetails(AdvancedViatraQueryEngine engine) {
        engineDetailsTreeViewer.setInput(engine);
        engineDetailsStackLayout.topControl = engineDetailsTreeViewer.getTree();
        requestLayoutInternal(engineDetailsTreeViewer.getControl());
    }
    
    private void removeEngineDetails() {
        engineDetailsStackLayout.topControl = lblScopeDescription;
        engineDetailsTreeViewer.setInput(null);
        requestLayoutInternal(lblScopeDescription);
    }
    
    /**
     * Copy implementation from newer framework version of {@link Control#requestLayout()}.
     */
    private void requestLayoutInternal(Control control) {
        control.getShell().layout(new Control[] {control}, SWT.DEFER);
    }
    
    public void loadModel(EMFModelConnector modelConnector, IModelConnectorTypeEnum scope) throws ViatraQueryException {

        unloadModel();

        input = QueryResultViewModel.INSTANCE.createInput(modelConnector, scope);
        input.setHint(hint);
        queryResultTreeViewer.setInput(input);
        modelConnector.addListener(connectorListener);
        loadEngineDetails(input.getEngine());
        activeEnginePropertyChanged();

    }

    /**
     * @since 1.4
     */
    public void loadExistingEngine(AdvancedViatraQueryEngine engine) {
        unloadModel();

        input = QueryResultViewModel.INSTANCE.createInput(engine, true);
        queryResultTreeViewer.setInput(input);
        loadEngineDetails(engine);
        activeEnginePropertyChanged();
    }
    
    /**
     * @since 1.4
     */
    private void activeEnginePropertyChanged() {
        //Casting is required for backward compatibility with old platform versions
        IEvaluationService service = (IEvaluationService) getSite().getService(IEvaluationService.class);
        if(service != null){
            service.requestEvaluation(ActiveEnginePropertyTester.ACTIVE_ENGINE_ID);
        }
    }
    
    public void unloadModel() {
        if(input != null) {
            QueryResultViewModel.INSTANCE.removeInput(input);
            if(input.getModelConnector() instanceof EMFModelConnector) {
                EMFModelConnector emfModelConnector = (EMFModelConnector) input.getModelConnector();
                emfModelConnector.removeListener(connectorListener);
            }
            input = null;
            if(!queryResultTreeViewer.getControl().isDisposed()){
                queryResultTreeViewer.setInput(null);
                removeEngineDetails();
            }
            activeEnginePropertyChanged();
        }
    }
    
    public void loadQueriesIntoActiveEngine(Iterable<IQuerySpecificationRegistryEntry> providers) {
        if(!input.isReadOnlyEngine()){
            input.loadQueries(providers);
        }
    }
    
    public boolean hasActiveEngine() {
        return input != null;
    }

    public void wipeEngine() {
        if(input != null && !input.isReadOnlyEngine()){
            input.resetInput();
        }
    }

    public QueryEvaluationHint getHint() {
        if(input != null){
            return input.getHint();
        }
        return hint;
    }
    
    public void setHint(QueryEvaluationHint hint) {
        this.hint = hint;
        if(input != null){
            input.setHint(hint);
        }
    }
    
    /**
     * @since 1.4
     */
    public IModelConnector getModelConnector() {
        if(input != null) {
            return input.getModelConnector();
        }
        return null;
    }
}
