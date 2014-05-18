/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.project;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.runtime.IncQueryRuntimePlugin;
import org.eclipse.incquery.tooling.core.generator.ExtensionData;
import org.eclipse.incquery.tooling.core.generator.IncQueryGeneratorPlugin;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.project.IBundleClasspathEntry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IPackageExportDescription;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A common helper class for generating IncQuery-related projects.
 *
 * @author Zoltan Ujhelyi
 */
@SuppressWarnings("restriction")
public abstract class ProjectGenerationHelper {

    private static final String INVALID_PROJECT_MESSAGE = "Invalid project %s. Only existing, open plug-in projects are supported by the generator.";

    private static final class IDToRequireBundleTransformer implements Function<String, IRequiredBundleDescription> {
        private final IBundleProjectService service;

        private IDToRequireBundleTransformer(IBundleProjectService service) {
            this.service = service;
        }

        @Override
        public IRequiredBundleDescription apply(String input) {
            return service.newRequiredBundle(input, null, false, false);
        }
    }

    /**
     * Two source folders: src to be manually written and src-gen to contain generated code
     */
    public static final List<String> SOURCEFOLDERS = ImmutableList
            .of(IncQueryNature.SRC_DIR, IncQueryNature.SRCGEN_DIR);
    /**
     * A single source folder named src
     */
    public static final List<String> SINGLESOURCEFOLDER = ImmutableList.of("src");

