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
package org.eclipse.incquery.tooling.localsearch.ui.debugger.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.matcher.integration.LocalSearchBackendFactory;
import org.eclipse.incquery.runtime.localsearch.matcher.integration.LocalSearchResultProvider;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.incquery.tooling.localsearch.ui.debugger.LocalSearchDebugger;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.PatternMatcherContent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 * This class is only for testing and introductory purposes and should be replaced soon.
 * 
 * @author Marton Bur
 *
 */
public class StartLocalSearchHandler extends AbstractHandler {

	public static Thread planExecutorThread = null;

	@Override
	public Object execute(ExecutionEvent event) {

		try {
			final ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection) {
				final Object obj = ((IStructuredSelection) selection).iterator().next();
				PatternMatcherContent content = (PatternMatcherContent) obj;
				final IQuerySpecification<?> specification = content.getSpecification();
				final AdvancedIncQueryEngine engine = content.getParent().getKey().getEngine();
				final IQueryBackend lsBackend = engine.getQueryBackend(LocalSearchBackendFactory.INSTANCE);
				final Object[] adornment = content.getFilter();

				final LocalSearchResultProvider lsResultProvider = (LocalSearchResultProvider) lsBackend
						.getResultProvider(specification.getInternalQueryRepresentation());

				
				// Create and start the matcher thread
				Runnable planExecutorRunnable = new Runnable() {
					@Override
					public void run() {
						try {
							final LocalSearchMatcher localSearchMatcher = lsResultProvider.newLocalSearchMatcher(adornment);
							LocalSearchDebugger debugger = new LocalSearchDebugger();
							localSearchMatcher.addAdapter(debugger);
							debugger.setStartHandlerCalled(true);

							// Initiate the matching
							localSearchMatcher.getAllMatches();
						} catch (Exception e) {
							new RuntimeException(e);
						}
					}
				};

				if (planExecutorThread == null || !planExecutorThread.isAlive()) {
					// Start the matching process if not started or in progress yet
					planExecutorThread = new Thread(planExecutorRunnable);
					planExecutorThread.start();
				} else if(planExecutorThread.isAlive()){
					planExecutorThread.interrupt();
					planExecutorThread = new Thread(planExecutorRunnable);
					planExecutorThread.start();
				}
			}
		} catch (IncQueryException e) {
			throw new RuntimeException(e);
		} catch (QueryProcessingException e1) {
			throw new RuntimeException(e1);
		}

		return null;
	}
}
