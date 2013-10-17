/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial version
 *******************************************************************************/
package org.eclipse.incquery.xcore.editor;

import java.util.List;

import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.presentation.EcoreEditorPlugin;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;


/**
 * Customized Ecore editor that avoids the dangerous {@link DiagnosticDecorator} of the superclass.
 * 
 * Based on verbatim copy of sections of {@link EcoreEditor}.
 * 
 * @author istvanrath
 *
 */
public class CustomizedEcoreEditor extends EcoreEditor {

	@Override
	public void init(IEditorSite site, IEditorInput editorInput) {
		// TODO write settingdelegate initialization here

		// locate .eiq files somehow
		// load them as per http://wiki.eclipse.org/EMFIncQuery/UserDocumentation/API/Advanced#Loading_EIQ_resources_programmatically
		// register them in the Query Spec registry
		
		super.init(site, editorInput);
	}
	
	@Override
	public void dispose() {
		// TODO write settingdelegate deinitialization here
		// when the editor is closed, all the queries registered must be gone
		super.dispose();
	}
	
	@Override
	public void createPages() {
		// Creates the model from the editor input
	    //
	    createModel();

	    // Only creates the other pages if there is something that can be edited
	    //
	    if (!getEditingDomain().getResourceSet().getResources().isEmpty())
	    {
	      // Create a page for the selection tree view.
	      //
	      Tree tree = new Tree(getContainer(), SWT.MULTI);
	      selectionViewer = new TreeViewer(tree);
	      setCurrentViewer(selectionViewer);

	      selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
	      selectionViewer.setLabelProvider(new DecoratingColumLabelProvider(new AdapterFactoryLabelProvider(adapterFactory), new DummyDiagnosticDecorator(editingDomain, selectionViewer, EcoreEditorPlugin.getPlugin().getDialogSettings())));
	      selectionViewer.setInput(editingDomain.getResourceSet());
	      selectionViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);

	      new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);
	      new ColumnViewerInformationControlToolTipSupport(selectionViewer, new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, selectionViewer));

	      createContextMenuFor(selectionViewer);
	      int pageIndex = addPage(tree);
	      //setPageText(pageIndex, getString("_UI_SelectionPage_label"));
	      setPageText(pageIndex, "TODO");

	      
	      getSite().getShell().getDisplay().asyncExec
	        (new Runnable()
	         {
	           public void run()
	           {
	             setActivePage(0);
	           }
	         });
	    }

	    // Ensures that this editor will only display the page's tab
	    // area if there are more than one page
	    //
	    getContainer().addControlListener
	      (new ControlAdapter()
	       {
	        boolean guard = false;
	        @Override
	        public void controlResized(ControlEvent event)
	        {
	          if (!guard)
	          {
	            guard = true;
	            hideTabs();
	            guard = false;
	          }
	        }
	       });

	    getSite().getShell().getDisplay().asyncExec
	      (new Runnable()
	       {
	         public void run()
	         {
	           updateProblemIndication();
	         }
	       });
	}
	
	@Override
	public IContentOutlinePage getContentOutlinePage()
	  {
	    if (contentOutlinePage == null)
	    {
	      // The content outline is just a tree.
	      //
	      class MyContentOutlinePage extends ContentOutlinePage
	      {
	        @Override
	        public void createControl(Composite parent)
	        {
	          super.createControl(parent);
	          contentOutlineViewer = getTreeViewer();
	          contentOutlineViewer.addSelectionChangedListener(this);

	          // Set up the tree viewer.
	          //
	          contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
	          contentOutlineViewer.setLabelProvider(new DecoratingColumLabelProvider(new AdapterFactoryLabelProvider(adapterFactory), new DummyDiagnosticDecorator(editingDomain, contentOutlineViewer, EcoreEditorPlugin.getPlugin().getDialogSettings())));
	          contentOutlineViewer.setInput(editingDomain.getResourceSet());

	          new ColumnViewerInformationControlToolTipSupport(contentOutlineViewer, new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, contentOutlineViewer));

	          // Make sure our popups work.
	          //
	          createContextMenuFor(contentOutlineViewer);

	          if (!editingDomain.getResourceSet().getResources().isEmpty())
	          {
	            // Select the root object in the view.
	            //
	            contentOutlineViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);
	          }
	        }

	        @Override
	        public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager)
	        {
	          super.makeContributions(menuManager, toolBarManager, statusLineManager);
	          contentOutlineStatusLineManager = statusLineManager;
	        }

	        @Override
	        public void setActionBars(IActionBars actionBars)
	        {
	          super.setActionBars(actionBars);
	          getActionBarContributor().shareGlobalActions(this, actionBars);
	        }
	      }

	      contentOutlinePage = new MyContentOutlinePage();

	      // Listen to selection so that we can handle it is a special way.
	      //
	      contentOutlinePage.addSelectionChangedListener
	        (new ISelectionChangedListener()
	         {
	           // This ensures that we handle selections correctly.
	           //
	           public void selectionChanged(SelectionChangedEvent event)
	           {
	             handleContentOutlineSelection(event.getSelection());
	           }
	         });
	    }

	    return contentOutlinePage;
	  }
	
	@Override
	public IPropertySheetPage getPropertySheetPage()
	  {
	    PropertySheetPage propertySheetPage =
	      //new ExtendedPropertySheetPage(editingDomain, ExtendedPropertySheetPage.Decoration.LIVE, EcoreEditorPlugin.getPlugin().getDialogSettings())
	      new ExtendedPropertySheetPage(editingDomain, ExtendedPropertySheetPage.Decoration.NONE, EcoreEditorPlugin.getPlugin().getDialogSettings())
	      {
	        @Override
	        public void setSelectionToViewer(List<?> selection)
	        {
	          CustomizedEcoreEditor.this.setSelectionToViewer(selection);
	          CustomizedEcoreEditor.this.setFocus();
	        }

	        @Override
	        public void setActionBars(IActionBars actionBars)
	        {
	          super.setActionBars(actionBars);
	          getActionBarContributor().shareGlobalActions(this, actionBars);
	        }
	      };
	    propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
	    propertySheetPages.add(propertySheetPage);

	    return propertySheetPage;
	  }
}
