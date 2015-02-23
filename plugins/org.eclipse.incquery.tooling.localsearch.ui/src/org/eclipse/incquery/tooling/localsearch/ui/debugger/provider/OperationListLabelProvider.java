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

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.incquery.runtime.localsearch.operations.extend.ExtendOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
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

import com.google.common.collect.Lists;

/**
 * 
 * @author Marton Bur
 *
 */
@SuppressWarnings("restriction")
public class OperationListLabelProvider extends StyledCellLabelProvider {

    private List<SearchPlanExecutor> planExecutors = Lists.newArrayList();
    
    // TODO proper resource management
    private LocalResourceManager localResourceManager;
    
    private static final Image notAppliedOperationImage;
    private static final Image appliedOperationImage;
    private static final Image currentOperationImage;

    private List<ISearchOperation> breakpoints;
    
    static {
        // notAppliedOperationImage = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_ELCL_SYNCED);
        notAppliedOperationImage = JavaDebugImages.get(JavaDebugImages.IMG_OBJS_CONTENDED_MONITOR);
        currentOperationImage = DebugPluginImages.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN);
        appliedOperationImage = JavaDebugImages.get(JavaDebugImages.IMG_OBJ_JAVA_INSPECT_EXPRESSION);
    }


    public OperationListLabelProvider(List<ISearchOperation> breakpoints) {
        this.breakpoints = breakpoints;
    }

    @Override
    public void update(ViewerCell cell) {
        final ISearchOperation operation = (ISearchOperation) cell.getElement();
        StyledString text = new StyledString();
        text.append(operation.getClass().getSimpleName());

        localResourceManager = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));

        SearchPlanExecutor planExecutor = null;
		for (SearchPlanExecutor searchPlanExecutor : planExecutors) {
			if(searchPlanExecutor.getSearchPlan().getOperations().contains(operation)){
				planExecutor = searchPlanExecutor;
				break;
			}
		}
        
        if (planExecutor != null && planExecutor.getCurrentOperation() > planExecutor.getSearchPlan().getOperations().indexOf(operation)) {
            cell.setImage(appliedOperationImage);
            text.setStyle(0, text.length(), new Styler() {
                public void applyStyles(TextStyle textStyle) {
                    textStyle.font = localResourceManager.createFont(FontDescriptor.createFrom("Arial", 10, SWT.BOLD ));
                    doColoring(operation, textStyle);
                }
            });
        } else if (planExecutor != null
                && planExecutor.getCurrentOperation() == planExecutor.getSearchPlan().getOperations().indexOf(operation)) {
            cell.setImage(currentOperationImage);
            text.setStyle(0, text.length(), new Styler() {
                public void applyStyles(TextStyle textStyle) {
                    LocalResourceManager localResMan = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));
                    textStyle.font = localResMan.createFont(FontDescriptor.createFrom("Arial", 10, SWT.BOLD | SWT.ITALIC));
                    doColoring(operation, textStyle);
                    textStyle.background = localResourceManager.createColor(new RGB(200,235,255));
                }
            });
        } else {
            cell.setImage(notAppliedOperationImage);
            text.setStyle(0, text.length(), new Styler() {
                public void applyStyles(TextStyle textStyle) {
                    LocalResourceManager localResMan = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));
                    textStyle.font = localResMan.createFont(FontDescriptor.createFrom("Arial", 10, SWT.NORMAL));
                    doColoring(operation, textStyle);
                }
            });
        }
        
        cell.setText(text.toString());
        cell.setStyleRanges(text.getStyleRanges());
        super.update(cell);
    }

    private void doColoring(final ISearchOperation operation, TextStyle textStyle) {
        if(operation instanceof ExtendOperation<?>){
            textStyle.foreground = localResourceManager.createColor(new RGB(0, 200, 0));
        } else if (operation instanceof NACOperation ) {
            textStyle.foreground = localResourceManager.createColor(new RGB(230,0,0));
        } else {
            // This case there is a check operation
            textStyle.foreground = localResourceManager.createColor(new RGB(100, 100, 100));
        }
        
        if(breakpoints.contains(operation)){
            textStyle.borderStyle = SWT.BORDER_SOLID;
            textStyle.borderColor = localResourceManager.createColor(new RGB(200, 0, 0));
        }
    }

    public void addPlanExecutor(SearchPlanExecutor planExecutor) {
        this.planExecutors.add(planExecutor);
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

}
