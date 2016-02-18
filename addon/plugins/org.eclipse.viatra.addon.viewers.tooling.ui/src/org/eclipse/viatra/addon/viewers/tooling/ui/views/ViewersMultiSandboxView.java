/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.addon.viewers.tooling.ui.ViewersToolingPlugin;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import com.google.common.collect.Lists;

/**
 * 
 * View to aid developing queries for VIATRA Viewers.
 * 
 * It supports displaying models based on the
 * {@value ViewersToolingViewsUtil#SANDBOX_TAB_EXTENSION_ID} extension implementations. Selection related requests are forwarded to the tabs.
 * 
 * Supports a compound architecture, whereby it can be "split" into several {@link ViewersMultiSandboxViewComponent}s.
 * 
 * The responsibility of this class is:
 *  - provide common utilities such as logging and view opening, initialization, property sheet
 *  - delegate business logic to its components
 *  -- selection synchronization
 *  -- content initialization
 *  - support a simple UI for multi-component operation (actions)
 * 
 * @author istvanrath
 *
 */
public class ViewersMultiSandboxView extends ViewPart implements ISelectionProvider {

	public static final String ID = "org.eclipse.viatra.addon.viewers.tooling.ui.sandbox";

	static void log(String message) {
		ViewersToolingPlugin
		.getDefault()
		.getLog()
		.log(new Status(IStatus.INFO, ViewersToolingPlugin.PLUGIN_ID, message));
	}
	
	static void log(String methodname, Exception e) {
		ViewersToolingPlugin
		.getDefault()
		.getLog()
		.log(new Status(IStatus.ERROR, ViewersToolingPlugin.PLUGIN_ID, e
				.getLocalizedMessage(), e));
	}
	
	
	private ViewersMultiSandboxViewComponent defaultComponent;
	private List<ViewersMultiSandboxViewComponent> additionalComponents = Lists.newArrayList();
	private ViewersMultiSandboxViewComponent currentComponent;
	SashForm container;
	
	public void initializeContents(Notifier model, Collection<IQuerySpecification<?>> queries, ViewerDataFilter filter)
            throws ViatraQueryException {
        if (model != null) {
        	defaultComponent.initializeContents(model, queries, filter);
        	for (ViewersMultiSandboxViewComponent c : additionalComponents) {
        		c.initializeContents(model, queries, filter);
        	}
         }
    }
	
