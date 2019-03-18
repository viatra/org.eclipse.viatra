/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.testing.ui.handlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.dialogs.WorkspaceResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
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
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent;

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
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        if (selection instanceof TreeSelection) {
            saveSnapshot((TreeSelection) selection, event);
        }
        return null;
    }

    
    private void saveSnapshot(TreeSelection selection, ExecutionEvent event) throws ExecutionException {
        Set<IFilteredMatcherContent<?>> matchers = Arrays.stream(selection.toArray()).
            filter(obj -> obj instanceof IFilteredMatcherContent).
            map(IFilteredMatcherContent.class::cast).
            collect(Collectors.toSet());

        ViatraQueryEngine engine = matchers.stream().findFirst().
                map(IFilteredMatcherContent::getMatcher).
                map(ViatraQueryMatcher::getEngine).
                orElseThrow(() -> new ExecutionException("Cannot save snapshot without ViatraQueryEngine!"));
        
        ResourceSet resourceSet = getResourceSetForScope(engine.getScope());
        if(resourceSet == null) {
            throw new ExecutionException("Cannot save snapshot, models not in ResourceSet!");
        }
        final QuerySnapshot snapshot = getSnapshotFile(HandlerUtil.getActiveShell(event), engine, resourceSet); 
        matchers.stream().
                filter(m -> m.getMatcher() != null).
                forEach(m -> saveMatchesToSnapshot(m, snapshot));
        
        try {
            snapshot.eResource().save(null);
        } catch(IOException e) {
            throw new ExecutionException("Error during saving snapshot into file!",e);
        }
    }


    private QuerySnapshot getSnapshotFile(Shell shell, ViatraQueryEngine engine, ResourceSet resourceSet)
            throws ExecutionException {
        IFile snapshotFile = null;
        IFile[] files = WorkspaceResourceDialog.openFileSelection(shell, "Existing snapshot", "Select existing Query snapshot file (Cancel for new file)", false, null, null);
        QuerySnapshot snapshot = null;
            
        if(files.length == 0) {
            snapshotFile = WorkspaceResourceDialog.openNewFile(shell, "New snapshot", "Select Query snapshot target file (.snapshot extension)", null, null);
            if(snapshotFile != null && !snapshotFile.exists()) {
                snapshot = SnapshotFactory.eINSTANCE.createQuerySnapshot();
                Resource res = resourceSet.createResource(URI.createPlatformResourceURI(snapshotFile.getFullPath().toString(),true));
                res.getContents().add(snapshot);
            } else {
                throw new ExecutionException("Selected file name must use .snapshot extension!");
            }
        } else {
            snapshotFile = files[0];
            if(snapshotFile != null && Objects.equals(snapshotFile.getFileExtension(), "snapshot")) {
            
                snapshot = loader.loadExpectedResultsFromFile(resourceSet, snapshotFile);
                
                if(snapshot != null) {
                    validateInputSpecification(engine, snapshot);
                } else {
                    throw new ExecutionException("Selected file does not contain snapshot!");
                }
            } else {
                throw new ExecutionException("Selected file not .snapshot!");
            }
        }
        return snapshot;
    }
    
    private <MATCH extends IPatternMatch> void saveMatchesToSnapshot(IFilteredMatcherContent<MATCH> matcher, QuerySnapshot snapshot) {
        helper.saveMatchesToSnapshot(matcher.getMatcher(), matcher.getFilterMatch(), snapshot);
    }

    private void validateInputSpecification(ViatraQueryEngine engine, QuerySnapshot snapshot) throws ExecutionException {
        if(snapshot.getInputSpecification() != null) {
            Notifier root = helper.getEMFRootForSnapshot(snapshot);
            Notifier matcherRoot = getScopeRoot(engine.getScope());
            if(matcherRoot != root) {
                throw new ExecutionException("Existing snapshot model root (" + root + ") not equal to selected input (" + matcherRoot + ")!");
            }
        }
    }
    
    private ResourceSet getResourceSetForScope(QueryScope scope) throws ExecutionException {
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
            throw new ExecutionException("Non-EMF scopes are not supported!");
        }
    }
    
    private Notifier getScopeRoot(QueryScope scope) throws ExecutionException {
        if (scope instanceof EMFScope) {
            EMFScope emfScope = (EMFScope) scope;
            Set<? extends Notifier> scopeRoots = emfScope.getScopeRoots();
            if (scopeRoots.size() > 1) {
                throw new ExecutionException("EMF scopes with multiple ResourceSets are not supported!");
            } else {
                return scopeRoots.iterator().next();
            }
        } else {
            throw new ExecutionException("Non-EMF scopes are not supported!");
        }
    }
}
