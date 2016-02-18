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
package org.eclipse.viatra.integration.xcore.modeleditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.QueryBasedFeatureSettingDelegateFactory;
import org.eclipse.viatra.addon.querybasedfeatures.runtime.handler.QueryBasedFeatures;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry;
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
 * @author Istvan Rath
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class CustomizedEcoreEditor extends EcoreEditor {
    
	@Inject
	IResourceSetProvider provider;

	
	private void log(String msg) {
		//System.out.println(msg);
		EcoreEditorPlugin.INSTANCE.log(msg);
	}
	
    private Set<String> specs = new HashSet<String>();

    @Override
    public void init(IEditorSite site, IEditorInput editorInput) {
    	//initializeRegistryFromSeparateResourceSet_WorkspaceTraversal(editorInput);
        super.init(site, editorInput);
        site.getPage().addSelectionListener(revealSelectionListener);
    }
	
	private void initializeRegistryFromSeparateResourceSet_MainResourceTraversal(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			// this is where the Xtext magic happens :-)
			final ResourceSet resourceSet = provider.get(((IFileEditorInput)input).getFile().getProject());
			// load all EIQs from the main resourceset again, but in a different resourceset
			for (Resource resource : editingDomain.getResourceSet().getResources()) {
	            if (resource.getURI().toString().endsWith(".eiq")) {
	            	Resource patternResource = resourceSet.getResource(resource.getURI(), true);
	            	if (patternResource!=null) {
	            		initReg(patternResource);
	            	}
	            }
	        }
		}
	}
	 
	// Copied from EcoreEditor.doSave but modified the looping over the Resources to avoid the ConcurrentModificationException
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
	 // Save only resources that have actually changed.
	    //
	    final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
	    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
	    saveOptions.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

	    // Do the work within an operation because this is a long running activity that modifies the workbench.
	    //
	    WorkspaceModifyOperation operation =
	      new WorkspaceModifyOperation()
	      {
	        // This is the method that gets invoked when the operation runs.
	        //
	        @Override
	        public void execute(IProgressMonitor monitor)
	        {
	          // Save the resources to the file system.
	          //
	          boolean first = true;
	          
	          // modified looping to avoid the ConcurrentModificationException
	          Set<Resource> processedResources = new HashSet<Resource>(); 
	          while (processedResources.size() != editingDomain.getResourceSet().getResources().size()) {
	              Set<Resource> resources = new HashSet<Resource>(editingDomain.getResourceSet().getResources());
	              resources.removeAll(processedResources);
	              for (Resource resource : resources) {
	                  if ((first || !resource.getContents().isEmpty() || isPersisted(resource)) && !editingDomain.isReadOnly(resource))
	                  {
	                    try
	                    {
	                      long timeStamp = resource.getTimeStamp();
	                      resource.save(saveOptions);
	                      if (resource.getTimeStamp() != timeStamp)
	                      {
	                        savedResources.add(resource);
	                      }
	                    }
	                    catch (Exception exception)
	                    {
	                      resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
	                    }
	                    first = false;
	                  }
	                  processedResources.add(resource);
	              }
	          }
	        }
	      };

	    updateProblemIndication = false;
	    try
	    {
	      // This runs the options, and shows progress.
	      //
	      new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);

	      // Refresh the necessary state.
	      //
	      ((BasicCommandStack)editingDomain.getCommandStack()).saveIsDone();
	      firePropertyChange(IEditorPart.PROP_DIRTY);
	    }
	    catch (Exception exception)
	    {
	      // Something went wrong that shouldn't.
	      //
	      EcoreEditorPlugin.INSTANCE.log(exception);
	    }
	    updateProblemIndication = true;
	    updateProblemIndication();
	}
	
	private void initReg(Resource patternResource) {
		if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
			log("Registering derived feature patterns from "+patternResource.getURI());
			SpecificationBuilder builder = new SpecificationBuilder(QuerySpecificationRegistry.getContributedQuerySpecifications());
            EObject topElement = patternResource.getContents().get(0);
            if (topElement instanceof PatternModel) {
                for (Pattern pattern : ((PatternModel) topElement).getPatterns()) {
                	String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                	if (!specs.contains(fqn)) {
                		IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> spec;
						try {
							spec = builder.getOrCreateSpecification(pattern);
						} catch (ViatraQueryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
                        
                		QueryBasedFeatureSettingDelegateFactory f = (QueryBasedFeatureSettingDelegateFactory) EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.get(QueryBasedFeatures.ANNOTATION_SOURCE);
                		if (f!=null)
                		{
                			f.getSpecificationMap().put(fqn, spec);
                			log("Registered derived feature pattern directly through QBFSDF " + fqn);	
                		}
                		else {
                			QuerySpecificationRegistry.registerQuerySpecification(spec);
                			log("Registered derived feature pattern indirectly through QSR " + fqn);	
                		}    
                        specs.add(fqn);
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
    	getSite().getPage().removeSelectionListener(revealSelectionListener);
        super.dispose();
        for (String spec : specs) {
        	QueryBasedFeatureSettingDelegateFactory f = (QueryBasedFeatureSettingDelegateFactory) EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.get(QueryBasedFeatures.ANNOTATION_SOURCE);
    		if (f!=null)
    		{
    			f.getSpecificationMap().remove(spec);
    		}
    		else {
    			QuerySpecificationRegistry.unregisterQuerySpecification(spec);
    		}
            log("Unregistered "+spec);
        }
    }
	
    

    
	@Override
	public void createPages() {
	    createModel();

	    //initializeRegistryFromMainResourceSet(); // doesn't work :-(
	    initializeRegistryFromSeparateResourceSet_MainResourceTraversal(this.getEditorInput());
	    
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
    
    // back-reveal feature
    private final ISelectionListener revealSelectionListener = new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        	// avoid infinite loop
            if (!part.equals(CustomizedEcoreEditor.this) && selection instanceof IStructuredSelection) {
                IStructuredSelection sel = (IStructuredSelection) selection;
                ArrayList<EObject> eSel = new ArrayList<EObject>();
                for (Object _o : sel.toArray()) {
                	System.out.println("\t"+_o);
                    if (_o instanceof EObject) {
                        eSel.add((EObject) _o);
                    }
                }
                CustomizedEcoreEditor.this.setSelectionToViewer(eSel);
            }
        }
    };
    
}
