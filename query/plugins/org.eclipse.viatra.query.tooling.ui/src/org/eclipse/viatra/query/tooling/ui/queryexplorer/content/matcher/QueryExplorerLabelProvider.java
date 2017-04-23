/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

/**
 * A {@link ColumnLabelProvider} implementation of the {@link QueryExplorer}.
 * 
 * @author Tamas Szabo (itemis AG)
 *
 */
public class QueryExplorerLabelProvider extends ColumnLabelProvider {

    private List<ILabelProviderListener> listeners;

    public QueryExplorerLabelProvider() {
        listeners = new ArrayList<ILabelProviderListener>();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return property.matches("text");
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public Image getImage(Object element) {
        ImageRegistry imageRegistry = ViatraQueryGUIPlugin.getDefault().getImageRegistry();

        if (element instanceof PatternMatcherRootContent) {
            PatternMatcherRootContent root = (PatternMatcherRootContent) element;
            if (root.isTainted() || root.getStatus().getSeverity() == IStatus.ERROR) {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_ERROR);
            } else {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_ROOT);
            }
        } else if (element instanceof PatternMatcherContent) {
            if (((PatternMatcherContent) element).isCreated()) {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_MATCHER);
            } else {
                return imageRegistry.get(ViatraQueryGUIPlugin.ICON_ERROR);
            }
        } else if (element instanceof PatternMatchContent) {
            return imageRegistry.get(ViatraQueryGUIPlugin.ICON_MATCH);
        } else {
            return null;
        }
    }

    @Override
    public String getText(Object element) {
        if (element instanceof BaseContent<?>) {
            return ((BaseContent<?>) element).getText();
        }
        return null;
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof PatternMatcherRootContent) {
            IStatus status = ((PatternMatcherRootContent) element).getStatus();
            if (!status.isOK()) {
                return String.format("%s. For details, check the Error Log view.", status.getMessage());
            }
        } else if (element instanceof PatternMatcherContent) {
        	final PatternMatcherContent pmContent = (PatternMatcherContent) element;
        	final Exception exception = pmContent.getException();
			final List<PProblem> pProblems = pmContent.getSpecification().getInternalQueryRepresentation().getPProblems();
			
			if (! pProblems.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("The following problems have been detected in the query specification: \n");
				for (PProblem pProblem : pProblems) {
					sb.append(String.format(" * %s%n", pProblem.getShortMessage()));
				}
				return sb.toString();
			} else if (exception != null)  {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println("The following exception occured during query evaluation:");
				final String msg = exception.getLocalizedMessage();
				final String exceptionClassName = exception.getClass().getName();
				if (msg == null) 
					pw.println("\t " + exceptionClassName);
				else 
					pw.println("\t " + exceptionClassName + ": " + msg);
				pw.println("See Error Log for detailed stack trace.");
				//exception.printStackTrace(pw);
				return sw.toString(); 
			}
        }
        return super.getToolTipText(element);
    }

    @Override
    public Color getForeground(Object element) {
        Display display = Display.getCurrent();
        if (element instanceof PatternMatcherContent) {
            PatternMatcherContent matcher = (PatternMatcherContent) element;
            if (matcher.isGenerated()) {
                return display.getSystemColor(SWT.COLOR_DARK_GRAY);
            }
        }
        return display.getSystemColor(SWT.COLOR_BLACK);
    }
}