    public static ViewersMultiSandboxView getInstance() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
            return (ViewersMultiSandboxView) activeWorkbenchWindow.getActivePage().findView(ID);
        }
        return null;
    }
    
    public static void ensureOpen() {
    	 IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
         if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
        	 try {
				activeWorkbenchWindow.getActivePage().showView(ID);
			} catch (PartInitException e) {
				log("ensureOpen",e);
			}
         }
    }
	
	protected ViewersMultiSandboxViewComponent getCurrentComponent() {
		return currentComponent;
	}

	protected void setCurrentComponent(ViewersMultiSandboxViewComponent newCurrent) {
		ViewersMultiSandboxViewComponent previous = getCurrentComponent();
		if (previous!=null) {
			previous.setBackGround();
		}
		this.currentComponent = newCurrent;
		newCurrent.setForeground();
		// refresh toolbar
		fillToolBar(newCurrent);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		//container = new Composite(parent, SWT.NONE);
		//container.setLayout(new GridLayout(1, false));

		container = new SashForm(parent, SWT.VERTICAL);
		
		// initialize the default component
		defaultComponent = new ViewersMultiSandboxViewComponent(this);
		defaultComponent.setForeground();
		this.currentComponent = defaultComponent;
				
		fillToolBar(getCurrentComponent());
		getSite().setSelectionProvider(this);
		getSite().getPage().addSelectionListener(forwardRevealListener);
	}

	
	@Override
	public void dispose() {
		defaultComponent.dispose();
		for (ViewersMultiSandboxViewComponent c : additionalComponents) {
			c.dispose();
		}
		getSite().getPage().removeSelectionListener(forwardRevealListener);
		super.dispose();
	}
	
	
	@Override
	public void setFocus() {
		if (getCurrentComponent()!=null) {
			getCurrentComponent().setFocus();
		}
	}
	
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter.equals(IPropertySheetPage.class)) {
            PropertySheetPage propertySheetPage = new PropertySheetPage();
            propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(new ComposedAdapterFactory(
                    ComposedAdapterFactory.Descriptor.Registry.INSTANCE)));
            return propertySheetPage;
        }
        return super.getAdapter(adapter);
    }
	
	// this should be called each time the current component is changed OR the tab inside a component is changed
    void fillToolBar(ViewersMultiSandboxViewComponent c) {
    	// add items for the multi operation
    	
    	IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    	mgr.removeAll();
    	mgr.add(getSwitchSashingAction);
    	mgr.add(getAddNewComponentAction);
    	mgr.add(getCloseCurrentComponentAction);
    	mgr.add(new Separator());
    	
    	c.fillToolBarBasedOnCurrentTab();
    	
        getViewSite().getActionBars().updateActionBars();
    }
    
    private Action getCloseCurrentComponentAction = new Action("Close current component") {
    	{
    		setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/collapse.gif"));
    		//setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/layout-join-vertical_16x16.png"));
//    		setEnabled(isEnabled());
    	}
    	public void run() {
    		if (defaultComponent.equals(currentComponent)) {
    			// do nothing, we cannot close the default component
    		}
    		else {
    			// set the default as the current
    			ViewersMultiSandboxViewComponent target = getCurrentComponent();
    			setCurrentComponent(defaultComponent);
    			// remove selection changed listeners
    			for (ISelectionChangedListener l : defaultComponent.selectionHelper.selectionChangedListeners) {
    				target.removeSelectionChangedListener(l);
    			}
    			target.dispose();
    			additionalComponents.remove(target);
    			container.layout();
    		}
    	};
    	
//    	public boolean isEnabled() {
//    		if (defaultComponent!=null && currentComponent!=null) {
//    			return (!defaultComponent.equals(currentComponent));
//    		}
//    		else return false;
//    	};
    };
    
    private Action getAddNewComponentAction = new Action("Create new component") {
    	{
    		setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/expand.gif"));
    		//setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/layout-split-vertical_16x16.png"));
    	}
    	public void run() {
    		ViewersMultiSandboxViewComponent newC = new ViewersMultiSandboxViewComponent(ViewersMultiSandboxView.this);
    		additionalComponents.add(newC);
    		// add selection changed listeners
    		for (ISelectionChangedListener l : defaultComponent.selectionHelper.selectionChangedListeners) {
    			newC.addSelectionChangedListener(l);
    		}
    		// set contents from default
    		try {
				newC.initializeContents(defaultComponent.initialConfiguration);
			} catch (ViatraQueryException e) {
				log("addNewComponentAction.run",e);
			}
    		//container.pack();
    		container.layout();
    	};
    };
    
    boolean isVerticalSashing = true;
    
    private Action getSwitchSashingAction = new Action("Switch between horizontal and vertical mode", IAction.AS_CHECK_BOX) { 
    	{
    		setState();
    	}
    	public void run() {
    		isVerticalSashing = !isVerticalSashing;
    		if (isVerticalSashing) {
    			container.setOrientation(SWT.VERTICAL);
    		}
    		else {
    			container.setOrientation(SWT.HORIZONTAL);
    		}
    		setState();
    	};
    	
    	void setState() {
    		setChecked(isVerticalSashing);
    		if (isVerticalSashing) {
    			setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/vertical.gif"));
    		}
    		else {
    			setImageDescriptor(ViewersToolingPlugin.imageDescriptorFromPlugin(ViewersToolingPlugin.PLUGIN_ID,"icons/horizontal.gif"));
    		}
    	}
    };
    
    
    // selection
    
    
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		defaultComponent.addSelectionChangedListener(listener);
		for (ViewersMultiSandboxViewComponent c : additionalComponents) {
			c.addSelectionChangedListener(listener);
		}
//		getCurrentComponent().addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return getCurrentComponent().getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
//		getCurrentComponent().removeSelectionChangedListener(listener);
		defaultComponent.removeSelectionChangedListener(listener);
		for (ViewersMultiSandboxViewComponent c : additionalComponents) {
			c.removeSelectionChangedListener(listener);
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		getCurrentComponent().setSelection(selection);		
	}
	
	/**
     * Selection listener for the "forward reveal" feature.
     */
    private final ISelectionListener forwardRevealListener = new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            if (!ViewersMultiSandboxView.this.equals(part) && selection instanceof IStructuredSelection) {
                ArrayList<Notifier> r = new ArrayList<Notifier>();
                for (Object _target : ((IStructuredSelection) selection).toArray()) {
                    if (_target instanceof Notifier) {
                        r.add((Notifier) _target);
                    }
                }
                setSelection(new StructuredSelection(r));
            }
        }
    };
	

}
