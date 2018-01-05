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
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;

/**
 * 
 * This class is only for testing and introductory purposes and should be replaced soon.
 * 
 * @author Marton Bur
 *
 */
public class StartLocalSearchHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        try {
            final ISelection selection = HandlerUtil.getCurrentSelection(event);
            if (selection instanceof IStructuredSelection) {
                final Object obj = ((IStructuredSelection) selection).iterator().next();
                if (obj instanceof IFilteredMatcherContent<?>) {
                    IFilteredMatcherContent<?> content = (IFilteredMatcherContent<?>) obj;
                    ViatraQueryMatcher<?> matcher = content.getMatcher();
                    
                    final IQuerySpecification<?> specification = matcher.getSpecification();
                    final AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.from(matcher.getEngine());
                    final Object[] adornment = content.getFilterMatch().toArray();

                    LocalSearchDebugView localSearchDebugView = (LocalSearchDebugView) HandlerUtil
                            .getActiveWorkbenchWindow(event).getActivePage().showView(LocalSearchDebugView.ID);
                    localSearchDebugView.createDebugger(engine, specification, adornment);
                }
            }
        } catch (PartInitException | ViatraQueryException | QueryProcessingException e) {
            throw new ExecutionException("Error starting local search debugger", e);
        }

        return null;
    }
}
