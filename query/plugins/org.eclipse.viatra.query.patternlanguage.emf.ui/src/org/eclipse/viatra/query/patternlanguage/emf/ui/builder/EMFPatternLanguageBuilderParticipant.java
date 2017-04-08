/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *   Andras Okros - minor changes
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.builder;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidator;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternValidationStatus;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.tooling.core.generator.ExtensionData;
import org.eclipse.viatra.query.tooling.core.generator.GenerateQuerySpecificationExtension;
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragment;
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class EMFPatternLanguageBuilderParticipant extends BuilderParticipant {

    @Inject
    private Injector injector;

    @Inject
    private IGenerator generator;

    @Inject
    private IGenerationFragmentProvider fragmentProvider;

    @Inject
    private EMFPatternLanguageJvmModelInferrerUtil util;

    @Inject
    private EnsurePluginSupport ensureSupport;

    @Inject
    private CleanSupport cleanSupport;

    @Inject
    private EclipseResourceSupport eclipseResourceSupport;

    @Inject
    private GenerateQuerySpecificationExtension querySpecificationExtensionGenerator;

    @Inject
    private IVQGenmodelProvider genmodelProvider;

    @Inject
    private Logger logger;

    @Inject
    private PatternSetValidator validator;

    @Override
    public void build(final IBuildContext context, IProgressMonitor monitor) throws CoreException {
        if (!isEnabled(context)) {
            return;
        }
        final List<IResourceDescription.Delta> relevantDeltas = getRelevantDeltas(context);
        if (relevantDeltas.isEmpty()) {
            return;
        }
        // monitor handling
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        SubMonitor progress = SubMonitor.convert(monitor, 5);
        final IProject modelProject = context.getBuiltProject();
        if (context.getBuildType() == BuildType.CLEAN || context.getBuildType() == BuildType.RECOVERY) {
            cleanSupport.fullClean(context, progress.newChild(1));
            // invoke clean build on main project src-gen
            super.build(context, progress.newChild(1));
            if (context.getBuildType() == BuildType.CLEAN) {
                return;
            }
        } else {
            ensureSupport.clean();
            cleanSupport.normalClean(context, relevantDeltas, progress.newChild(1));
        }
        super.build(context, progress.newChild(1));
        // normal code generation done, extensions, packages ready to add to the
        // plug-ins
        ensureSupport.ensure(modelProject, progress.newChild(1));
    }

    @Override
    protected void handleChangedContents(Delta delta, IBuildContext context,
            EclipseResourceFileSystemAccess2 fileSystemAccess) throws CoreException {
        // Determine if this resource is logically nested in the project being built.
        // Hopefully helps with performance, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=461302
        URI uri = delta.getUri();
        IProject builtProject = context.getBuiltProject();
        if (uri.isPlatformResource() && builtProject.getName().equals(uri.segment(1))) {
            // TODO: we will run out of memory here if the number of deltas is large enough
            Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
            if (shouldGenerate(deltaResource, context)) {
                try {
                	registerCurrentSourceFolder(context, delta, fileSystemAccess);
                    // do inferred jvm model to code transformation
                    generator.doGenerate(deltaResource, fileSystemAccess);
                    doPostGenerate(deltaResource, context);
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof CoreException) {
                        throw (CoreException) e.getCause();
                    }
                    throw e;
                }
            }
        }
    }

    /**
     * From all {@link Pattern} instance in the current deltaResource, computes various additions to the modelProject,
     * and executes the provided fragments. Various contribution: package export, QuerySpecification extension, validation
     * constraint stuff.
     *
     * @param deltaResource
     * @param context
     * @throws CoreException
     */
    private void doPostGenerate(Resource deltaResource, IBuildContext context) throws CoreException {
        PatternSetValidationDiagnostics validate = validator.validate(deltaResource);
        if (validate.getStatus() == PatternValidationStatus.ERROR) {
            // If there are errors in the resource, do not execute post-build steps
            return;
        }
        final IProject project = context.getBuiltProject();
        calculateEMFModelProjects(deltaResource, project);
        TreeIterator<EObject> it = deltaResource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof Pattern) {
                Pattern pattern = (Pattern) obj;
                boolean isPublic = !CorePatternLanguageHelper.isPrivate(pattern);
                if (isPublic) {
                    executeGeneratorFragments(context.getBuiltProject(), pattern);
                    ensureSupport.exportPackage(project, util.getPackageName(pattern));
                    ensureSupport.exportPackage(project, util.getUtilPackageName(pattern));
                }
            } else if (obj instanceof PatternModel) {
                PatternModel model = (PatternModel) obj;
                Iterable<ExtensionData> querySpecificationExtensionContribution = querySpecificationExtensionGenerator
                        .extensionContribution(model);
                ensureSupport.appendAllExtension(project, querySpecificationExtensionContribution);
                
            }
        }
    }

    private void calculateEMFModelProjects(Resource deltaResource, IProject project) {
        TreeIterator<EObject> it = deltaResource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof PatternModel) {
                PatternModel patternModel = (PatternModel) obj;
                for (PackageImport packageImport : EMFPatternLanguageHelper.getPackageImportsIterable(patternModel)) {
                    GenPackage genPackage = genmodelProvider.findGenPackage(packageImport, packageImport.getEPackage());
                    if (genPackage != null) {
                        String modelPluginID = genPackage.getGenModel().getModelPluginID();
                        if (modelPluginID != null && !modelPluginID.isEmpty()) {
                            ensureSupport.addModelBundleId(project, modelPluginID);
                        }
                    }
                }
                it.prune();
            }
        }
    }

    /**
     * Executes all {@link IGenerationFragment} provided for the current {@link Pattern}.
     *
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeGeneratorFragments(IProject modelProject, Pattern pattern) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getFragmentsForPattern(pattern)) {
            try {
                injector.injectMembers(fragment);
                executeGeneratorFragment(fragment, modelProject, pattern);
            } catch (Exception e) {
                String msg = String.format("Exception when executing generation for '%s' in fragment '%s'",
                        CorePatternLanguageHelper.getFullyQualifiedName(pattern), fragment.getClass()
                                .getCanonicalName());
                logger.error(msg, e);
            }
        }
    }

    private void executeGeneratorFragment(IGenerationFragment fragment, IProject modelProject, Pattern pattern)
            throws CoreException {
        IProject targetProject = createOrGetTargetProject(modelProject, fragment);
        EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport.createProjectFileSystemAccess(targetProject);
        fragment.generateFiles(pattern, fsa);
        // Generating Eclipse extensions
        Iterable<ExtensionData> extensionContribution = fragment.extensionContribution(pattern);
        // Gathering all registered extensions together to avoid unnecessary
        // plugin.xml modifications
        // Both for performance and for avoiding race conditions
        ensureSupport.appendAllExtension(targetProject, extensionContribution);
    }

    /**
     * Creates or finds {@link IProject} associated with the {@link IGenerationFragment}. If the project exist
     * dependencies ensured based on the {@link IGenerationFragment} contribution. If the project not exist, it will be
     * initialized.
     *
     * @param modelProject
     * @param fragment
     * @return
     * @throws CoreException
     */
    private IProject createOrGetTargetProject(IProject modelProject, IGenerationFragment fragment) throws CoreException {
        String postfix = fragment.getProjectPostfix();
        String modelProjectName = ProjectGenerationHelper.getBundleSymbolicName(modelProject);
        if (postfix == null || postfix.isEmpty()) {
            ProjectGenerationHelper.ensureBundleDependencies(modelProject,
                    Lists.newArrayList(fragment.getProjectDependencies()));
            return modelProject;
        } else {
            List<String> dependencies = Lists.newArrayList();
            dependencies.add(modelProjectName);
            dependencies.addAll(ensureSupport.getModelBundleDependencies(modelProject));
            dependencies.addAll(Lists.newArrayList(fragment.getProjectDependencies()));
            IProject targetProject = fragmentProvider.getFragmentProject(modelProject, fragment);
            if (!targetProject.exists()) {
                ProjectGenerationHelper.initializePluginProject(targetProject, dependencies,
                        fragment.getAdditionalBinIncludes());
            } else {
                if (!targetProject.isOpen()) {
                    targetProject.open(new NullProgressMonitor());
                }
                ProjectGenerationHelper.ensureBundleDependencies(targetProject, dependencies);
            }
            return targetProject;
        }
    }

}
