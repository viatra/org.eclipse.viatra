/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.ui.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.runtime.IExtensions;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.tooling.core.generator.GenerateQuerySpecificationExtension;
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragment;
import org.eclipse.viatra.query.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.builder.IXtextBuilderParticipant.IBuildContext;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Clean phase support for BuilderParticipant.
 *
 * @author Mark Czotter
 *
 */
public class CleanSupport {

    @Inject
    private Injector injector;

    @Inject
    private IGenerationFragmentProvider fragmentProvider;

    @Inject
    private EclipseResourceSupport eclipseResourceSupport;

    @Inject
    private EnsurePluginSupport ensureSupport;

    @Inject
    private Logger logger;
    
    @Inject
    OldVersionHelper oldVersion;
    
    @Inject
    EMFPatternLanguageJvmModelInferrerUtil inferrerUtil; 


    /**
     * Performs a full clean on the currently built project and all related fragments.
     *
     * @param context
     * @param monitor
     */
    public void fullClean(IBuildContext context, IProgressMonitor monitor) {
        try {
            internalFullClean(context, monitor);
        } catch (Exception e) {
            logger.error("Exception during Full Clean!", e);
        } finally {
            monitor.worked(1);
        }
    }

    private void internalFullClean(IBuildContext context, IProgressMonitor monitor) throws CoreException,
            ViatraQueryException {
        IProject modelProject = context.getBuiltProject();
        // clean all fragments
        cleanAllFragment(modelProject);
        // clean current model project
        List<Pair<String, String>> removableExtensions = new ArrayList<Pair<String, String>>();
        removableExtensions.addAll(GenerateQuerySpecificationExtension.getRemovableExtensionIdentifiers());
        ProjectGenerationHelper.removeAllExtension(modelProject, removableExtensions);
    }

    /**
     * Performs full Clean on every registered {@link IGenerationFragment}.
     *
     * @param modelProject
     * @throws CoreException
     */
    private void cleanAllFragment(IProject modelProject) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getAllFragments()) {
            try {
                cleanFragment(modelProject, fragment);
            } catch (Exception e) {
                logger.error("Exception during full Clean on " + fragment.getClass().getCanonicalName(), e);
            }
        }
    }

    private void cleanFragment(IProject modelProject, IGenerationFragment fragment) throws CoreException {
        IProject fragmentProject = fragmentProvider.getFragmentProject(modelProject, fragment);
        if (fragmentProject.exists() && !fragmentProject.equals(modelProject)) {
            fragmentProject.refreshLocal(IResource.DEPTH_INFINITE, null);
            // full clean on output directories
            EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport
                    .createProjectFileSystemAccess(fragmentProject);
            for (OutputConfiguration config : fsa.getOutputConfigurations().values()) {
                cleanFragmentFolder(fragmentProject, config);
            }
            if (PDE.hasPluginNature(fragmentProject)) {
                // clean all removable extensions
                ProjectGenerationHelper.removeAllExtension(fragmentProject, fragment.getRemovableExtensions());
            }
            // removing all fragment-related markers
            fragmentProject.deleteMarkers(IErrorFeedback.FRAGMENT_ERROR_TYPE, true, IResource.DEPTH_INFINITE);
        }
    }

    private void cleanFragmentFolder(IProject fragmentProject, OutputConfiguration config) throws CoreException {
        IFolder folder = fragmentProject.getFolder(config.getOutputDirectory());
        if (folder.exists()) {
            for (IResource resource : folder.members()) {
                resource.delete(IResource.KEEP_HISTORY, new NullProgressMonitor());
            }
        }
    }

    /**
     * Performs a normal Clean on the currently built project and all related fragments.
     *
     * @param context
     * @param relevantDeltas
     * @param monitor
     */
    public void normalClean(IBuildContext context, List<Delta> relevantDeltas, IProgressMonitor monitor) {
        try {
            internalNormalClean(context, relevantDeltas, monitor);
        } catch (Exception e) {
            logger.error("Exception during Normal Clean!", e);
        } finally {
            monitor.worked(1);
        }
    }

    private void internalNormalClean(IBuildContext context, List<Delta> relevantDeltas, IProgressMonitor monitor)
            throws CoreException, ViatraQueryException {
        for (Delta delta : relevantDeltas) {
            // Determine if this resource is logically nested in the project being built.
            // Not currently built projects should be left alone, see bugs 452176 and 496257
            URI uri = delta.getUri();
            if (uri.isPlatformResource() && context.getBuiltProject().getName().equals(uri.segment(1))) {
                if (delta.getOld() != null) {
                    Iterable<IEObjectDescription> oldExportedPatterns = delta.getOld().getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN);
                    for (IEObjectDescription desc : oldExportedPatterns) {
                        Pattern pattern = (Pattern) desc.getEObjectOrProxy();
                        if (pattern.eIsProxy()) {
                            pattern = oldVersion.findOldVersion(((InternalEObject)pattern).eProxyURI());
                        }
                        final String fqn = desc.getQualifiedName().toString();
                        if (pattern == null || pattern.eIsProxy()) {
                            // Old version cannot be found, executing full clean
                            context.getBuiltProject().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
                            return;
                        }
                        final String foundFQN = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                        if (!foundFQN.equals(fqn)){
                        	// Incorrect old version found, executing full clean
                            context.getBuiltProject().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
                            return;
                        }
                        // Only clean up extensions in the modelProject if file is removed, otherwise it will be overwritten later
                        if (delta.getNew() == null) {
                            executeCleanUpOnModelProject(context.getBuiltProject(), pattern);
                        }
                        // clean up code and extensions for all fragments
                        executeCleanUpOnFragments(context.getBuiltProject(), pattern);
                    }
                }
            }
        }
    }

    /**
     * Executes Normal Build cleanUp on the current Built Project (modelProject). Removes all code generated previously
     * for the {@link Pattern}, and marks current {@link Pattern} related extensions for removal.
     *
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeCleanUpOnModelProject(IProject modelProject, Pattern pattern) throws CoreException {
        if (pattern != null) {
            String extensionId = inferrerUtil.modelFileQualifiedName(pattern);
            ensureSupport
                .removeExtension(modelProject, Pair.of(extensionId, IExtensions.QUERY_SPECIFICATION_EXTENSION_POINT_ID));
        }
    }

    /**
     * Executes Normal Build cleanUp on every {@link IGenerationFragment} registered to the current {@link Pattern}.
     * Marks current {@link Pattern} related extensions for removal. If the {@link IProject} related to
     * {@link IGenerationFragment} does not exist, clean up skipped for the fragment.
     *
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeCleanUpOnFragments(IProject modelProject, Pattern pattern) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getAllFragments()) {
            try {
                injector.injectMembers(fragment);
                // clean if the project still exist
                IProject targetProject = fragmentProvider.getFragmentProject(modelProject, fragment);
                if (targetProject.exists()) {
                    EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport
                            .createProjectFileSystemAccess(targetProject);
                    fragment.cleanUp(pattern, fsa);
                    ensureSupport.removeAllExtension(targetProject, fragment.removeExtension(pattern));
                    // removing all fragment-related markers
                    targetProject.deleteMarkers(IErrorFeedback.FRAGMENT_ERROR_TYPE, true, IResource.DEPTH_INFINITE);
                }
            } catch (Exception e) {
                String msg = String.format("Exception when executing clean for '%s' in fragment '%s'",
                        CorePatternLanguageHelper.getFullyQualifiedName(pattern), fragment.getClass()
                                .getCanonicalName());
                logger.error(msg, e);
            }
        }
    }

}
