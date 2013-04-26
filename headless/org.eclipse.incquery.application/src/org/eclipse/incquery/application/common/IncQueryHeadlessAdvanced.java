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



import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IMatchUpdateListener;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
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
	public String executeDemo_GenericAPI(String modelPath, String patternFQN) {
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// get all matches of the pattern
				// create an *unmanaged* engine to ensure that noone else is going
				// to use our engine
				IncQueryEngine engine = EngineManager.getInstance().createUnmanagedIncQueryEngine(resource);
				// instantiate a pattern matcher through the registry, by only knowing its FQN
				// assuming that there is a pattern definition registered matching 'patternFQN'
				
				Pattern p = null;
				// would be nice: IMatcherFactory<IncQueryMatcher<? extends IPatternMatch>> factory
				IMatcherFactory<?> factory = null;
				
				// use a trick to load Pattern models from a file
				ResourceSet resourceSet = new ResourceSetImpl();
				// here, we make use of the (undocumented) fact that the Pattern model is stored inside the hidden "queries" directory inside an EMF-IncQuery project
			    URI fileURI = URI.createPlatformPluginURI("headlessQueries.incquery/queries/globalEiqModel.xmi", false);
			    Resource patternResource = resourceSet.getResource(fileURI, true);
			    // navigate to the pattern definition that we want
			    if (patternResource != null) {
		            if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
		                EObject topElement = patternResource.getContents().get(0);
		                if (topElement instanceof PatternModel) {
		                	for (Pattern _p  : ((PatternModel) topElement).getPatterns()) {
		                		if (_p.getName().equals(patternFQN)) {
		                			p = _p; break;
		                		}
		                	}
		                }
		            }
		        }
			    
			    if (p!=null) {
			    	factory = MatcherFactoryRegistry.getMatcherFactory(p);
			    }
			    else {
			    	// fall back to the registry in case the pattern model extraction didn't work
			    	factory = MatcherFactoryRegistry.getMatcherFactory(patternFQN);
			    }
				
				if (factory!=null) {
					IncQueryMatcher<? extends IPatternMatch> matcher = factory.getMatcher(engine);
					Collection<? extends IPatternMatch> matches = matcher.getAllMatches();
					prettyPrintMatches(results, matches);
				}
				// wipe the engine
				engine.wipe();
				// after a wipe, new patterns can be rebuilt with much less overhead than 
				// complete traversal (as the base indexes will be kept)
				
				// completely dispose of the engine once's it is not needed
				engine.dispose();
				resource.unload();
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
	
	
	public String executeTrackChangesDemo_Advanced(String modelPath, String patternFQN)
	{
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// initialization
				// phase 1: (managed) IncQueryEngine
				IncQueryEngine engine = EngineManager.getInstance().getIncQueryEngine(resource);
				// phase 2: pattern matcher for packages
				IncQueryMatcher<? extends IPatternMatch> matcher = MatcherFactoryRegistry.getMatcherFactory(patternFQN).getMatcher(engine);
				matcher.forEachMatch(new IMatchProcessor<IPatternMatch>() {
					@Override
					public void process(IPatternMatch match) {
						results.append("\tMatch before modification: " + match.prettyPrint()+"\n");
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

	private void changeProcessing_lowlevel(final StringBuilder results, IncQueryMatcher<? extends IPatternMatch> matcher) {
		// (+) these update callbacks are called whenever there is an actual change in the
		// result set of the pattern you are interested in. Hence, they are called fewer times
		// than the "afterUpdates" option, giving better performance.
		// (-)  the downside is that the callbacks are *not* guaranteed to be called in a consistent
		// state (i.e. when the update propagation is settled), hence
		//  * you must not invoke pattern matching and model manipulation _inside_ the callback method
		//  * the callbacks might encounter "hazards", i.e. when an appearance is followed immediately by a disappearance.
		matcher.addCallbackOnMatchUpdate(new IMatchUpdateListener<IPatternMatch>() {
			@Override
			public void notifyDisappearance(IPatternMatch match) {
				// left empty
			}
			@Override
			public void notifyAppearance(IPatternMatch match) {
				results.append("\tNew match found by changeset low level callback: " + match.prettyPrint()+"\n");
			}
		}, false);
	}
	
	private void changeProcessing_deltaMonitor(final StringBuilder results, IncQueryMatcher<? extends IPatternMatch> matcher) {
		final DeltaMonitor<? extends IPatternMatch> dm = matcher.newDeltaMonitor(false);
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
				for (IPatternMatch newMatch : dm.matchFoundEvents) {
					results.append("\tNew match found by changeset delta monitor: " + newMatch.prettyPrint()+"\n");
				}
				for (IPatternMatch lostMatch : dm.matchLostEvents) {
					// left empty
				}
				dm.clear();
			}
		});
	}
	
	
}
