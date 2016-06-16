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
package org.eclipse.viatra.query.tooling.ui.queryregistry;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.XtextIndexBasedRegistryUpdater;

import com.google.inject.Injector;

/**
 * @author Abel Hegedus
 *
 */
public class QueryRegistryView extends ViewPart implements ITabbedPropertySheetPageContributor {

    public static final String ID = "org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryView"; //$NON-NLS-1$
    private TreeViewer queryTreeViewer;
    private QueryRegistryTreeInput queryRegistryTreeInput;
    private XtextIndexBasedRegistryUpdater updater;

    public QueryRegistryView() {
        
        Injector injector = EMFPatternLanguageActivator.getInstance().getInjector(EMFPatternLanguageActivator.ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE);
        updater = injector.getInstance(XtextIndexBasedRegistryUpdater.class);
        updater.connectIndexToRegistry(QuerySpecificationRegistry.getInstance());

        queryRegistryTreeInput = new QueryRegistryTreeInput(QuerySpecificationRegistry.getInstance());
    }
    
    @Override
    public void dispose() {
        updater.disconnectIndexFromRegistry();
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
    }

    private void initializeQueryTreeViewer(Composite queryRegistryContainer) {
        PatternFilter patternFilter = new PatternFilter();
        patternFilter.setIncludeLeadingWildcard(true);
        FilteredTree filteredTree = new FilteredTree(queryRegistryContainer, SWT.BORDER | SWT.MULTI, patternFilter, true);
        queryTreeViewer = filteredTree.getViewer();
        filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        queryTreeViewer.setComparator(new ViewerComparator());
        queryTreeViewer.setLabelProvider(new QueryRegistryTreeLabelProvider());
        queryTreeViewer.setContentProvider(new QueryRegistryTreeContentProvider());
        getSite().setSelectionProvider(queryTreeViewer);
        
        queryTreeViewer.setInput(queryRegistryTreeInput);
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
        updater.disconnectIndexFromRegistry();
        updater.connectIndexToRegistry(QuerySpecificationRegistry.getInstance());
    }
}
