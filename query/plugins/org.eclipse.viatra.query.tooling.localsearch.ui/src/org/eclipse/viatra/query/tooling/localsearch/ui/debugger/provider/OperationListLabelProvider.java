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
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.query.runtime.localsearch.operations.ExtendOperationExecutor;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.viatra.query.tooling.localsearch.ui.LocalSearchToolingActivator;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.IPlanNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.OperationKind;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.PatternBodyNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationNode;

/**
 * Label provider class for the search plan tree viewer
 * 
 * @author Marton Bur
 *
 */
public class OperationListLabelProvider extends StyledCellLabelProvider {

    // TODO proper resource management
    private LocalResourceManager localResourceManager;
    
    private ImageRegistry imageRegistry = LocalSearchToolingActivator.getDefault().getImageRegistry();

    @Override
    public void update(final ViewerCell cell) {

        localResourceManager = new LocalResourceManager(JFaceResources.getResources(Display.getCurrent()));

        StyledString text = new StyledString();
        final IPlanNode node = (IPlanNode) cell.getElement();
        text.setStyle(0, text.length(), new Styler() {
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = localResourceManager.createFont(FontDescriptor.createFrom("Arial", 10, SWT.BOLD));
                doColoring(node, textStyle);
            }
        });
        text.append(node.getLabelText());
        if (node instanceof PatternBodyNode) {
            text.append("Pattern Body");
        } else if (node instanceof SearchOperationNode) {
            cell.setImage(imageRegistry.get(LocalSearchToolingActivator.ICON_APPLIED_OPERATION));
        }
        
        switch (node.getOperationStatus()) {
        case EXECUTED:
            break;
        case CURRENT:
            cell.setImage(imageRegistry.get(LocalSearchToolingActivator.ICON_CURRENT_OPERATION));
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
            cell.setImage(imageRegistry.get(LocalSearchToolingActivator.ICON_NOT_APPLIED_OPERATION));
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

    private OperationKind calculateOperationKind(ISearchOperation searchOperation) {
        if (searchOperation instanceof ExtendOperationExecutor) {
            return OperationKind.EXTEND;
        } else if (searchOperation instanceof NACOperation) {
            return OperationKind.NAC;
        } else if (searchOperation instanceof CountOperation) {
            return OperationKind.COUNT;
        } else {
            // This case there is a check operation
            return OperationKind.CHECK;
        }
    }
    
    private void doColoring(IPlanNode node, TextStyle textStyle) {
        if (node instanceof SearchOperationNode) {
            doColoring(((SearchOperationNode) node).getSearchOperation(), textStyle);
        }
        if (node.isBreakpointSet()) {
            textStyle.borderStyle = SWT.BORDER_SOLID;
            textStyle.borderColor = localResourceManager.createColor(new RGB(200, 0, 0));
        }
    }
    
    private void doColoring(ISearchOperation searchOperation, TextStyle textStyle) {
        switch (calculateOperationKind(searchOperation)) {
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
        }
       
    }

    @Override
    public void dispose() {
        if(localResourceManager != null){
            localResourceManager.dispose();
        }
        super.dispose();
    }

}
