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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.presentation.EcoreEditorPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.tooling.core.generator.GeneratorModule;
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

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Customized Ecore editor that avoids the dangerous {@link DiagnosticDecorator} of the superclass.
 * 
 * Based on verbatim copy of sections of {@link EcoreEditor}.
 * 
 * @author istvanrath
 * 
 */
public class CustomizedEcoreEditor extends EcoreEditor {
    
	private void log(String msg) {
		// TODO proper logging
		System.out.println(msg);
	}
	
    private Set<String> specs = new HashSet<String>();

    @Override
    public void init(IEditorSite site, IEditorInput editorInput) {
    	initializeQuerySpecificationRegistry();
        super.init(site, editorInput);
    }
    
	private void initializeQuerySpecificationRegistry() {
		// TODO eliminate hard coded stuff
		// use a trick to load Pattern models from a file
		
//		new EMFPatternLanguageStandaloneSetup()
//	    {
//	     @Override
//	     public Injector createInjector() { return Guice.createInjector(new GeneratorModule()); }
//	    }
//	   .createInjectorAndDoEMFRegistration();
		
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI1 = URI.createPlatformResourceURI("library/src/library/LibraryQueries.eiq",false);
		URI fileURI2 = URI.createPlatformResourceURI("library/src/library/ValidationQueries.eiq",false);
		URI fileURI3 = URI.createPlatformResourceURI("library/src/library/HelperQueries.eiq",false);
		
		Resource patternResource1 = resourceSet.getResource(fileURI1, true);
		Resource patternResource2 = resourceSet.getResource(fileURI2, true);
		Resource patternResource3 = resourceSet.getResource(fileURI3, true);
		
		if (patternResource1!=null) initReg(patternResource1);
		if (patternResource2!=null) initReg(patternResource2);
		if (patternResource3!=null) initReg(patternResource3);
	}
	
	private void initReg(Resource patternResource) {
		if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
			log("Registering patterns from "+patternResource.getURI());
            EObject topElement = patternResource.getContents().get(0);
            if (topElement instanceof PatternModel) {
                for (Pattern pattern : ((PatternModel) topElement).getPatterns()) {
                    IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> spec = QuerySpecificationRegistry.getOrCreateQuerySpecification(pattern);
                    QuerySpecificationRegistry.registerQuerySpecification(spec);
                    specs.add(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
                    log("Registered " + spec.getPatternFullyQualifiedName());
                }
            }
        } 
		else {
			log("Skipping registration from "+patternResource.getURI()+" due to errors: "+patternResource.getErrors().size());
			for (Diagnostic d : patternResource.getErrors()) {
				log("\t"+d.getMessage());
			}
		}
	}

