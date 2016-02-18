/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal.operations;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.GeneratorModelFactory;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.GeneratorModelReference;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.ViatraQueryGeneratorModel;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.StringInputStream;

public class CreateGenmodelOperation extends WorkspaceModifyOperation {
    private final IProject project;
    private final Collection<GenModel> genmodels;
    private final IVQGenmodelProvider genmodelProvider;
    private final IResourceSetProvider resourceSetProvider;

    public CreateGenmodelOperation(IProject project, Collection<GenModel> genmodels, IVQGenmodelProvider genmodelProvider,
            IResourceSetProvider resourceSetProvider) {
        this.project = project;
        this.genmodels = genmodels;
        this.genmodelProvider = genmodelProvider;
        this.resourceSetProvider = resourceSetProvider;
    }

    @Override
    protected void execute(IProgressMonitor monitor) throws CoreException {
        try {
            ViatraQueryGeneratorModel generatorModel = genmodelProvider.getGeneratorModel(project,
                    resourceSetProvider.get(project));
            EList<GeneratorModelReference> genmodelRefs = generatorModel.getGenmodels();
            for (GenModel ecoreGenmodel : genmodels) {
                GeneratorModelReference ref = GeneratorModelFactory.eINSTANCE.createGeneratorModelReference();
                ref.setGenmodel(ecoreGenmodel);
                genmodelRefs.add(ref);
            }
            if (genmodelRefs.isEmpty()) {
                IFile file = project.getFile(ViatraQueryNature.VQGENMODEL);
                file.create(new StringInputStream(""), false, new SubProgressMonitor(monitor, 1));
            } else {
                genmodelProvider.saveGeneratorModel(project, generatorModel);
            }
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, ViatraQueryGUIPlugin.PLUGIN_ID,
                    "Cannot create generator model: " + e.getMessage(), e));
        }
    }
}