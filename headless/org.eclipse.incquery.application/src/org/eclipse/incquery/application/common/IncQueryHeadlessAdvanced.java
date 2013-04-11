/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *    Istvan Rath - refactorings to accommodate to generic/patternspecific API differences
 *******************************************************************************/
package org.eclipse.incquery.application.common;


import headless.epackage.EPackageMatch;
import headless.epackage.EPackageMatcher;
import headless.epackage.EPackageProcessor;

import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;

/**
 * @author Abel Hegedus
 * @author Istvan Rath
 *
 */
public class IncQueryHeadlessAdvanced extends IncQueryHeadless {
	
	
	/**
	 * Returns the match set for patternFQN over the model in modelPath in pretty printed form
	 * 
	 * @param modelPath
	 * @param patternFQN
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String executeDemo_GenericAPI(String modelPath, String patternFQN) {
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// get all matches of the pattern
				IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngine(resource);
				IncQueryMatcher matcher = MatcherFactoryRegistry.getMatcherFactory(patternFQN).getMatcher(engine);
				// assuming that there is a pattern definition registered matching 'patternFQN'
				if (matcher!=null) {
					Collection<IPatternMatch> matches = matcher.getAllMatches();
					prettyPrintMatches(results, matches);
				}
			} catch (IncQueryException e) {
				e.printStackTrace();
				results.append(e.getMessage());
			}
		} else {
			results.append("Resource not found");
		}
		return results.toString();
	}


	
	// incrementally track changes
	
	
	public String executeTrackChangesDemo_Advanced(String modelPath)
	{
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// initialization
				// phase 1: (managed) IncQueryEngine
				IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngine(resource);
				// phase 2: pattern matcher for packages
				EPackageMatcher matcher = new EPackageMatcher(engine);
				matcher.forEachMatch(new EPackageProcessor() {
					@Override
					public void process(EPackage p) {
						results.append("\tEPackage before modification: " + p.getName()+"\n");
					}
				});
				// phase 3: prepare for change processing
				changeProcessing_lowlevel(results, matcher);
				changeProcessing_deltaMonitor(results, matcher);
				// phase 4: modify model, change processor will update results accordingly
				performModelModification(resource);
			}
			catch (IncQueryException e) {
					e.printStackTrace();
					results.append(e.getMessage());
				}
		} else {
			results.append("Resource not found");
		}
		return results.toString();
	}

	private void changeProcessing_lowlevel(final StringBuilder results, EPackageMatcher matcher) {
		// (+) these update callbacks are called whenever there is an actual change in the
		// result set of the pattern you are interested in. Hence, they are called fewer times
		// than the "afterUpdates" option, giving better performance.
		// (-)  the downside is that the callbacks are *not* guaranteed to be called in a consistent
		// state (i.e. when the update propagation is settled), hence
		//  * you must not invoke pattern matching and model manipulation _inside_ the callback method
		//  * the callbacks might encounter "hazards", i.e. when an appearance is followed immediately by a disappearance.
		matcher.addCallbackOnMatchUpdate(new IMatchUpdateListener<EPackageMatch>() {
			@Override
			public void notifyDisappearance(EPackageMatch match) {
				// left empty
			}
			@Override
			public void notifyAppearance(EPackageMatch match) {
				results.append("\tNew EPackage found by changeset low level callback: " + match.getP().getName()+"\n");
			}
		}, false);
	}
	
	private void changeProcessing_deltaMonitor(final StringBuilder results, EPackageMatcher matcher) {
		final DeltaMonitor<EPackageMatch> dm = matcher.newDeltaMonitor(false);
		// (+) these updates are guaranteed to be called in a *consistent* state,
		// i.e. when the pattern matcher is guaranteed to be consistent with the model
		// anything can be written into the callback method
		// (-) the downside is that the callback is called after *every* update
		// that propagates through the matcher, i.e. also when the updates do not actually
		// influence the result set you are interested in. Hence, the performance is somewhat
		// lower than for the "lowlevel" option.
		matcher.addCallbackAfterUpdates(new Runnable() {
			@Override
			public void run() {
				for (EPackageMatch newMatch : dm.matchFoundEvents) {
					results.append("\tNew EPackage found by changeset delta monitor: " + newMatch.getP().getName()+"\n");
				}
				for (EPackageMatch lostMatch : dm.matchLostEvents) {
					// left empty
				}
			}
		});
	}
	
	
}