    private void initializeRegistry() {
        ResourceSet resourceSet = new ResourceSetImpl();
        for (Resource resource : editingDomain.getResourceSet().getResources()) {
            if (resource.getURI().toString().endsWith(".eiq")) {
                Resource patternResource = resourceSet.getResource(resource.getURI(), true);
                if (patternResource != null) {
                    if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
                        EObject topElement = patternResource.getContents().get(0);
                        if (topElement instanceof PatternModel) {
                            for (Pattern pattern : ((PatternModel) topElement).getPatterns()) {
                                IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> spec = QuerySpecificationRegistry.getOrCreateQuerySpecification(pattern);
                                QuerySpecificationRegistry.registerQuerySpecification(spec);
                                specs.add(CorePatternLanguageHelper.getFullyQualifiedName(pattern));
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        for (String spec : specs) {
            QuerySpecificationRegistry.unregisterQuerySpecification(spec);
        }
    }
	
	@Override
	public void createPages() {
	    createModel();

        // initializeRegistry();

        // Only creates the other pages if there is something that can be edited
        //
        if (!getEditingDomain().getResourceSet().getResources().isEmpty()) {
            // Create a page for the selection tree view.
            //
            Tree tree = new Tree(getContainer(), SWT.MULTI);
            selectionViewer = new TreeViewer(tree);
            setCurrentViewer(selectionViewer);

            selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
            selectionViewer.setLabelProvider(new DecoratingColumLabelProvider(new AdapterFactoryLabelProvider(
                    adapterFactory), new DummyDiagnosticDecorator(editingDomain, selectionViewer, EcoreEditorPlugin
                    .getPlugin().getDialogSettings())));
            selectionViewer.setInput(editingDomain.getResourceSet());
            selectionViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)),
                    true);

            new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);
            new ColumnViewerInformationControlToolTipSupport(selectionViewer,
                    new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, selectionViewer));

            createContextMenuFor(selectionViewer);
            int pageIndex = addPage(tree);
            // setPageText(pageIndex, getString("_UI_SelectionPage_label"));
            setPageText(pageIndex, "TODO");

            getSite().getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    setActivePage(0);
                }
            });
        }

        // Ensures that this editor will only display the page's tab
        // area if there are more than one page
        //
        getContainer().addControlListener(new ControlAdapter() {
            boolean guard = false;

            @Override
            public void controlResized(ControlEvent event) {
                if (!guard) {
                    guard = true;
                    hideTabs();
                    guard = false;
                }
            }
        });

        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                updateProblemIndication();
            }
        });
    }

    @Override
    public IContentOutlinePage getContentOutlinePage() {
        if (contentOutlinePage == null) {
            // The content outline is just a tree.
            //
            class MyContentOutlinePage extends ContentOutlinePage {
                @Override
                public void createControl(Composite parent) {
                    super.createControl(parent);
                    contentOutlineViewer = getTreeViewer();
                    contentOutlineViewer.addSelectionChangedListener(this);

                    // Set up the tree viewer.
                    //
                    contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
                    contentOutlineViewer.setLabelProvider(new DecoratingColumLabelProvider(
                            new AdapterFactoryLabelProvider(adapterFactory), new DummyDiagnosticDecorator(
                                    editingDomain, contentOutlineViewer, EcoreEditorPlugin.getPlugin()
                                            .getDialogSettings())));
                    contentOutlineViewer.setInput(editingDomain.getResourceSet());

                    new ColumnViewerInformationControlToolTipSupport(contentOutlineViewer,
                            new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, contentOutlineViewer));

                    // Make sure our popups work.
                    //
                    createContextMenuFor(contentOutlineViewer);

                    if (!editingDomain.getResourceSet().getResources().isEmpty()) {
                        // Select the root object in the view.
                        //
                        contentOutlineViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet()
                                .getResources().get(0)), true);
                    }
                }

                @Override
                public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager,
                        IStatusLineManager statusLineManager) {
                    super.makeContributions(menuManager, toolBarManager, statusLineManager);
                    contentOutlineStatusLineManager = statusLineManager;
                }

                @Override
                public void setActionBars(IActionBars actionBars) {
                    super.setActionBars(actionBars);
                    getActionBarContributor().shareGlobalActions(this, actionBars);
                }
            }

            contentOutlinePage = new MyContentOutlinePage();

            // Listen to selection so that we can handle it is a special way.
            //
            contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
                // This ensures that we handle selections correctly.
                //
                public void selectionChanged(SelectionChangedEvent event) {
                    handleContentOutlineSelection(event.getSelection());
                }
            });
        }

        return contentOutlinePage;
    }

    @Override
    public IPropertySheetPage getPropertySheetPage() {
        PropertySheetPage propertySheetPage =
        // new ExtendedPropertySheetPage(editingDomain, ExtendedPropertySheetPage.Decoration.LIVE,
        // EcoreEditorPlugin.getPlugin().getDialogSettings())
        new ExtendedPropertySheetPage(editingDomain, ExtendedPropertySheetPage.Decoration.NONE, EcoreEditorPlugin
                .getPlugin().getDialogSettings()) {
            @Override
            public void setSelectionToViewer(List<?> selection) {
                CustomizedEcoreEditor.this.setSelectionToViewer(selection);
                CustomizedEcoreEditor.this.setFocus();
            }

            @Override
            public void setActionBars(IActionBars actionBars) {
                super.setActionBars(actionBars);
                getActionBarContributor().shareGlobalActions(this, actionBars);
            }
        };
        propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
        propertySheetPages.add(propertySheetPage);

        return propertySheetPage;
    }
}
