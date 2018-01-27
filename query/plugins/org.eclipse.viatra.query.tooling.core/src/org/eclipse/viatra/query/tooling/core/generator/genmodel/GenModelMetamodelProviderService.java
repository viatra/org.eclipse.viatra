/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.core.generator.genmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra.query.patternlanguage.emf.helper.GeneratorModelHelper;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.BaseMetamodelProviderService;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProviderInstance;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.GeneratorModelFactory;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.GeneratorModelReference;
import org.eclipse.viatra.query.tooling.generator.model.generatorModel.ViatraQueryGeneratorModel;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GenModelMetamodelProviderService extends BaseMetamodelProviderService implements IVQGenmodelProvider, IMetamodelProviderInstance {

    @Inject
    private IVQGenmodelProvider provider;
    
    private static final class ParentScopeFilter implements Predicate<IEObjectDescription> {

        private final Iterable<IEObjectDescription> referencedPackages;

        public ParentScopeFilter(Iterable<IEObjectDescription> referencedPackages) {
            super();
            this.referencedPackages = referencedPackages;
        }

        @Override
        public boolean apply(IEObjectDescription desc) {
            Objects.requireNonNull(desc);

            return !Iterables.contains(Iterables.transform(referencedPackages, IEObjectDescription::getQualifiedName),
                    desc.getQualifiedName());
        }
    }

    @Inject
    private IJavaProjectProvider projectProvider;


    @Override
    public String getIdentifier() {
        return "genmodel";
    }

    @Override
    public int getPriority() {
        return 0;
    }
    
    private URI getGenmodelURI(IProject project) {
        IFile file = project.getFile(ViatraQueryNature.VQGENMODEL);
        return URI.createPlatformResourceURI(file.getFullPath().toString(), false);
    }

    @Override
    public IScope getAllMetamodelObjects(IScope delegateScope, EObject ctx) {
        Objects.requireNonNull(ctx, "Context is required");
        Iterable<IEObjectDescription> referencedPackages = new ArrayList<>();
        ViatraQueryGeneratorModel generatorModel = getGeneratorModel(ctx);
        if (generatorModel != null) {
            for (GeneratorModelReference generatorModelReference : generatorModel.getGenmodels()) {
                Iterable<IEObjectDescription> packages = Iterables.transform(
                        getAllGenPackages(generatorModelReference.getGenmodel()),
                        from -> {
                            EPackage ePackage = from.getEcorePackage();
                            QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(ePackage
                                    .getNsURI());
                            return EObjectDescription.create(qualifiedName, ePackage,
                                    Collections.singletonMap("nsURI", "true"));
                        });
                referencedPackages = Iterables.concat(referencedPackages, packages);
            }
        }
        // The FilteringScope is used to ensure elements in vql genmodel are not accidentally found in the parent
        // version
        return new SimpleScope(new FilteringScope(super.getAllMetamodelObjects(delegateScope, ctx), new ParentScopeFilter(
                referencedPackages)), referencedPackages);
    }

    @Override
    public Collection<EPackage> getAllMetamodelObjects(IProject project) throws CoreException {
        Preconditions.checkArgument(project.exists() && project.hasNature(ViatraQueryNature.NATURE_ID),
                "Only works for VIATRA Query projects");
        Set<EPackage> referencedPackages = new LinkedHashSet<>();
        ViatraQueryGeneratorModel generatorModel = getGeneratorModel(project);
        for (GeneratorModelReference generatorModelReference : generatorModel.getGenmodels()) {
            referencedPackages.addAll(Lists.transform(getAllGenPackages(generatorModelReference.getGenmodel()),
                    desc -> desc.getEcorePackage()));
        }

        referencedPackages.addAll(getGenmodelRegistry().getPackages());
        return referencedPackages;
    }

    @Override
    public EPackage loadEPackage(final String packageUri, ResourceSet set) {
        GenPackage loadedPackage = findGenPackage(set, packageUri);
        if (loadedPackage != null) {
            return loadedPackage.getEcorePackage();
        }
        return null;
    }

    @Override
    public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
        return (findGenPackage(set, ePackage) != null) || super.isGeneratedCodeAvailable(ePackage, set);
    }
    
    @Override
    public String getModelPluginId(EPackage ePackage, ResourceSet set) {
        String modelPluginId = getModelPluginId(findGenPackage(set, ePackage));
        if(modelPluginId != null){
            return modelPluginId;
        } else {
            return super.getModelPluginId(ePackage, set);
        }
    }

    @Override
    public ViatraQueryGeneratorModel getGeneratorModel(EObject pattern) {
        Resource res = pattern.eResource();
        if (res != null && projectProvider != null) {
            ResourceSet set = res.getResourceSet();
            return getGeneratorModel(set);
        }
        throw new IllegalArgumentException("The project of the context cannot be determined.");
    }

    public ViatraQueryGeneratorModel getGeneratorModel(IProject project) {
        return getGeneratorModel(project, new ResourceSetImpl());
    }

    public ViatraQueryGeneratorModel getGeneratorModel(ResourceSet set) {
        if (projectProvider != null) {
            IJavaProject javaProject = projectProvider.getJavaProject(set);
            if (javaProject != null) {
                return getGeneratorModel(javaProject.getProject(), set);
            }
        }
        return null;
    }

    @Override
    public ViatraQueryGeneratorModel getGeneratorModel(IProject project, ResourceSet set) {
        IFile file = project.getFile(ViatraQueryNature.VQGENMODEL);
        if (file.exists()) {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            Resource resource = set.getResource(uri, true);
            if (!resource.getContents().isEmpty()) {
                return (ViatraQueryGeneratorModel) resource.getContents().get(0);
            }
        }
        return GeneratorModelFactory.eINSTANCE.createViatraQueryGeneratorModel();
    }

    @Override
    public void saveGeneratorModel(IProject project, ViatraQueryGeneratorModel generatorModel) throws IOException {
        Resource eResource = generatorModel.eResource();
        if (eResource != null) {
            eResource.save(new HashMap<>());
        } else {
            URI uri = getGenmodelURI(project);
            ResourceSet set = new ResourceSetImpl();
            Resource resource = set.createResource(uri);
            resource.getContents().add(generatorModel);
            resource.save(new HashMap<>());
        }

    }

    @Override
    public GenPackage findGenPackage(EObject ctx, final EPackage ePackage) {
        if (ePackage == null || ePackage.eIsProxy()) {
            return null;
        }
        return findGenPackage(ctx, ePackage.getNsURI());
    }

    @Override
    public GenPackage findGenPackage(EObject ctx, final String packageNsUri) {
        ViatraQueryGeneratorModel vqGenModel = getGeneratorModel(ctx);
        return findGenPackage(vqGenModel, ctx.eResource().getResourceSet(), packageNsUri);
    }

    @Override
    public GenPackage findGenPackage(ResourceSet set, final EPackage ePackage) {
        return findGenPackage(set, ePackage.getNsURI());
    }

    @Override
    public GenPackage findGenPackage(ResourceSet set, final String packageNsUri) {
        ViatraQueryGeneratorModel vqGenModel = getGeneratorModel(set);
        return findGenPackage(vqGenModel, set, packageNsUri);
    }

    private GenPackage findGenPackage(ViatraQueryGeneratorModel vqGenModel, ResourceSet set, final String packageNsUri) {
        // vqGenModel is null if loading a pattern from the registry
        // in this case only fallback to package Registry works
        if (vqGenModel == null) {
            return null;
        }
        
        List<GenPackage> genPackageIterable = new ArrayList<>();
        for (GeneratorModelReference generatorModelReference : vqGenModel.getGenmodels()) {
            genPackageIterable.addAll(getAllGenPackages(generatorModelReference.getGenmodel()));
        }
        return genPackageIterable.stream()
                .filter(genPackage -> Objects.equals(packageNsUri, genPackage.getEcorePackage().getNsURI()))
                .findFirst().orElse(null);
    }

    private List<GenPackage> getAllGenPackages(GenModel genModel) {
        List<GenPackage> resultList = new ArrayList<>();
        for (GenPackage genPackage : genModel.getGenPackages()) {
            resultList.add(genPackage);
            resultList.addAll(getAllNestedGenPackages(genPackage));
        }
        return resultList;
    }

    private List<GenPackage> getAllNestedGenPackages(GenPackage outerGenPackage) {
        List<GenPackage> resultList = new ArrayList<>();
        for (GenPackage innerGenPackage : outerGenPackage.getNestedGenPackages()) {
            resultList.add(innerGenPackage);
            resultList.addAll(getAllNestedGenPackages(innerGenPackage));
        }
        return resultList;
    }

    public boolean isGeneratorModelDefined(IProject project) {
        IFile file = getGeneratorModelFile(project);
        return file.exists();
    }

    public IFile getGeneratorModelFile(IProject project) {
        return project.getFile(ViatraQueryNature.VQGENMODEL);
    }

    @Override
    public IPath getGeneratorModelPath(IProject project) {
        return getGeneratorModelFile(project).getFullPath();
    }

    /**
     * Initializes and returns the VIATRA Query generator model for the selected project. If the model is already
     * initialized, it returns the existing model.
     * 
     */
    public ViatraQueryGeneratorModel initializeGeneratorModel(IProject project, ResourceSet set) {
        IFile file = getGeneratorModelFile(project);
        if (file.exists()) {
            return getGeneratorModel(project, set);
        } else {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            Resource resource = set.createResource(uri);
            ViatraQueryGeneratorModel model = GeneratorModelFactory.eINSTANCE.createViatraQueryGeneratorModel();
            resource.getContents().add(model);
            return model;
        }
    }

    @Override
    protected Collection<String> getProvidedMetamodels() {
        return getGenmodelRegistry().getPackageUris();
    }

    @Override
    protected String doGetQualifiedClassName(EClassifier classifier, EObject context) {
        EPackage ePackage = classifier.getEPackage();
        if (ePackage != null) {
            GenPackage genPackage = provider.findGenPackage(classifier, ePackage);
            if (genPackage != null) {
                return GeneratorModelHelper.resolveTypeReference(genPackage, classifier);
            }
        }
        return null;
    }
}
