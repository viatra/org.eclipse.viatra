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

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.addon.databinding.runtime.api.IncQueryObservables;
import org.eclipse.viatra.query.application.queries.EClassNamesMatcher;
import org.eclipse.viatra.query.application.queries.EObjectMatch;
import org.eclipse.viatra.query.application.queries.EObjectMatcher;
import org.eclipse.viatra.query.application.queries.EPackageMatch;
import org.eclipse.viatra.query.application.queries.EPackageMatcher;
import org.eclipse.viatra.query.application.queries.HeadlessQueries;
import org.eclipse.viatra.query.application.queries.util.EClassNamesProcessor;
import org.eclipse.viatra.query.application.queries.util.EObjectProcessor;
import org.eclipse.viatra.query.application.queries.util.EPackageProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

/**
 * @author Abel Hegedus
 * @author Istvan Rath
 *
 */
public class IncQueryHeadless {

    protected Resource loadModel(String modelPath) {
        URI fileURI = URI.createFileURI(modelPath);
        return loadModel(fileURI);
    }
    
	protected Resource loadModel(URI fileURI) {
		// Loads the resource
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(fileURI, true);
		return resource;
	}
	
	protected void prettyPrintMatches(StringBuilder results, Collection<? extends IPatternMatch> matches) {
		for (IPatternMatch match : matches) {
			results.append(match.prettyPrint()+"; ");
		}
		if(matches.size() == 0) {
			results.append("Empty match set");
		}
		results.append("\n");
	}
	
	
	public String executeDemo(String modelPath) {
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// get all matches of the pattern
				// initialization
				// phase 1: (managed) IncQueryEngine
				ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resource));
				// phase 2: the matcher itself
				EObjectMatcher matcher = EObjectMatcher.on(engine);
				// get all matches of the pattern
				Collection<EObjectMatch> matches = matcher.getAllMatches();
				prettyPrintMatches(results, matches);
				// using a match processor
				matcher.forEachMatch(new EObjectProcessor() {
					@Override
					public void process(EObject o) {
						results.append("\tEObject: " + o.toString() + "\n");
					}
				});
				// matching with partially bound input parameters
				// a new matcher initialization will trigger a new traversal of the model
				// unless you use pattern groups, see executePatternSpecific_PatternGroups below
				EClassNamesMatcher matcher2 = EClassNamesMatcher.on(engine);
				// defining an input mask by binding "name" to "A" ->
				matcher2.forEachMatch( matcher2.newMatch(null, "A") , new EClassNamesProcessor() {
					@Override
					public void process(EClass c, String n) {
						results.append("\tEClass with name A: " + c.toString() + "\n");
					}
				});				
			} catch (IncQueryException e) {
				e.printStackTrace();
				results.append(e.getMessage());
			}
		} else {
			results.append("Resource not found");
		}
		return results.toString();
	}
	
	public String executeDemo_PatternGroups(String modelPath) {
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// initialization
				// phase 1: (managed) IncQueryEngine
				ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resource));
				// phase 2: the group of pattern matchers
				HeadlessQueries patternGroup = HeadlessQueries.instance();
				patternGroup.prepare(engine);
				// from here on everything is the same
				EObjectMatcher matcher = EObjectMatcher.on(engine);
				// get all matches of the pattern
				Collection<EObjectMatch> matches = matcher.getAllMatches();
				prettyPrintMatches(results, matches);
				// using a match processor
				matcher.forEachMatch(new EObjectProcessor() {
					@Override
					public void process(EObject o) {
						results.append("\tEObject: " + o.toString() + "\n");
					}
				});
				// matching with partially bound input parameters
				// because EClassNamesMatcher is included in the patterngroup, *no new traversal* will be done here
				EClassNamesMatcher matcher2 = EClassNamesMatcher.on(engine);
				// defining an input mask by binding "name" to "A" ->
				matcher2.forEachMatch(null, "A" , new EClassNamesProcessor() {
					@Override
					public void process(EClass c, String n) {
						results.append("\tEClass with name A: " + c.toString() + "\n");
					}
				});	
				// projections
				for (EClass ec: matcher2.getAllValuesOfc(matcher2.newMatch(null,"A")))
				{
					results.append("\tEClass with name A: " + ec.toString() + "\n");
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
	
	protected void performModelModification(Resource res) {
		// somewhat brittle code, assumes there is a root EPackage in the Resource
		EPackage rootPackage = (EPackage)res.getContents().get(0);
		// add a new EPackage
		EPackage newPackage = EcoreFactory.eINSTANCE.createEPackage();
		newPackage.setName("NewPackage");
		rootPackage.getESubpackages().add(newPackage);
	}
	
	public String executeTrackChangesDemo(String modelPath)
	{
		final StringBuilder results = new StringBuilder();
		Resource resource = loadModel(modelPath);
		if (resource != null) {
			try {
				// initialization
				// phase 1: (managed) IncQueryEngine
				ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resource));
				// phase 2: pattern matcher for packages
				EPackageMatcher matcher = EPackageMatcher.on(engine);
				matcher.forEachMatch(new EPackageProcessor() {
					@Override
					public void process(EPackage p) {
						results.append("\tEPackage before modification: " + p.getName()+"\n");
					}
				});
				// phase 3: prepare for change processing
				changeProcessing_databinding(results, matcher);
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


	
	private void changeProcessing_databinding(final StringBuilder results, ViatraQueryMatcher<?> matcher) {
		// (+) changes can also be tracked using JFace Databinding
		// this approach provides good performance, as the observable callbacks are guaranteed to be called
		//   in a consistent state, and only when there is a relevant change; anything
		//   can be written into the callback method
		// (-) * the databinding API introduces additional dependencies
		//     * is does not support generics, hence typesafe programming is not possible
		//     * a "Realm" needs to be set up for headless execution
		DefaultRealm realm = new DefaultRealm();
		IObservableSet set = IncQueryObservables.observeMatchesAsSet(matcher);
		
		set.addSetChangeListener(new ISetChangeListener() {
			@Override
			public void handleSetChange(SetChangeEvent event) {
				for (Object _o : event.diff.getAdditions()) {
					if (_o instanceof EPackageMatch) {
						results.append("\tNew EPackage found by changeset databinding: " + ((EPackageMatch)_o).getP().getName()+"\n");
					}
				}
				for (Object _o : event.diff.getRemovals()) {
					// left empty
				}
			}
		});
		
		/* the same could also be done with a list:
		IObservableList list = IncQueryObservables.observeMatchesAsList(factory, engine);
		list.addListChangeListener(new IListChangeListener() {
			
			@Override
			public void handleListChange(ListChangeEvent event) {
				for (ListDiffEntry lde : event.diff.getDifferences())
				{
					if (lde.isAddition()) {
						Object _o = lde.getElement();
						if (_o instanceof EPackageMatch) {
							results.append("\tNew EPackage found by changeset databinding: " + ((EPackageMatch)_o).getP().getName()+"\n");
						}
					}
					else {
						// left emptry
					}
				}	
			}
		});
		*/
	}
	
	
	
}
