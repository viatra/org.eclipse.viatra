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
package org.eclipse.incquery.tooling.localsearch.ui.debugger.provider;

import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.incquery.tooling.localsearch.ui.LocalSearchToolingActivator;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Label provider class for the search plan tree viewer
 * 
 * @author Marton Bur
 *
 */
public class OperationListLabelProvider extends StyledCellLabelProvider {

    private List<SearchPlanExecutor> planExecutors = Lists.newArrayList();
    
    // TODO proper resource management
    private LocalResourceManager localResourceManager;
    
    private static final Image notAppliedOperationImage;
    private static final Image appliedOperationImage;
    private static final Image currentOperationImage;

	private Map<Object, SearchPlanExecutor> dummyMatchOperationMappings = Maps.newHashMap();

	static {
		// notAppliedOperationImage = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_ELCL_SYNCED);
		// notAppliedOperationImage = JavaDebugImages.get(JavaDebugImages.IMG_OBJS_CONTENDED_MONITOR);
		// currentOperationImage = DebugPluginImages.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN);
		notAppliedOperationImage = AbstractUIPlugin.imageDescriptorFromPlugin(LocalSearchToolingActivator.PLUGIN_ID, "/icons/help_contents.gif").createImage();
		currentOperationImage = AbstractUIPlugin.imageDescriptorFromPlugin(LocalSearchToolingActivator.PLUGIN_ID, "icons/nav_go.gif").createImage();
		appliedOperationImage = AbstractUIPlugin.imageDescriptorFromPlugin(LocalSearchToolingActivator.PLUGIN_ID, "/icons/complete_status.gif").createImage();
	}

	@Override
	public void update(final ViewerCell cell) {

		localResourceManager = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));

		final SearchOperationViewerNode node = (SearchOperationViewerNode) cell.getElement();

		StyledString text = new StyledString();

		text.append(node.getLabelText());

		switch (node.getOperationStatus()) {
		case EXECUTED:
			cell.setImage(appliedOperationImage);
			text.setStyle(0, text.length(), new Styler() {
				public void applyStyles(TextStyle textStyle) {
					textStyle.font = localResourceManager.createFont(FontDescriptor.createFrom("Arial", 10, SWT.BOLD));
					doColoring(node, textStyle);
				}
			});
			break;
		case CURRENT:
			cell.setImage(currentOperationImage);
			text.setStyle(0, text.length(), new Styler() {
				public void applyStyles(TextStyle textStyle) {
					LocalResourceManager localResMan = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));
					textStyle.font = localResMan.createFont(FontDescriptor.createFrom("Arial", 10, SWT.BOLD | SWT.ITALIC));
					doColoring(node, textStyle);
					textStyle.background = localResourceManager.createColor(new RGB(200, 235, 255));
				}
			});
			break;
		case QUEUED:
			cell.setImage(notAppliedOperationImage);
			text.setStyle(0, text.length(), new Styler() {
				public void applyStyles(TextStyle textStyle) {
					LocalResourceManager localResMan = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));
					textStyle.font = localResMan.createFont(FontDescriptor.createFrom("Arial", 10, SWT.NORMAL));
					doColoring(node, textStyle);
				}
			});
			break;
		default:
			throw new UnsupportedOperationException("Unknown operation status: " + node.getOperationStatus());
		}

		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());

		super.update(cell);
	}

    private void doColoring(SearchOperationViewerNode node, TextStyle textStyle) {
        switch (node.getOperationKind()) {
		case EXTEND:
			textStyle.foreground = localResourceManager.createColor(new RGB(0, 200, 0));
			break;
		case COUNT:
			textStyle.foreground = localResourceManager.createColor(new RGB(200,200,200));
			break;
		case NAC:
			textStyle.foreground = localResourceManager.createColor(new RGB(230,0,0));
			break;
		case CHECK:
			textStyle.foreground = localResourceManager.createColor(new RGB(100, 100, 100));
			break;
		case MATCH:
			textStyle.foreground = localResourceManager.createColor(new RGB(0, 0, 255));
			break;
		default:
			throw new UnsupportedOperationException("Unknown operation kind: " + node.getOperationKind());
		}
        
        if(node.isBreakpoint()){
            textStyle.borderStyle = SWT.BORDER_SOLID;
            textStyle.borderColor = localResourceManager.createColor(new RGB(200, 0, 0));
        }
    }

    public List<SearchPlanExecutor> getPlanExecutorList() {
        return this.planExecutors;
    }

    @Override
    public void dispose() {
        if(localResourceManager != null){
            localResourceManager.dispose();
        }
        
        appliedOperationImage.dispose();
        currentOperationImage.dispose();
        notAppliedOperationImage.dispose();
        
        super.dispose();
    }

    public void createDummyMatchOperationMapping(Object dummyOperation, SearchPlanExecutor inputElement) {
		this.dummyMatchOperationMappings.put(dummyOperation, inputElement);
	}
    public Object getDummyMatchOperation(SearchPlanExecutor planExecutor) {
    	for (Object key : dummyMatchOperationMappings.keySet()) {
			if(dummyMatchOperationMappings.get(key).equals(planExecutor)){
				return key;
			}
		}
		return null;
	}

}
