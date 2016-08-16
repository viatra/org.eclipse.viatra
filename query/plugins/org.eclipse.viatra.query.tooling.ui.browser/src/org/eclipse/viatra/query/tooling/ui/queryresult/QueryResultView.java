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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
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
import org.eclipse.viatra.query.tooling.ui.util.CommandInvokingDoubleClickListener;

/**
 * @author Abel Hegedus
 *
 */
public class QueryResultView extends ViewPart {
    
    private static final String SCOPE_UNINITIALIZED_MSG = "Scope uninitialized!\r\nPress the \"Load from active editor\" button on the toolbar!";
    public static final String ID = "org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView"; //$NON-NLS-1$
    private TreeViewer queryResultTreeViewer;
    private QueryResultTreeInput input;
    private Label lblScopeDescription;
    private ITabbedPropertySheetPageContributor propertyPageContributor;
    private QueryEvaluationHint hint;
    private CollapseAllHandler collapseHandler;

    public QueryResultView() {
        this.propertyPageContributor = new ITabbedPropertySheetPageContributor(){
            @Override
            public String getContributorId() {
                return getSite().getId();
            }
        };
        this.hint = new QueryEvaluationHint(new ReteBackendFactory(), new HashMap<String, Object>());
    }

    /**
     * Create contents of the view part.
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        
        Group grpScope = new Group(container, SWT.NONE);
        grpScope.setLayout(new GridLayout(1, false));
        grpScope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        grpScope.setText("Scope");
        
        lblScopeDescription = new Label(grpScope, SWT.NONE);
        lblScopeDescription.setText(SCOPE_UNINITIALIZED_MSG);
        queryResultTreeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
        Tree tree = queryResultTreeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
        queryResultTreeViewer.addDoubleClickListener(new CommandInvokingDoubleClickListener(CommandConstants.SHOW_LOCATION_COMMAND_ID, "Exception when activating show location!"));
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
        
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transferTypes = new Transfer[]{LocalTransfer.getInstance()};
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
        
        getSite().setSelectionProvider(queryResultTreeViewer);
        
        // Create pop-up menu over the tree viewer
        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager mgr) {
                mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        Control control = queryResultTreeViewer.getControl();
        control.setMenu(menuManager.createContextMenu(control));
        getSite().registerContextMenu(ID,menuManager, queryResultTreeViewer);
        
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
        collapseHandler = new CollapseAllHandler(queryResultTreeViewer);
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

    public void loadModel(EMFModelConnector modelConnector, IModelConnectorTypeEnum scope) throws ViatraQueryException {

        unloadModel();

        input = QueryResultViewModel.INSTANCE.createInput(modelConnector, scope);
        input.setHint(hint);
        queryResultTreeViewer.setInput(input);
        StringBuilder scopeDescriptionBuilder = new StringBuilder();
        scopeDescriptionBuilder.append("Editor: ").append(modelConnector.getKey().getEditorPart().getTitle())
                .append("\nScope type: ").append(scope.name().toLowerCase());
        if (scope == IModelConnectorTypeEnum.RESOURCE) {
            Notifier notifier = modelConnector.getNotifier(scope);
            if (notifier instanceof Resource) {
                scopeDescriptionBuilder.append("\nResource: ").append(((Resource) notifier).getURI().toString());
            }
        }
        lblScopeDescription.setText(scopeDescriptionBuilder.toString());
        activeEnginePropertyChanged();

    }

    /**
     * @since 1.4
     */
    private void activeEnginePropertyChanged() {
        //Casting is required for backward compatibility with old platform versions
        IEvaluationService service = (IEvaluationService) getSite().getService(IEvaluationService.class);
        service.requestEvaluation(ActiveEnginePropertyTester.ACTIVE_ENGINE_ID);
    }
    
    public void unloadModel() {
        if(input != null) {
            QueryResultViewModel.INSTANCE.removeInput(input);
            input = null;
            queryResultTreeViewer.setInput(null);
            lblScopeDescription.setText(SCOPE_UNINITIALIZED_MSG);
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
