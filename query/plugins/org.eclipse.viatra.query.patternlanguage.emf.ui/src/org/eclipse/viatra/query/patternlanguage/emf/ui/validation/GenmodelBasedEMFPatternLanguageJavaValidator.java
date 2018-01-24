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

package org.eclipse.viatra.query.patternlanguage.emf.ui.validation;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes;
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator;
import org.eclipse.viatra.query.tooling.core.generator.genmodel.IVQGenmodelProvider;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.google.inject.Inject;

public class GenmodelBasedEMFPatternLanguageJavaValidator extends EMFPatternLanguageValidator {

    @Inject
    private IVQGenmodelProvider genmodelProvider;
    @Inject
    private IJavaProjectProvider projectProvider;
    @Inject
    private Logger logger;
    @Inject
    private IMetamodelProvider metamodelProvider;
    @Inject
    private TypeReferences typeReferences;
    
    @Check(CheckType.NORMAL)
    public void checkImportDependency(PackageImport importDecl) {
        Resource res = importDecl.eResource();
        if (projectProvider == null || res == null) {
            return;
        }
        ResourceSet resourceSet = res.getResourceSet();
        IJavaProject javaProject = projectProvider.getJavaProject(resourceSet);
        if (javaProject == null) {
            return;
        }
        IProject project = javaProject.getProject();
        EPackage ePackage = importDecl.getEPackage();
        GenPackage genPackage = genmodelProvider.findGenPackage(importDecl, ePackage);
        if (genPackage != null) {
            final GenModel genmodel = genPackage.getGenModel();
            if (genmodel != null) {
                String modelPluginID = genmodel.getModelPluginID();
                checkModelPluginDependencyOnProject(importDecl, project, ePackage, modelPluginID);
            }
        } else {
            String contributorId = metamodelProvider.getModelPluginId(ePackage, resourceSet);
            if(contributorId != null) {
                checkModelPluginDependencyOnProject(importDecl, project, ePackage, contributorId);
            }
        }
    }

    protected void checkModelPluginDependencyOnProject(PackageImport importDecl, IProject project, EPackage ePackage,
            String modelPluginID) {
        try {
            if (modelPluginID != null && !modelPluginID.isEmpty() && !modelPluginID.equals(project.getName())
                    && !ProjectGenerationHelper.checkBundleDependency(project, modelPluginID)) {
                warning(String.format(
                        "To refer elements from the Package %s the bundle %s must be added as dependency",
                        ePackage.getNsURI(), modelPluginID), importDecl,
                        PatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
                        IssueCodes.IMPORT_DEPENDENCY_MISSING, modelPluginID);
            }
        } catch (CoreException e) {
            logger.error("Error while checking the dependencies of the import declaration", e);
        }
    }
    
    @Check(CheckType.NORMAL)
    public void checkClassPath(ClassType typeDecl) {
        Resource resource = typeDecl.eResource();
        if (resource == null) {
            return;
        }
        ResourceSet resourceSet = resource.getResourceSet();
        if (resourceSet == null) {
            return;
        }
        IJavaProject javaProject = projectProvider.getJavaProject(resourceSet);
        if (javaProject == null) {
            return;
        }
        EClassifier classifier = typeDecl.getClassname();
        String clazz = metamodelProvider.getQualifiedClassName(classifier, classifier);
        if (clazz != null && !clazz.isEmpty() && typeReferences.findDeclaredType(clazz, typeDecl) == null) {
            error(String.format("Couldn't find type %s on the project's classpath", clazz), typeDecl, null,
                    IssueCodes.TYPE_NOT_ON_CLASSPATH, classifier.getEPackage().getNsURI());
        }
    }
}
