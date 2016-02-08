/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.ui.builder;

import static org.eclipse.viatra.cep.vepl.ui.builder.PackageExportHelper.packageShouldBeExported;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.util.Pair;

import com.google.inject.Inject;

/**
 * 
 * @author Istvan David
 *
 */
public class VeplBuilderParticipant extends BuilderParticipant {

    @Inject
    private PackageExportProvider packageExportProvider;

    @Inject
    private IGenerator generator;

    @Inject
    private IStorage2UriMapper storage2UriMapper;

    @Override
    public void build(final IBuildContext context, IProgressMonitor monitor) throws CoreException {
        SubMonitor progress = SubMonitor.convert(monitor, 5);
        super.build(context, progress.newChild(4));

        // execute the actual package export
        packageExportProvider.export(context.getBuiltProject(), progress.newChild(1));
    }

    @Override
    protected void handleChangedContents(Delta delta, IBuildContext context,
            EclipseResourceFileSystemAccess2 fileSystemAccess) throws CoreException {
        Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
        if (shouldGenerate(deltaResource, context)) {
            try {
                generator.doGenerate(deltaResource, fileSystemAccess);

                // mark the new element's package as exportable
                final IProject project = context.getBuiltProject();
                TreeIterator<EObject> it = deltaResource.getAllContents();
                while (it.hasNext()) {
                    EObject obj = it.next();
                    if (obj instanceof EventModel) {
                        EventModel eventModel = (EventModel) obj;

                        // set up the potential CEP-related packages
                        packageExportProvider.setupCepPackageCollection(project, eventModel);

                        // set the base package
                        String basePackage = eventModel.getName();
                        packageExportProvider.addBasePackage(project, basePackage);

                        // if the model is empty, remove the CEP-related packages
                        if (eventModel.getModelElements().isEmpty()) {
                            packageExportProvider.removeCepPackages(project, new NullProgressMonitor());
                        }
                    } else if (obj instanceof ModelElement) {
                        ModelElement modelElement = (ModelElement) obj;

                        // not every package will exported
                        if (!packageShouldBeExported(modelElement)) {
                            continue;
                        }

                        // add element to the exportables
                        packageExportProvider.addExportablePackage(project, modelElement);
                    }
                }
            } catch (RuntimeException e) {
                if (e.getCause() instanceof CoreException) {
                    throw (CoreException) e.getCause();
                }
                throw e;
            }
        }
    }

    @Override
    protected boolean shouldGenerate(Resource resource, IBuildContext context) {
        try {
            Iterable<Pair<IStorage, IProject>> storages = storage2UriMapper.getStorages(resource.getURI());
            for (Pair<IStorage, IProject> pair : storages) {
                if (pair.getFirst() instanceof IFile && pair.getSecond().equals(context.getBuiltProject())) {
                    IFile file = (IFile) pair.getFirst();
                    return file.findMaxProblemSeverity("org.eclipse.xtext.ui.check", true, IResource.DEPTH_INFINITE) != IMarker.SEVERITY_ERROR;
                }
            }
            return false;
        } catch (CoreException exc) {
            throw new WrappedException(exc);
        }
    }
}
