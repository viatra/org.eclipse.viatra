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
package org.eclipse.viatra.query.application.common;



import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry;

/**
 * @author Abel Hegedus
 * @author Istvan Rath
 *
 */
public class ViatraQueryHeadlessAdvanced extends ViatraQueryHeadless {

	/**
	 * Returns the match set for patternFQN over the model in modelPath in pretty printed form
	 * 
	 * @param modelPath
	 * @param patternFQN
	 * @return
	 */
	public String executeDemo_GenericAPI_LoadFromVQL(URI modelURI, URI fileURI, String patternFQN) {
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelURI);
		if (resource != null) {
			try {
				// get all matches of the pattern
				// create an *unmanaged* engine to ensure that noone else is going
				// to use our engine
				AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resource));
				// instantiate a pattern matcher through the registry, by only knowing its FQN
				// assuming that there is a pattern definition registered matching 'patternFQN'
				
				Pattern p = null;
				
		        // Initializing Xtext-based resource parser
		        new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
		        
				//Loading pattern resource from file
		        ResourceSet resourceSet = new ResourceSetImpl();
				
			    Resource patternResource = resourceSet.getResource(fileURI, true);
			    
			    // navigate to the pattern definition that we want
			    if (patternResource != null) {
		            if (patternResource.getErrors().size() == 0 && patternResource.getContents().size() >= 1) {
		                EObject topElement = patternResource.getContents().get(0);
		                if (topElement instanceof PatternModel) {
		                	for (Pattern _p  : ((PatternModel) topElement).getPatterns()) {
		                		if (patternFQN.equals(CorePatternLanguageHelper.getFullyQualifiedName(_p))) {
		                			p = _p; break;
		                		}
		                	}
		                }
		            }
		        }
			    if (p == null) {
			        throw new RuntimeException(String.format("Pattern %s not found", patternFQN));
			    }
			    SpecificationBuilder builder = new SpecificationBuilder();
			    final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> specification = builder.getOrCreateSpecification(p);
			    QuerySpecificationRegistry.registerQuerySpecification(specification);
			    
			    // Initialize matcher from specification
                ViatraQueryMatcher<? extends IPatternMatch> matcher = engine.getMatcher(specification);
				
				if (matcher!=null) {
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
			} catch (ViatraQueryException e) {
				e.printStackTrace();
				results.append(e.getMessage());
			}
		} else {
			results.append("Resource not found");
		}
		return results.toString();
	}

	
	// incrementally track changes
	
	
	public String executeTrackChangesDemo_Advanced(URI modelURI, String patternFQN)
	{
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelURI);
		if (resource != null) {
			try {
				// initialization
				// phase 1: (managed) ViatraQueryEngine
				AdvancedViatraQueryEngine engine = AdvancedViatraQueryEngine.createUnmanagedEngine(new EMFScope(resource));
				// phase 2: pattern matcher for packages
				ViatraQueryMatcher<? extends IPatternMatch> matcher = QuerySpecificationRegistry.getQuerySpecification(patternFQN).getMatcher(engine);
				matcher.forEachMatch(new IMatchProcessor<IPatternMatch>() {
					@Override
					public void process(IPatternMatch match) {
						results.append("\tMatch before modification: " + match.prettyPrint()+"\n");
					}
				});
				// phase 3: prepare for advanced change processing
				changeProcessing_lowlevel(results, matcher, engine);
				// phase 4: modify model, change processor will update results accordingly
				performModelModification(resource);
			}
			catch (ViatraQueryException e) {
					e.printStackTrace();
					results.append(e.getMessage());
				}
		} else {
			results.append("Resource not found");
		}
		return results.toString();
	}

	private void changeProcessing_lowlevel(final StringBuilder results, 
			ViatraQueryMatcher<? extends IPatternMatch> matcher,
			AdvancedViatraQueryEngine engine) {
		// (+) these update callbacks are called whenever there is an actual change in the
		// result set of the pattern you are interested in. Hence, they are called fewer times
		// than the "afterUpdates" option, giving better performance.
		// (-)  the downside is that the callbacks are *not* guaranteed to be called in a consistent
		// state (i.e. when the update propagation is settled), hence
		//  * you must not invoke pattern matching and model manipulation _inside_ the callback method
		//  * the callbacks might encounter "hazards", i.e. when an appearance is followed immediately by a disappearance.
		engine.addMatchUpdateListener(matcher, new IMatchUpdateListener<IPatternMatch>() {
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
	
	
}
