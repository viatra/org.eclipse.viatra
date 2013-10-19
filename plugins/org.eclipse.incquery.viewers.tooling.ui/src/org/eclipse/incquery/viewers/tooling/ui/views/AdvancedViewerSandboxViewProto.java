package org.eclipse.incquery.viewers.tooling.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;

public class AdvancedViewerSandboxViewProto extends ViewPart {

	public static final String ID = "org.eclipse.incquery.viewers.tooling.ui.views.AdvancedViewerSandboxViewProto"; //$NON-NLS-1$

	public AdvancedViewerSandboxViewProto() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		
		TreeViewer treeViewer = new TreeViewer(sashForm, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		
		TreeViewer treeViewer_1 = new TreeViewer(sashForm, SWT.BORDER);
		Tree tree_1 = treeViewer_1.getTree();
		
		TreeViewer treeViewer_2 = new TreeViewer(sashForm, SWT.BORDER);
		Tree tree_2 = treeViewer_2.getTree();
		sashForm.setWeights(new int[] {1, 1, 1});


		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
