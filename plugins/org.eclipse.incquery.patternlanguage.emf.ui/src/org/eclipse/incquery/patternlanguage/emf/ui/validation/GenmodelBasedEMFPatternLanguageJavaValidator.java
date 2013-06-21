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

package org.eclipse.incquery.patternlanguage.emf.ui.validation;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFIssueCodes;
import org.eclipse.incquery.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.patternlanguage.validation.IssueCodes;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Check;

import com.google.inject.Inject;

public class GenmodelBasedEMFPatternLanguageJavaValidator extends EMFPatternLanguageJavaValidator {

    @Inject
    private IEiqGenmodelProvider genmodelProvider;
    @Inject
    private IJavaProjectProvider projectProvider;
    @Inject
    private IStorage2UriMapper storage2UriMapper;
    @Inject
    private Logger logger;

    @Check
    public void checkPackageDeclaration(PatternModel model) {
		String actualPackage = getActualPackageName(model);
		String declaredPackage = model.getPackageName();
		
		if (!Strings.equal(actualPackage, declaredPackage)) {
			error(String.format(
					"The package declaration '%s' does not match the container '%s'",
					Strings.emptyIfNull(declaredPackage),
					Strings.emptyIfNull(actualPackage)),
					PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
					IssueCodes.PACKAGE_NAME_MISMATCH);
		}
    }
    
    /*
     * Based on org.eclipse.xtend.ide.validator.XtendUIValidator.java
     */
    protected String getActualPackageName(PatternModel model) {
    	URI fileURI = model.eResource().getURI();
		for(Pair<IStorage, IProject> storage: storage2UriMapper.getStorages(fileURI)) {
			if(storage.getFirst() instanceof IFile) {
				IPath fileWorkspacePath = storage.getFirst().getFullPath();
				IJavaProject javaProject = JavaCore.create(storage.getSecond());
				if(javaProject != null && javaProject.exists() && javaProject.isOpen()) {
					try {
						for(IPackageFragmentRoot root: javaProject.getPackageFragmentRoots()) {
							if(!root.isArchive() && !root.isExternal()) {
								IResource resource = root.getResource();
								if(resource != null) {
									IPath sourceFolderPath = resource.getFullPath();
									if(sourceFolderPath.isPrefixOf(fileWorkspacePath)) {
										IPath classpathRelativePath = fileWorkspacePath.makeRelativeTo(sourceFolderPath);
										return classpathRelativePath.removeLastSegments(1).toString().replace("/", ".");
									}
								}
							}
						}
					} catch (JavaModelException e) {
						logger.error("Error resolving package declaration for Pattern Model", e);
					}
				}
			}
		}
		return null;
    }
    
    @Check
    public void checkImportDependency(PackageImport importDecl) {
        Resource res = importDecl.eResource();
        if (projectProvider == null || res == null) {
            return;
        }
        IJavaProject javaProject = projectProvider.getJavaProject(res.getResourceSet());
        if (javaProject == null) {
        	return;
        }
		IProject project = javaProject.getProject();
        GenPackage genPackage = genmodelProvider.findGenPackage(importDecl, importDecl.getEPackage());
        if (genPackage != null) {
            final GenModel genmodel = genPackage.getGenModel();
            if (genmodel != null) {
                String modelPluginID = genmodel.getModelPluginID();
                try {
                    if (modelPluginID != null && !modelPluginID.isEmpty()
                            && !ProjectGenerationHelper.checkBundleDependency(project, modelPluginID)) {
                        error(String.format(
                                "To refer elements from the Package %s the bundle %s must be added as dependency",
                                importDecl.getEPackage().getNsURI(), modelPluginID), importDecl,
                                EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
                                EMFIssueCodes.IMPORT_DEPENDENCY_MISSING, modelPluginID);
                    }
                } catch (CoreException e) {
                    logger.error("Error while checking the dependencies of the import declaration", e);
                }
            }
        }
    }
}
