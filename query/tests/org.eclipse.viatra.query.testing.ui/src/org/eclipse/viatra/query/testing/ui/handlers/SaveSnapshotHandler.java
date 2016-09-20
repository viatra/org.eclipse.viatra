/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.ui.handlers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.testing.core.ModelLoadHelper;
import org.eclipse.viatra.query.testing.core.SnapshotHelper;
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot;
import org.eclipse.viatra.query.testing.snapshot.SnapshotFactory;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherContent;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContent;
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author Abel Hegedus
 *
 */
public class SaveSnapshotHandler extends AbstractHandler {

	@Inject
	SnapshotHelper helper;
	@Inject
	ModelLoadHelper loader;
	@Inject
	private Logger logger;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			saveSnapshot((TreeSelection) selection, event);
		}
		return null;
	}

	
	private void saveSnapshot(TreeSelection selection, ExecutionEvent event) {
		Object obj = selection.getFirstElement();
		
		IEditorPart editor = null;
		Set<IFilteredMatcherContent> matchers = Sets.newHashSet();
		ViatraQueryEngine engine = null;
		if(obj instanceof PatternMatcherContent) {
		    PatternMatcherContent observablePatternMatcher = (PatternMatcherContent) obj;
			editor = observablePatternMatcher.getParent().getEditorPart();
			matchers.add(observablePatternMatcher);
			ViatraQueryMatcher<?> matcher = observablePatternMatcher.getMatcher();
			if(matcher != null) {
			    engine = matcher.getEngine();
			}
			
		} else if(obj instanceof PatternMatcherRootContent) {
		    PatternMatcherRootContent matcherRoot = (PatternMatcherRootContent) obj;
			editor = matcherRoot.getEditorPart();
			Iterator<PatternMatcherContent> iterator = matcherRoot.getChildrenIterator();
			
			while (iterator.hasNext()) {
			    PatternMatcherContent patternMatcherContent = iterator.next();
			    matchers.add(patternMatcherContent);
			    
                ViatraQueryMatcher<?> matcher = patternMatcherContent.getMatcher();
                if(matcher != null && matcher.getEngine() != null) {
                    engine = matcher.getEngine();
                }
			}
		} else {
		    Iterator<IFilteredMatcherContent> filteredMatchers = Iterators.filter(selection.iterator(), IFilteredMatcherContent.class);
		    while (filteredMatchers.hasNext()) {
                IFilteredMatcherContent selectedElement = filteredMatchers.next();
                matchers.add(selectedElement);
                engine = selectedElement.getMatcher().getEngine();
            }
		}
		if(engine == null) {
			logger.error("Cannot save snapshot without ViatraQueryEngine!");
			return;
		}
		ResourceSet resourceSet = getResourceSetForScope(engine.getScope());
		if(resourceSet == null) {
			logger.error("Cannot save snapshot, models not in ResourceSet!");
			return;
		}
		IFile snapshotFile = null;
		IFile[] files = WorkspaceResourceDialog.openFileSelection(HandlerUtil.getActiveShell(event), "Existing snapshot", "Select existing Query snapshot file (Cancel for new file)", false, null, null);
		QuerySnapshot snapshot = null;
			
		if(files.length == 0) {
			snapshotFile = WorkspaceResourceDialog.openNewFile(HandlerUtil.getActiveShell(event), "New snapshot", "Select Query snapshot target file (.snapshot extension)", null, null);
			if(snapshotFile != null && !snapshotFile.exists()) {
				snapshot = SnapshotFactory.eINSTANCE.createQuerySnapshot();
				Resource res = resourceSet.createResource(URI.createPlatformResourceURI(snapshotFile.getFullPath().toString(),true));
				res.getContents().add(snapshot);
			} else {
				logger.error("Selected file name must use .snapshot extension!");
				return;
			}
		} else {
			snapshotFile = files[0];
			if(snapshotFile != null && snapshotFile.getFileExtension().equals("snapshot")) {
			
				snapshot = loader.loadExpectedResultsFromFile(resourceSet,snapshotFile);
				
				if(snapshot != null) {
					if(!validateInputSpecification(engine, snapshot)) {
						return;
					}
				} else {
					logger.error("Selected file does not contain snapshot!");
					return;
				}
			} else {
				logger.error("Selected file not .snapshot!");
				return;
			}
		} 
		for (IFilteredMatcherContent matcher : matchers) {
			if(matcher.getMatcher() != null) {
				IPatternMatch filter = matcher.getFilterMatch();
			    helper.saveMatchesToSnapshot(matcher.getMatcher(), filter, snapshot);
			}
		}
		
		if(editor != null) {
			editor.doSave(new NullProgressMonitor());
		} else {
			try {
				snapshot.eResource().save(null);
			} catch(IOException e) {
				logger.error("Error during saving snapshot into file!",e);
			}
		}
	}


	private boolean validateInputSpecification(ViatraQueryEngine engine, QuerySnapshot snapshot) {
		if(snapshot.getInputSpecification() != null) {
			Notifier root = helper.getEMFRootForSnapshot(snapshot);
            Notifier matcherRoot = getScopeRoot(engine.getScope());
			if(matcherRoot != root) {
				logger.error("Existing snapshot model root (" + root + ") not equal to selected input (" + matcherRoot + ")!");
				return false;
			}
			return true;
		}
		return true;
	}
	
	private ResourceSet getResourceSetForScope(QueryScope scope) {
	    if (scope instanceof EMFScope) {
            EMFScope emfScope = (EMFScope) scope;
            Set<? extends Notifier> scopeRoots = emfScope.getScopeRoots();
            if (scopeRoots.size() > 1) {
                throw new IllegalArgumentException("EMF scopes with multiple ResourceSets are not supported!");
            } else {
                Notifier notifier = scopeRoots.iterator().next();
                if(notifier instanceof EObject) {
                    Resource resource = ((EObject) notifier).eResource();
                    if(resource != null) {
                        return resource.getResourceSet();
                    } else {
                        return null;
                    }
                } else if(notifier instanceof Resource) {
                    return ((Resource) notifier).getResourceSet();
                } else if(notifier instanceof ResourceSet) {
                    return (ResourceSet) notifier;
                } else {
                    return null;
                }
            }
        } else {
            throw new IllegalArgumentException("Non-EMF scopes are not supported!");
        }
	}
	
	private Notifier getScopeRoot(QueryScope scope) {
        if (scope instanceof EMFScope) {
            EMFScope emfScope = (EMFScope) scope;
            Set<? extends Notifier> scopeRoots = emfScope.getScopeRoots();
            if (scopeRoots.size() > 1) {
                throw new IllegalArgumentException("EMF scopes with multiple ResourceSets are not supported!");
            } else {
                return scopeRoots.iterator().next();
            }
        } else {
            throw new IllegalArgumentException("Non-EMF scopes are not supported!");
        }
    }
}
