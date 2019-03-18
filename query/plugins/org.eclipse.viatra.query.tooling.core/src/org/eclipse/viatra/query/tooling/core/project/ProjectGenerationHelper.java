/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.core.project;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.project.IBundleClasspathEntry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.core.project.IPackageExportDescription;
import org.eclipse.pde.core.project.IPackageImportDescription;
import org.eclipse.pde.core.project.IRequiredBundleDescription;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.viatra.query.runtime.ViatraQueryRuntimePlugin;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.tooling.core.generator.ExtensionData;
import org.eclipse.viatra.query.tooling.core.generator.ViatraQueryGeneratorPlugin;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A common helper class for generating VIATRA Query-related projects.
 *
 * @author Zoltan Ujhelyi
 */
@SuppressWarnings("restriction")
public abstract class ProjectGenerationHelper {

    private static final String INVALID_PROJECT_MESSAGE = "Invalid project %s. Only existing, open plug-in projects are supported by the generator.";
    private static final String UTF8_ENCODING = "UTF-8";

    /**
     * Contains the default bundle requirements for VIATRA in a format that can be loaded into {@link #ensureBundleDependencies(IProject, List)}.
     * @since 2.0
     */
    public static final List<String> DEFAULT_VIATRA_BUNDLE_REQUIREMENTS = Arrays.asList("org.eclipse.emf.ecore", ViatraQueryRuntimePlugin.PLUGIN_ID,
            "org.eclipse.viatra.query.runtime.rete", "org.eclipse.viatra.query.runtime.localsearch", "org.eclipse.xtext.xbase.lib");
    /**
     * Contains the default import package requirements for VIATRA in a format that can be loaded into {@link #ensurePackageImports(IProject, List)}.
     * @since 2.0
     */
    public static final List<String> DEFAULT_VIATRA_IMPORT_PACKAGES = Arrays.asList("org.apache.log4j");
    
    private ProjectGenerationHelper() {/*Utility class constructor*/}
    
    private static final class IDToPackageImportTransformer implements Function<String, IPackageImportDescription> {
        private final IBundleProjectService service;

        private IDToPackageImportTransformer(IBundleProjectService service) {
            this.service = service;
        }

        @Override
        public IPackageImportDescription apply(String arg0) {
            return service.newPackageImport(arg0, null, false);
        }
    }

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
     * Return true if the given project exists, is open and has PDE plug-in nature configured
     * @param project
     */
    public static boolean isOpenPDEProject(IProject project){
        return project.exists() && project.isOpen() && (PDE.hasPluginNature(project));
    }
    
    /**
     * Checks whether the given project exists, is open and has PDE plug-in nature configured. Throws
     * an {@link IllegalArgumentException} otherwise.
     * @param project
     */
    public static void checkOpenPDEProject(IProject project){
        Preconditions.checkArgument(isOpenPDEProject(project),
                INVALID_PROJECT_MESSAGE, project.getName());
    }
 
    /**
     * A single source folder named src
     */
    public static final List<String> SINGLESOURCEFOLDER = ImmutableList.of("src");

