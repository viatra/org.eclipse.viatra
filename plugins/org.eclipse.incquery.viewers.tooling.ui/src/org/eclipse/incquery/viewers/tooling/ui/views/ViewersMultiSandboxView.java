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
package org.eclipse.incquery.viewers.tooling.ui.views;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.tooling.ui.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.google.common.collect.Lists;

/**
 *	TODO comment me 
 * @author istvanrath
 *
 * todos:
 *  - add forward reveal mode support
 */
public class ViewersMultiSandboxView extends ViewPart implements ISelectionProvider {

	static void log(String methodname, Exception e) {
		Activator
		.getDefault()
		.getLog()
		.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
				.getLocalizedMessage(), e));
	}
	
	public static final String ID = "org.eclipse.incquery.viewers.tooling.ui.multisandbox";
	
	private ViewersMultiSandboxViewComponent defaultComponent;
	private List<ViewersMultiSandboxViewComponent> additionalComponents = Lists.newArrayList();
	private ViewersMultiSandboxViewComponent currentComponent;
	SashForm container;
	
	public void setContents(Notifier model, Collection<Pattern> patterns, ViewerDataFilter filter)
            throws IncQueryException {
        if (model != null) {
        	defaultComponent.setContents(model, patterns, filter);
        	for (ViewersMultiSandboxViewComponent c : additionalComponents) {
        		c.setContents(model, patterns, filter);
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
    		//setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/collapseall_16x16.gif"));
    		setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/layout-join-vertical_16x16.png"));
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
    			for (ISelectionChangedListener l : defaultComponent.selectionChangedListeners) {
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
    		//setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/expandall_16x16.gif"));
    		setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/layout-split-vertical_16x16.png"));
    	}
    	public void run() {
    		ViewersMultiSandboxViewComponent newC = new ViewersMultiSandboxViewComponent(ViewersMultiSandboxView.this);
    		additionalComponents.add(newC);
    		// add selection changed listeners
    		for (ISelectionChangedListener l : defaultComponent.selectionChangedListeners) {
    			newC.addSelectionChangedListener(l);
    		}
    		// set contents from default
    		try {
				newC.setContents(defaultComponent.configuration);
			} catch (IncQueryException e) {
				log("addNewComponentAction.run",e);
			}
    		//container.pack();
    		container.layout();
    	};
    };
    
    boolean isVerticalSashing = true;
    
    private Action getSwitchSashingAction = new Action("Switch between horizontal and vertical mode", IAction.AS_CHECK_BOX) { 
    	{
    		setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"icons/application_tile_horizontal_16x16.png"));
    		setChecked(isVerticalSashing);
    	}
    	public void run() {
    		isVerticalSashing = !isVerticalSashing;
    		if (isVerticalSashing) {
    			container.setOrientation(SWT.VERTICAL);
    		}
    		else {
    			container.setOrientation(SWT.HORIZONTAL);
    		}
    		setChecked(isVerticalSashing);
    	};
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
	
	

}
