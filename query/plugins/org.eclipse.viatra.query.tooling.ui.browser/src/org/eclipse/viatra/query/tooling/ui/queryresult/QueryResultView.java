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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters.EMFModelConnector;

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

    public QueryResultView() {
        this.propertyPageContributor = new ITabbedPropertySheetPageContributor(){
            @Override
            public String getContributorId() {
                return getSite().getId();
            }
        };
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
        queryResultTreeViewer = new TreeViewer(container, SWT.BORDER);
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
        getSite().setSelectionProvider(queryResultTreeViewer);
        
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

    public void loadModel(EMFModelConnector modelConnector, IModelConnectorTypeEnum scope) {

        unloadModel();
        
        try {
            input = QueryResultViewModel.INSTANCE.createInput(modelConnector, scope);
            queryResultTreeViewer.setInput(input);
            StringBuilder scopeDescriptionBuilder = new StringBuilder();
            scopeDescriptionBuilder
                .append("Editor: ").append(modelConnector.getKey().getEditorPart().getTitle())
                .append("\nScope type: ").append(scope.name().toLowerCase());
            if(scope == IModelConnectorTypeEnum.RESOURCE){
                Notifier notifier = modelConnector.getNotifier(scope);
                if(notifier instanceof Resource){
                    scopeDescriptionBuilder
                        .append("\nResource: ").append(((Resource) notifier).getURI().toString());
                }
            }
            lblScopeDescription.setText(scopeDescriptionBuilder.toString());
        } catch (ViatraQueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public void unloadModel() {
        if(input != null) {
            QueryResultViewModel.INSTANCE.removeInput(input);
            input = null;
            queryResultTreeViewer.setInput(null);
            lblScopeDescription.setText(SCOPE_UNINITIALIZED_MSG);
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
}
