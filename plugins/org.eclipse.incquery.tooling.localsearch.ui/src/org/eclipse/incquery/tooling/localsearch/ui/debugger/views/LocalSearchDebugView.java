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
package org.eclipse.incquery.tooling.localsearch.ui.debugger.views;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.OperationListContentProvider;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.OperationListLabelProvider;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.ZestNodeContentProvider;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.views.internal.BreakPointListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.Lists;

/**
 * 
 * @author Marton Bur
 *
 */
public class LocalSearchDebugView extends ViewPart implements IZoomableWorkbenchPart {


    private final class ColumnLabelProviderExtension extends ColumnLabelProvider {
		private int columnIndex;

		public ColumnLabelProviderExtension(int i) {
			this.columnIndex = i;
		}

		@Override
		public String getText(Object inputElement) {
			
			MatchingFrame frame = (MatchingFrame)inputElement;
			Object element = frame.get(columnIndex);
			
			if(element == null){
				return "";
			}
			if(element instanceof EObject){
				EObject eObject = ((EObject) element);
				
				EStructuralFeature feature = eObject.eClass().getEStructuralFeature("name");
				if(feature != null){
					if(!feature.isMany()){
						return eObject.eGet(feature).toString();
					}							
				} else {
					feature = eObject.eClass().getEStructuralFeature(0);
					if(!feature.isMany()){
						return eObject.eGet(feature).toString();
					}														
				}
			}
			return element.toString();
		}
	}

	public static final String ID = "org.eclipse.incquery.tooling.localsearch.ui.LocalSearchDebugView";
    
    private OperationListContentProvider operationListContentProvider;
    private TreeViewer operationListViewer;
    private OperationListLabelProvider operationListLabelProvider;

    private GraphViewer graphViewer;
    private ZestNodeContentProvider zestContentProvider;
    
    private TableViewer matchesViewer;
    private List<TableViewerColumn> columns = Lists.newArrayList();

    public TableViewer getMatchesViewer() {
		return matchesViewer;
	}

    private List<Object> breakpoints = Lists.newLinkedList();
    
    private boolean halted = true;

	private SashForm planSashForm;


    public LocalSearchDebugView() {
    }
    
    public List<Object> getBreakpoints() {
        return breakpoints;
    }
    
    public boolean isBreakpointHit(SearchPlanExecutor planExecutor) {
    	
    	int currentOperation = planExecutor.getCurrentOperation();
		boolean operationNotInRange = planExecutor.getSearchPlan().getOperations().size() <= currentOperation || currentOperation < 0;
		ISearchOperation currentSerachOperation = operationNotInRange 
				? null 
				: planExecutor.getSearchPlan().getOperations().get(currentOperation);

		boolean matched = planExecutor.getSearchPlan().getOperations().size() == currentOperation;
		Object dummyMatchOperation = null;
		if(matched){
			dummyMatchOperation = operationListLabelProvider.getDummyMatchOperation(planExecutor);
		}
    	
        if (halted == false) {
            halted = breakpoints.contains(currentSerachOperation);
            halted |= breakpoints.contains(dummyMatchOperation);
            return halted;
        } else {
            return true;
        }
    }
    
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayoutData(new FillLayout());
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

        planSashForm = new SashForm(sashForm, SWT.VERTICAL);
        
        // TreeViewer for the plan
        createTreeViewer(planSashForm);

        // Table viewer for the matches
        createTableViewer(planSashForm);
        
        // Zest viewer
        createZestViewer(sashForm);
    }

    private void createTableViewer(SashForm planSashForm) {
    	matchesViewer = new TableViewer(planSashForm, SWT.MULTI | SWT.H_SCROLL
    		      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
    	
		matchesViewer.setContentProvider(ArrayContentProvider.getInstance());
		
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
//	    gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    matchesViewer.getControl().setLayoutData(gridData);
	    
		
    	final Table table = matchesViewer.getTable();
    	table.setHeaderVisible(true);
    	table.setLinesVisible(true); 
    	
	}

    /**
     * Create the columns for the frame variables
     * 
     * @param colNames the variable names
     * @param parent the parent container
     * @param viewer the table viewer that will show the variable values
     */
    public void recreateColumns(List<String> colNames) {
      
      for (TableViewerColumn column : columns) {
    	  column.getColumn().dispose();
      }
      columns.clear();
      
      for (int i = 0; i<colNames.size(); i++) {
			TableViewerColumn col = createTableViewerColumn(colNames.get(i), 100, i);
			columns.add(col);
			col.setLabelProvider(new ColumnLabelProviderExtension(i));
		}

    }
    
    private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(matchesViewer,SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
      }
    
	private void createZestViewer(SashForm sashForm) {
        this.graphViewer = new GraphViewer(sashForm, SWT.BORDER);
        
        ZestNodeContentProvider zestContentProvider = new ZestNodeContentProvider();
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
        this.operationListLabelProvider = new OperationListLabelProvider(breakpoints);

        this.operationListViewer.setContentProvider(operationListContentProvider);
        operationListContentProvider.setLabelProvider(operationListLabelProvider);
        this.operationListViewer.setLabelProvider(operationListLabelProvider);
        this.operationListViewer.setInput(null);

        
        BreakPointListener breakPointListener = new BreakPointListener(this);
        this.operationListViewer.addDoubleClickListener(breakPointListener);

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

    public void refreshOperationList() {
    	// TODO check thread accesses
    	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				operationListViewer.refresh();
			}
		});
    }

    public void refreshGraph() {
        graphViewer.refresh();
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

    public OperationListLabelProvider getOperationListLabelProvider() {
        return operationListLabelProvider;
    }

    public void setOperationListLabelProvider(OperationListLabelProvider operationLabelProvider) {
        this.operationListLabelProvider = operationLabelProvider;
    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    public ZestNodeContentProvider getZestContentProvider() {
        return zestContentProvider;
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public boolean isHalted() {
        return halted;
    }


}
