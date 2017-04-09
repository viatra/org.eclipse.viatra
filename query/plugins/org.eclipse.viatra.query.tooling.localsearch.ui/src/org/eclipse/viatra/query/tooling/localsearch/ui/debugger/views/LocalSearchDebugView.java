/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.FrameViewerContentProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.MatchesTableLabelProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.OperationListContentProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.OperationListLabelProvider;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal.LocalSearchDebuggerPropertyTester;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugView extends ViewPart implements IZoomableWorkbenchPart {


	public static final String ID = "org.eclipse.viatra.query.tooling.localsearch.ui.LocalSearchDebugView";

	public static final String VIEWER_KEY = "key";
    
    private OperationListContentProvider operationListContentProvider;
    private TreeViewer operationListViewer;

    private GraphViewer graphViewer;
    private FrameViewerContentProvider zestContentProvider;
    
	private SashForm planSashForm;
	private CTabFolder matchesTabFolder;

	private Map<String, TableViewer> matchViewersMap = Maps.newHashMap();

	private LocalSearchDebugger debugger;


	public void setDebugger(LocalSearchDebugger localSearchDebugger) {
		this.debugger = localSearchDebugger;
		//Casting is required for backward compatibility with old platform versions
		IEvaluationService service = (IEvaluationService) getSite().getService(IEvaluationService.class);
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

		planSashForm = new SashForm(sashForm, SWT.VERTICAL);

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
        this.graphViewer = new GraphViewer(sashForm, SWT.BORDER);
        
        FrameViewerContentProvider zestContentProvider = new FrameViewerContentProvider();
        this.graphViewer.setContentProvider(zestContentProvider);
  
        ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
        adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
        AdapterFactoryLabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
        this.graphViewer.setLabelProvider(labelProvider);

        LayoutAlgorithm layout = getLayout();
        this.graphViewer.setLayoutAlgorithm(layout, true);
        this.graphViewer.applyLayout();
        this.graphViewer.setNodeStyle(ZestStyles.NONE);
        
        fillToolBar();
    }

    private void createTreeViewer(SashForm sashForm) {
        this.operationListViewer = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        this.operationListContentProvider = new OperationListContentProvider();

        this.operationListViewer.setContentProvider(operationListContentProvider);
        this.operationListViewer.setLabelProvider(new OperationListLabelProvider());
        // TODO why is this needed?
        this.operationListViewer.setInput(null);

    }

    private LayoutAlgorithm getLayout() {
        LayoutAlgorithm layout;
        layout = new TreeLayoutAlgorithm();
        // layout = new GridLayoutAlgorithm();
        // layout = new SpringLayoutAlgorithm();
        // layout = new HorizontalTreeLayoutAlgorithm();
        // layout = new RadialLayoutAlgorithm();
        return layout;
    }

    private void fillToolBar() {
        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
        IActionBars bars = getViewSite().getActionBars();
        bars.getMenuManager().add(toolbarZoomContributionViewItem);
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return graphViewer;
    }

    @Override
    public void setFocus() {
        operationListViewer.getControl().setFocus();
    }

    public void refreshView() {
    	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				operationListViewer.refresh();
				graphViewer.refresh();
				graphViewer.applyLayout();
				Collection<TableViewer> tableViewers = matchViewersMap.values();
				for (TableViewer tableViewer : tableViewers) {
					tableViewer.refresh();
				}
			}
		});
    }

    public TreeViewer getOperationListViewer() {
        return operationListViewer;
    }

    public void setOperationListViewer(TreeViewer operationListViewer) {
        this.operationListViewer = operationListViewer;
    }
    
    public OperationListContentProvider getOperationListContentProvider() {
    	return operationListContentProvider;
    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    public FrameViewerContentProvider getZestContentProvider() {
        return zestContentProvider;
    }

	public TableViewer getMatchesViewer(String queryName) {
		TableViewer viewer = matchViewersMap.get(queryName);
		if(viewer == null){
			getOrCreateMatchesTab(queryName);
		}
		return matchViewersMap.get(queryName);
	}

	private void getOrCreateMatchesTab(final String tabTitle) {
		// This method is called from a non-ui thread so that a syncexec is required here
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				CTabItem item = new CTabItem(getMatchesTabFolder(), SWT.NULL);
				item.setText(tabTitle);		

				// Mark as active
				getMatchesTabFolder().setSelection(item);
				
				// Table viewer for the matches
				Composite container = new Composite(getMatchesTabFolder(),SWT.NONE);
				container.setLayout(new FillLayout());
				final TableViewer viewer = createTableViewer(container);
				
				viewer.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if(event.getSelection() instanceof IStructuredSelection){
							IStructuredSelection selection = (IStructuredSelection) event.getSelection();
							MatchingFrame frame = (MatchingFrame) selection.getFirstElement();
							graphViewer.setInput(frame);
							graphViewer.applyLayout();
						}
					}
				});
				
				matchViewersMap.put(tabTitle, viewer);
				
				viewer.refresh();
				List<MatchingFrame> matchViewerInput = Lists.<MatchingFrame>newArrayList();
				viewer.setData(VIEWER_KEY, matchViewerInput);
				viewer.setInput(matchViewerInput);
				
				item.setControl(container);
				item.addListener(SWT.FOCUSED, new Listener() {
					
					@Override
					public void handleEvent(Event event) {
						viewer.setSelection(null);
					}
				}); 
				item.addListener(SWT.FocusIn, new Listener() {
					
					@Override
					public void handleEvent(Event event) {
						viewer.setSelection(null);
					}
				}); 
			}
		});
		
	}

	
	private static class MatchTableContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// nop
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nop
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				return ((Collection<?>) inputElement).toArray();
			}
			return new Object[0];
		}

	}

	private TableViewer createTableViewer(Composite parent) {
		TableViewer matchesViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
    	
		matchesViewer.setContentProvider(new MatchTableContentProvider());

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
        if (debugger != null) {
            debugger.dispose();
            debugger = null;
        }
        super.dispose();
    }

}
