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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.presentation.EcoreEditorPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.inject.Inject;

/**
 * Customized Ecore editor that avoids the dangerous {@link DiagnosticDecorator} of the superclass.
 * 
 * It also uses a trick to initialize the Query Specification Registry (necessary for SettingDelegate-supported 
 * query-based derived features) from on a separate, properly set up (classpath-wise) Xtext-resourceset,
 * which will be filled with all the .EIQ files from the project of the file the editor is opened on.
 * 
 * Based on verbatim copy of sections of {@link EcoreEditor}.
 * 
 * @author istvanrath
 * 
 */
public class CustomizedEcoreEditor extends EcoreEditor {
    
	@Inject
	IResourceSetProvider provider;

	
	private void log(String msg) {
		//System.out.println(msg);
		EcoreEditorPlugin.INSTANCE.log(msg);
	}
	
	private void log(Exception e) {
		//e.printStackTrace();
		EcoreEditorPlugin.INSTANCE.log(e);
	}
	
    private Set<String> specs = new HashSet<String>();

    @Override
    public void init(IEditorSite site, IEditorInput editorInput) {
    	initializeRegistryFromSeparateResourceSet(editorInput);
        super.init(site, editorInput);
    }
    
    // doesn't work, as the classpath for the "main" resourceset is not set up properly
	private void initializeRegistryFromMainResourceSet() {
        for (Resource resource : editingDomain.getResourceSet().getResources()) {
            if (resource.getURI().toString().endsWith(".eiq")) {
                    initReg(resource);
            }
        }
    }
	 
	private void initializeRegistryFromSeparateResourceSet(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			IFileEditorInput finput = (IFileEditorInput)input;
			// this is where the Xtext magic happens :-)
			final ResourceSet resourceSet = provider.get(finput.getFile().getProject());
			// load all EIQs from this project
			try {
				finput.getFile().getProject().accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource instanceof IFile) {
							if ( ((IFile)resource).getFileExtension().equalsIgnoreCase("eiq") ) {
								URI fileURI = URI.createPlatformResourceURI(((IFile)resource).getFullPath().toString(),true);
								Resource patternResource = resourceSet.getResource(fileURI, true);
								if (patternResource!=null) {
									initReg(patternResource);
								}
							}
							return false;
						}
						return true;
					}
				});
			} catch (CoreException e) {
				log(e);
			}
		}
	}
	
	private void initReg(Resource patternResource) {
		if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
			log("Registering derived feature patterns from "+patternResource.getURI());
            EObject topElement = patternResource.getContents().get(0);
            if (topElement instanceof PatternModel) {
                for (Pattern pattern : ((PatternModel) topElement).getPatterns()) {
                	String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                	if (!specs.contains(fqn)) {
                		IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> spec = QuerySpecificationRegistry.getOrCreateQuerySpecification(pattern);
                        QuerySpecificationRegistry.registerQuerySpecification(spec);
                        specs.add(fqn);
                        log("Registered derived feature pattern " + fqn);	
                	}
                	else {
                		log("Skipped the duplicate registration of derived feature pattern " + fqn);
                	}
                }
            }
        } 
		else {
			log("Skipping derived feature pattern registration from "+patternResource.getURI()+" due to the number of errors: "+patternResource.getErrors().size());
			for (org.eclipse.emf.ecore.resource.Resource.Diagnostic d : patternResource.getErrors()) {
				log("\t"+d.getMessage());
			}
		}
	}


    
    @Override
    public void dispose() {
        super.dispose();
        for (String spec : specs) {
            QuerySpecificationRegistry.unregisterQuerySpecification(spec);
            log("Unregistered "+spec);
        }
    }
	
    

    
	@Override
	public void createPages() {
	    createModel();

	    //initializeRegistryFromMainResourceSet();
	    
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
            		 // XXX modification by istvanrath
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
                    		 // XXX modification by istvanrath
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
        // XXX modification by istvanrath
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