    /**
     * Creates a new IncQuery project: a plug-in project with src and src-gen folders and specific dependencies.
     *
     */
    public static void createProject(IProjectDescription description, IProject proj,
            List<String> additionalDependencies, IProgressMonitor monitor) throws CoreException {
        List<String> dependencies = Lists.newArrayList("org.eclipse.emf.ecore",
                "org.eclipse.emf.transaction", IncQueryRuntimePlugin.PLUGIN_ID, "org.eclipse.xtext.xbase.lib");
        if (additionalDependencies != null) {
            dependencies.addAll(additionalDependencies);
        }
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;

        try {

            monitor.beginTask("", 2000);
            /* Creating plug-in information */
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(proj);
            IPath[] additionalBinIncludes = new IPath[] { new Path("plugin.xml")};
            ProjectGenerationHelper.fillProjectMetadata(proj, dependencies, service, bundleDesc, additionalBinIncludes);
            bundleDesc.apply(monitor);
            // Adding IncQuery-specific natures
            ProjectGenerationHelper.updateNatures(proj,
                    ImmutableList.of(IncQueryNature.XTEXT_NATURE_ID, IncQueryNature.NATURE_ID),
                    ImmutableList.<String>of(), monitor);
        } finally {
            monitor.done();
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates the set of project natures of a selected project
     */
    public static void updateNatures(IProject proj, Collection<String> naturesToAdd,
            Collection<String> naturesToRemove, IProgressMonitor monitor) throws CoreException {
        IProjectDescription desc = proj.getDescription();
        Set<String> newNatures = new LinkedHashSet<String>();
        newNatures.addAll(Arrays.asList(desc.getNatureIds()));
        newNatures.addAll(naturesToAdd);
        newNatures.removeAll(naturesToRemove);
        desc.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
        proj.setDescription(desc, monitor);
    }

    /**
     * Adds a file to a container.
     *
     * @param container
     *            the container to add the file to
     * @param path
     *            the path of the newly created file
     * @param contentStream
     *            the file will be filled with this stream's contents
     * @param monitor
     * @throws CoreException
     */
    public static void addFileToProject(IContainer container, Path path, InputStream contentStream,
            IProgressMonitor monitor) throws CoreException {
        final IFile file = container.getFile(path);

        if (file.exists()) {
            file.setContents(contentStream, true, true, monitor);
        } else {
            file.create(contentStream, true, monitor);
        }

    }

    public static void initializePluginProject(IProject project, final List<String> dependencies,
            final IPath[] additionalBinIncludes) throws CoreException {
        initializePluginProject(project, dependencies, additionalBinIncludes, new NullProgressMonitor());
    }

    public static void initializePluginProject(IProject project, final List<String> dependencies,
            final IPath[] additionalBinIncludes, IProgressMonitor monitor) throws CoreException {
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            fillProjectMetadata(project, dependencies, service, bundleDesc, additionalBinIncludes);
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Initializes the plug-in metadata of a newly created project.
     *
     * @param project
     *            the plug-in project to create the metadata for. The plug-in id will be the same as the project name
     * @param dependencies
     *            a list of required bundles to add
     * @param service
     * @param bundleDesc
     */
    public static void fillProjectMetadata(IProject project, final List<String> dependencies,
            final IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final IPath[] additionalBinIncludes) {
        bundleDesc.setBundleName(project.getName());
        bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
        bundleDesc.setSingleton(true);
        bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
        bundleDesc.setSymbolicName(project.getName());
        bundleDesc.setExtensionRegistry(true);
        bundleDesc.setBinIncludes(additionalBinIncludes);

        bundleDesc.setBundleClasspath(getUpdatedBundleClasspathEntries(new IBundleClasspathEntry[0], service));
        bundleDesc.setExecutionEnvironments(new String[] { IncQueryNature.EXECUTION_ENVIRONMENT });
        // Adding dependencies
        IRequiredBundleDescription[] reqBundles = Lists.transform(dependencies,
                new IDToRequireBundleTransformer(service)).toArray(new IRequiredBundleDescription[dependencies.size()]);
        bundleDesc.setRequiredBundles(reqBundles);
    }

    /**
     * Checks whether the project depends on a selected bundle ID
     *
     * @param project
     *            an existing, open plug-in project to check
     * @param dependency
     *            bundle identifier
     * @return true, if the project depends on the given bundle
     * @throws CoreException
     */
    public static boolean checkBundleDependency(IProject project, String dependency) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
                String.format(INVALID_PROJECT_MESSAGE, project.getName()));
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            for (IRequiredBundleDescription require : bundleDesc.getRequiredBundles()) {
                if (dependency.equals(require.getName())) {
                    return true;
                }
            }
            return false;
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates project manifest to ensure the selected bundle dependencies are set. Does not change existing
     * dependencies.
     *
     * @param project
     * @param dependencies
     * @throws CoreException
     */
    public static void ensureBundleDependencies(IProject project, final List<String> dependencies) throws CoreException {
        ensureBundleDependencies(project, dependencies, new NullProgressMonitor());
    }

    /**
     * Updates project manifest to ensure the selected bundle dependencies are set. Does not change existing
     * dependencies.
     *
     * @param project
     *            an existing, open PDE plug-in project
     * @param dependencies
     * @param monitor
     * @throws CoreException
     */
    public static void ensureBundleDependencies(IProject project, final List<String> dependencies,
            IProgressMonitor monitor) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
        		String.format(INVALID_PROJECT_MESSAGE, project.getName()));
        if (dependencies.isEmpty()) {
            return;
        }
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            ensureBundleDependencies(service, bundleDesc, dependencies);
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates project manifest to ensure the selected bundle dependencies are set. Does not change existing
     * dependencies.
     *
     * @param service
     * @param bundleDesc
     * @param dependencies
     */
    static void ensureBundleDependencies(IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final List<String> dependencies) {
        IRequiredBundleDescription[] requiredBundles = bundleDesc.getRequiredBundles();
        if (requiredBundles == null) {
            return;
        }
        List<String> updatedDependencies = new ArrayList<String>(dependencies);

        // XXX for compatibility two different versions are needed
        final Version pdeVersion = Platform.getBundle("org.eclipse.pde.core").getVersion();
        if (pdeVersion.compareTo(new Version(3, 9, 0)) < 0) {
            // Before Kepler setRequiredBundles only adds dependencies, does not remove
            List<String> missingDependencies = new ArrayList<String>(dependencies);
            for (IRequiredBundleDescription bundle : requiredBundles) {
                if (missingDependencies.contains(bundle.getName())) {
                    missingDependencies.remove(bundle.getName());
                }
            }
            bundleDesc.setRequiredBundles(Lists.transform(missingDependencies, new IDToRequireBundleTransformer(service))
                    .toArray(new IRequiredBundleDescription[missingDependencies.size()]));
        } else {
            // Since Kepler setRequiredBundles overwrites existing dependencies
            for (IRequiredBundleDescription bundle : requiredBundles) {
                if (!updatedDependencies.contains(bundle.getName())) {
                    updatedDependencies.add(bundle.getName());
                }
            }
            bundleDesc.setRequiredBundles(Lists.transform(updatedDependencies, new IDToRequireBundleTransformer(service))
                    .toArray(new IRequiredBundleDescription[updatedDependencies.size()]));
        }
    }

    /**
     * Updates project manifest to ensure the selected packages are exported. Does not change existing exports.
     *
     * @param project
     * @param dependencies
     * @throws CoreException
     */
    public static void ensurePackageExports(IProject project, final Collection<String> dependencies)
            throws CoreException {
        ensurePackageExports(project, dependencies, new NullProgressMonitor());
    }

    /**
     * Updates project manifest to ensure the selected packages are exported. Does not change existing exports.
     *
     * @param project
     *            an existing, open PDE plug-in project
     * @param exports
     *            a non-empty list of package exports
     * @param monitor
     * @throws CoreException
     */
    public static void ensurePackageExports(IProject project, final Collection<String> exports, IProgressMonitor monitor)
            throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
        		String.format(INVALID_PROJECT_MESSAGE, project.getName()));
        if (exports.isEmpty()) {
            return;
        }

        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            ensurePackageExports(service, bundleDesc, exports);
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates project manifest to ensure the selected packages are removed. Does not change existing exports.
     *
     * @param project
     *            an existing, open plug-in project
     * @param dependencies
     * @param monitor
     * @throws CoreException
     */
    public static void removePackageExports(IProject project, final List<String> dependencies, IProgressMonitor monitor)
            throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
        		String.format(INVALID_PROJECT_MESSAGE, project.getName()));
        if (dependencies.isEmpty()) {
            return;
        }

        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            removePackageExports(service, bundleDesc, dependencies);
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates project manifest to ensure the selected packages are exported. Does not change existing exports.
     *
     * @param service
     * @param bundleDesc
     * @param exports
     */
    static void ensurePackageExports(final IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final Collection<String> exports) {
        IPackageExportDescription[] packageExports = bundleDesc.getPackageExports();
        List<String> missingExports = new ArrayList<String>(exports);
        List<IPackageExportDescription> exportList = new ArrayList<IPackageExportDescription>();
        if (packageExports != null) {
            for (IPackageExportDescription export : packageExports) {
                if (!missingExports.contains(export.getName())) {
                    missingExports.remove(export.getName());
                }
                exportList.add(export);
            }
        }
        exportList.addAll(Lists.transform(missingExports, new Function<String, IPackageExportDescription>() {

            @Override
            public IPackageExportDescription apply(String input) {
                return service.newPackageExport(input, null, true, null);
            }
        }));

        bundleDesc.setPackageExports(exportList.toArray(new IPackageExportDescription[exportList.size()]));
    }

    /**
     * Updates project manifest to ensure the selected packages are removed. Does not change existing exports.
     *
     * @param service
     * @param bundleDesc
     * @param exports
     */
    static void removePackageExports(final IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final List<String> exports) {
        IPackageExportDescription[] packageExports = bundleDesc.getPackageExports();
        List<IPackageExportDescription> exportList = new ArrayList<IPackageExportDescription>();
        if (packageExports != null) {
            for (IPackageExportDescription export : packageExports) {
                if (!exports.contains(export.getName())) {
                    exportList.add(export);
                }
            }
        }
        bundleDesc.setPackageExports(exportList.toArray(new IPackageExportDescription[exportList.size()]));
    }

    /**
     * Updates the selected project to contain the selected extension. The extensions are identified using an identifier
     * and extension point together; old extensions are replaced with the new ones, other extensions are kept intact.
     *
     * @param project
     * @param contributedExtensions
     * @throws CoreException
     */
    public static void ensureExtensions(IProject project, Iterable<ExtensionData> contributedExtensions,
            Iterable<Pair<String, String>> removedExtensions) throws CoreException {
        ensureExtensions(project, contributedExtensions, removedExtensions, new NullProgressMonitor());
    }

    /**
     * Updates the selected project to contain the selected extension. The extensions are identified using an identifier
     * and extension point together; old extensions are replaced with the new ones, other extensions are kept intact. An
     * extension will be ignored, if exist in the removedExtensions list.
     *
     * @param project
     *            an existing, open PDE plug-in project
     * @param contributedExtensions
     * @param removedExtensions
     * @param monitor
     * @throws CoreException
     */
    public static void ensureExtensions(IProject project, Iterable<ExtensionData> contributedExtensions,
            Iterable<Pair<String, String>> removedExtensions, IProgressMonitor monitor) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
        		String.format(INVALID_PROJECT_MESSAGE, project.getName()));

        if (StringExtensions.isNullOrEmpty(project.getName())) {
            return;
        }
        PluginXmlModifier modifier = new PluginXmlModifier();
        modifier.loadPluginXml(project);
        modifier.removeExtensions(removedExtensions);
        modifier.addExtensions(contributedExtensions);
        modifier.savePluginXml();
    }
    
    /**
     * Updates project manifest to ensure the selected packages are removed. Does not change existing exports.
     *
     * @param project
     * @param dependencies
     * @throws CoreException
     */
    public static void removePackageExports(IProject project, List<String> dependencies) throws CoreException {
        removePackageExports(project, dependencies, new NullProgressMonitor());
    }

    /**
     * Removes all extensions from the project, if the extension's pointId equals to one of the given pointId.
     *
     * @param project
     *            an existing, open PDE project
     * @param removableExtensionIdentifiers
     *            - contains both the extension id prefix (key), and the extension point id (value)
     * @throws CoreException
     */
    public static void removeAllExtension(IProject project,
            Collection<Pair<String, String>> removableExtensionIdentifiers) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)));

        if (StringExtensions.isNullOrEmpty(project.getName())) {
            return;
        }
        PluginXmlModifier modifier = new PluginXmlModifier();
        modifier.loadPluginXml(project);
        modifier.removeExtensions(removableExtensionIdentifiers);
        modifier.savePluginXml();
    }

    /**
     * Ensures that the project contains the src and src-gen folders as source folders.
     *
     * @param project
     *            an existing, open plug-in project
     * @param monitor
     * @throws CoreException
     */
    public static void ensureSourceFolders(IProject project, IProgressMonitor monitor) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
        		String.format(INVALID_PROJECT_MESSAGE, project.getName()));
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = IncQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            bundleDesc.setBundleClasspath(getUpdatedBundleClasspathEntries(bundleDesc.getBundleClasspath(), service));
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Returns an updated the classpath entries of a project by ensuring all required source folders are present.
     *
     * @param service
     * @return
     */
    private static IBundleClasspathEntry[] getUpdatedBundleClasspathEntries(final IBundleClasspathEntry[] oldClasspath,
            final IBundleProjectService service) {
        Collection<IBundleClasspathEntry> classPathSourceList = Collections2.filter(Lists.newArrayList(oldClasspath),
                new Predicate<IBundleClasspathEntry>() {
                    @Override
                    public boolean apply(IBundleClasspathEntry entry) {
                        return entry.getSourcePath() != null && !entry.getSourcePath().isEmpty();
                    }
                });
        final Collection<String> existingSourceEntries = Collections2.transform(classPathSourceList,
                new Function<IBundleClasspathEntry, String>() {
                    @Override
                    public String apply(IBundleClasspathEntry entry) {
                        return entry.getSourcePath().toString();
                    }
                });
        Collection<String> missingSourceFolders = Collections2.filter(SOURCEFOLDERS, new Predicate<String>() {
            @Override
            public boolean apply(String entry) {
                return !existingSourceEntries.contains(entry);
            }
        });
        Collection<IBundleClasspathEntry> newClasspathEntries = Collections2.transform(missingSourceFolders,
                new Function<String, IBundleClasspathEntry>() {
                    @Override
                    public IBundleClasspathEntry apply(String input) {
                        return service.newBundleClasspathEntry(new Path(input), null, null);
                    }
                });

        List<IBundleClasspathEntry> modifiedClasspathEntries = Lists.newArrayList(oldClasspath);
        modifiedClasspathEntries.addAll(newClasspathEntries);
        return modifiedClasspathEntries.toArray(new IBundleClasspathEntry[modifiedClasspathEntries.size()]);
    }

    public static String getBundleSymbolicName(IProject project) {
        IPluginModelBase plugin = PDECore.getDefault().getModelManager().findModel(project);
        return plugin.getBundleDescription().getSymbolicName();
    }

}