    /**
     * Creates a new VIATRA Query project: a plug-in project with src source folder and specific dependencies.
     *
     */
    public static void createProject(IProjectDescription description, IProject proj,
            List<String> additionalDependencies, IProgressMonitor monitor) throws CoreException {
        List<String> dependencies = new ArrayList<String>(DEFAULT_VIATRA_BUNDLE_REQUIREMENTS);
        if (additionalDependencies != null) {
            dependencies.addAll(additionalDependencies);
        }
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;

        try {

            monitor.beginTask("", 2000);
            /* Creating plug-in information */
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(proj);
            IPath[] additionalBinIncludes = new IPath[] { new Path("plugin.xml")};
            ProjectGenerationHelper.fillProjectMetadata(proj, dependencies, DEFAULT_VIATRA_IMPORT_PACKAGES, service, bundleDesc, additionalBinIncludes);
            bundleDesc.apply(monitor);
            //Ensure UTF-8 encoding
            proj.setDefaultCharset(UTF8_ENCODING, monitor);
            // Adding VIATRA Query-specific natures
            ProjectGenerationHelper.updateNatures(proj,
                    ImmutableList.of(ViatraQueryNature.XTEXT_NATURE_ID, ViatraQueryNature.NATURE_ID),
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
        Set<String> newNatures = new LinkedHashSet<>();
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
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            fillProjectMetadata(project, dependencies, Collections.<String>emptyList(), service, bundleDesc, additionalBinIncludes);
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
    public static void fillProjectMetadata(IProject project, final List<String> dependencies, final List<String> packageImports,
            final IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final IPath[] additionalBinIncludes) {
        bundleDesc.setBundleName(project.getName());
        bundleDesc.setBundleVersion(new Version(0, 0, 1, "qualifier"));
        bundleDesc.setSingleton(true);
        bundleDesc.setTargetVersion(IBundleProjectDescription.VERSION_3_6);
        bundleDesc.setSymbolicName(project.getName());
        bundleDesc.setExtensionRegistry(true);
        bundleDesc.setBinIncludes(additionalBinIncludes);

        bundleDesc.setBundleClasspath(getUpdatedBundleClasspathEntries(new IBundleClasspathEntry[0], SINGLESOURCEFOLDER, service));
        bundleDesc.setExecutionEnvironments(new String[] { ViatraQueryNature.EXECUTION_ENVIRONMENT });
        // Adding dependencies
        IRequiredBundleDescription[] reqBundles = Lists.transform(dependencies,
                new IDToRequireBundleTransformer(service)).toArray(new IRequiredBundleDescription[dependencies.size()]);
        bundleDesc.setRequiredBundles(reqBundles);
        IPackageImportDescription[] importArray = Lists.transform(packageImports,
                new IDToPackageImportTransformer(service))
                .toArray(new IPackageImportDescription[packageImports.size()]);
        bundleDesc.setPackageImports(importArray);
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
        checkOpenPDEProject(project);
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            if (Objects.equals(bundleDesc.getSymbolicName(), dependency)) {
                return true;
            }
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
     * Ensures that the given plug-in project is declared a singleton. This is required for extensions.
     * @param project
     * @throws CoreException
     * @since 2.0
     */
    public static void ensureSingletonDeclaration(IProject project) throws CoreException {
        checkOpenPDEProject(project);
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            bundleDesc.setSingleton(true);
            bundleDesc.apply(new NullProgressMonitor());
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
        ensureBundleDependenciesAndPackageImports(project, dependencies, Collections.<String>emptyList(), new NullProgressMonitor());
    }

    /**
     * Updates project manifest to ensure the selected package imports are set. Does not change existing
     * package imports or required bundle declarations.
     *
     * @param project
     * @param packageImports
     * @throws CoreException
     */
    public static void ensurePackageImports(IProject project, final List<String> packageImports) throws CoreException {
        ensureBundleDependenciesAndPackageImports(project, Collections.<String>emptyList(), packageImports, new NullProgressMonitor());
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
    public static void ensureBundleDependenciesAndPackageImports(IProject project, final List<String> dependencies, final List<String> importPackages,
            IProgressMonitor monitor) throws CoreException {
        checkOpenPDEProject(project);
        if (dependencies.isEmpty() && importPackages.isEmpty()) {
            return;
        }
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            List<String> nonSelfDependencies = dependencies.stream().filter(dependency -> !Objects.equals(bundleDesc.getSymbolicName(), dependency)).collect(Collectors.toList());
            if (!nonSelfDependencies.isEmpty()) {
                ensureBundleDependencies(service, bundleDesc, nonSelfDependencies);
            }
            if (!importPackages.isEmpty()) {
                ensurePackageImports(service, bundleDesc, importPackages);
            }
            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }

    /**
     * Updates the plugin dependency settings of the given project by replacing entries according to the given map. This
     * method preserves optional and re-export flags, and updates version settings (if was originally set)
     * 
     * @param project the project to apply changes on
     * @param replacedDependencies bundle IDs to replace dependencies
     * @param versions version ranges to set for the new entries
     * @param monitor
     * @throws CoreException
     * @since 1.5
     */
    public static void replaceBundleDependencies(IProject project, 
            final Map<String, String> replacedDependencies, final Map<String, VersionRange> versions, IProgressMonitor monitor) throws CoreException{
        checkOpenPDEProject(project);
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            if (!replacedDependencies.isEmpty()) {
                replaceBundleDependencies(service, bundleDesc, replacedDependencies, versions);
            }

            bundleDesc.apply(monitor);
        } finally {
            if (context != null && ref != null) {
                context.ungetService(ref);
            }
        }
    }
    
    /**
     * Updates plugin Manifest file to replace plug-in dependencies according to the given Map
     * 
     * @param service
     * @param bundleDesc
     * @param replacedDependencies
     */
    public static void replaceBundleDependencies(IBundleProjectService service, IBundleProjectDescription bundleDesc, 
            final Map<String, String> replacedDependencies, final Map<String, VersionRange> versions){
        
        IRequiredBundleDescription[] existingDependencies = bundleDesc.getRequiredBundles();
        if (existingDependencies == null) {
            existingDependencies = new IRequiredBundleDescription[0];
        }
        
        Set<String> toRemove = new HashSet<>();
        Set<IRequiredBundleDescription> toAdd = new LinkedHashSet<>();
        
        for(IRequiredBundleDescription r : existingDependencies){
            String id = r.getName();
            String replacedId = replacedDependencies.get(id); 
            if (replacedId != null){
                VersionRange v = r.getVersionRange();
                toRemove.add(id);
                if (v != null){
                    v = versions.get(replacedId);
                }
                toAdd.add(service.newRequiredBundle(replacedId, v, r.isOptional(), r.isExported()));
            }
        }
        
        List<IRequiredBundleDescription> dependencies = new LinkedList<>();
        for(IRequiredBundleDescription r : existingDependencies){
            if (!toRemove.contains(r.getName())){
                dependencies.add(r);
            }
        }
        dependencies.addAll(toAdd);
        bundleDesc.setRequiredBundles(dependencies.toArray(new IRequiredBundleDescription[dependencies.size()]));
    }
    
    
    /**
     * Updates project manifest to ensure the selected bundle dependencies are set. Does not change existing
     * dependencies.
     *
     * @param service
     * @param bundleDesc
     * @param dependencyNames
     */
    static void ensureBundleDependencies(IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final List<String> dependencyNames) {
        
        IRequiredBundleDescription[] existingDependencies = bundleDesc.getRequiredBundles();
        if (existingDependencies == null) {
            List<IRequiredBundleDescription> missingDependencies = Lists.transform(dependencyNames, new IDToRequireBundleTransformer(service));
            bundleDesc.setRequiredBundles(Iterables.toArray(missingDependencies, IRequiredBundleDescription.class));
            
        } else {
            List<String> missingDependencyNames = new ArrayList<>(dependencyNames);
            for (IRequiredBundleDescription bundle : existingDependencies) {
                if (missingDependencyNames.contains(bundle.getName())) {
                    missingDependencyNames.remove(bundle.getName());
                }
            }
            List<IRequiredBundleDescription> missingDependencies = Lists.transform(missingDependencyNames, new IDToRequireBundleTransformer(service));

            // Since Kepler setRequiredBundles overwrites existing dependencies
            Iterable<IRequiredBundleDescription> dependenciesToSet =
                Iterables.concat(missingDependencies, Arrays.asList(existingDependencies));

            bundleDesc.setRequiredBundles(Iterables.toArray(dependenciesToSet, IRequiredBundleDescription.class));
        }
    }
    
    /**
     * Updates project manifest to ensure the selected bundle dependencies are set. Does not change existing
     * dependencies.
     *
     * @param service
     * @param bundleDesc
     * @param packageImports
     */
    static void ensurePackageImports(final IBundleProjectService service, IBundleProjectDescription bundleDesc,
            final List<String> packageImports) {
        IPackageImportDescription[] importArray = bundleDesc.getPackageImports();
        List<IPackageImportDescription> importList = importArray == null ? Lists
                .<IPackageImportDescription> newArrayList() : Arrays.asList(importArray);
        List<String> newImports = Lists.newArrayList(packageImports);
        for (IPackageImportDescription importDecl : importList) {
            final String packageName = importDecl.getName();
            if (packageImports.contains(packageName)) {
                newImports.remove(packageName);
            }
        }
        importList.addAll(Lists.transform(newImports, new IDToPackageImportTransformer(service)));
        bundleDesc.setPackageImports(importList.toArray(new IPackageImportDescription[importList.size()]));
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
                INVALID_PROJECT_MESSAGE, project.getName());
        if (exports.isEmpty()) {
            return;
        }

        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
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
                INVALID_PROJECT_MESSAGE, project.getName());
        if (dependencies.isEmpty()) {
            return;
        }

        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            removePackageExports(bundleDesc, dependencies);
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
        Set<String> missingExports = new HashSet<>(exports);
        List<IPackageExportDescription> exportList = new ArrayList<>();
        if (packageExports != null) {
            for (IPackageExportDescription export : packageExports) {
                missingExports.remove(export.getName());
                exportList.add(export);
            }
        }
        exportList.addAll(Collections2.transform(missingExports, input -> service.newPackageExport(input, null, true, null)));

        bundleDesc.setPackageExports(exportList.toArray(new IPackageExportDescription[exportList.size()]));
    }

    /**
     * Updates project manifest to ensure the selected packages are removed. Does not change existing exports.
     *
     * @param bundleDesc
     * @param exports
     */
    static void removePackageExports(IBundleProjectDescription bundleDesc, final List<String> exports) {
        IPackageExportDescription[] packageExports = bundleDesc.getPackageExports();
        List<IPackageExportDescription> exportList = new ArrayList<>();
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
                INVALID_PROJECT_MESSAGE, project.getName());

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
     * Ensures that the project contains the output folder specified by its configuration as source folder.
     *
     * @param project
     *            an existing, open plug-in project
     * @param outputConfigurations output configurations defining the output folder
     * @param monitor
     * @throws CoreException
     * @since 2.0
     */
    public static void ensureSourceFolder(IProject project, Collection<OutputConfiguration> outputConfigurations, IProgressMonitor monitor) throws CoreException {
        if (!outputConfigurations.isEmpty()) {
            String sourceFolder = outputConfigurations.iterator().next().getOutputDirectory();
            ProjectGenerationHelper.ensureSourceFolder(project, sourceFolder, monitor);
        }
    }

    /**
     * Ensures that the project contains the required folder as source folder.
     *
     * @param project
     *            an existing, open plug-in project
     * @param folder a project-relative path encoded as a string           
     * @param monitor
     * @throws CoreException
     * @since 1.5
     */
    public static void ensureSourceFolder(IProject project, String folder, IProgressMonitor monitor) throws CoreException {
        ensureSourceFolders(project, ImmutableList.of(folder), monitor);
    }

    /**
     * Ensures that the project contains the provided folders as source folders.
     *
     * @param project
     *            an existing, open plug-in project
     * @param requiredSourceFolders a list of strings representing project-relative paths for source folders
     * @param monitor
     * @throws CoreException
     * @since 1.5
     */
    public static void ensureSourceFolders(IProject project, List<String> requiredSourceFolders, IProgressMonitor monitor) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.isOpen() && (PDE.hasPluginNature(project)),
                INVALID_PROJECT_MESSAGE, project.getName());
        BundleContext context = null;
        ServiceReference<IBundleProjectService> ref = null;
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService.class);
            final IBundleProjectService service = context.getService(ref);
            IBundleProjectDescription bundleDesc = service.getDescription(project);
            bundleDesc.setBundleClasspath(getUpdatedBundleClasspathEntries(bundleDesc.getBundleClasspath(), requiredSourceFolders, service));
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
            final List<String> requiredSourceFolders, final IBundleProjectService service) {
        
        final Collection<String> existingSourceEntries = (oldClasspath == null) ? new ArrayList<>() : getExistingSourceEntries(oldClasspath);
        
        Collection<String> missingSourceFolders = Collections2.filter(requiredSourceFolders, entry -> !existingSourceEntries.contains(entry));
        Collection<IBundleClasspathEntry> newClasspathEntries = Collections2.transform(missingSourceFolders,
                input -> service.newBundleClasspathEntry(new Path(input), null, null));

        List<IBundleClasspathEntry> modifiedClasspathEntries = Lists.newArrayList(oldClasspath);
        modifiedClasspathEntries.addAll(newClasspathEntries);
        return modifiedClasspathEntries.toArray(new IBundleClasspathEntry[modifiedClasspathEntries.size()]);
    }
    
    private static Collection<String> getExistingSourceEntries(final IBundleClasspathEntry[] oldClasspath) {
        Collection<IBundleClasspathEntry> classPathSourceList = Collections2.filter(Lists.newArrayList(oldClasspath),
                entry -> entry.getSourcePath() != null && !entry.getSourcePath().isEmpty());
        return Collections2.transform(classPathSourceList,
                entry -> entry.getSourcePath().toString());
    }

    public static String getBundleSymbolicName(IProject project) {
        IPluginModelBase plugin = PDECore.getDefault().getModelManager().findModel(project);
        return plugin.getBundleDescription().getSymbolicName();
    }

}
