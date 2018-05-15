/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageConfigurationConstants;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProvider;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ExecutionType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Modifiers;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.xtext.common.types.JvmEnumerationType;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Classpath validators for VQL language
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class ClasspathValidator extends AbstractDeclarativeValidator {
    
    @Inject
    private TypeReferences typeReferences;
    @Inject
    private IMetamodelProvider metamodelProvider;
    @Named(EMFPatternLanguageConfigurationConstants.VALIDATE_CLASSPATH_KEY)
    private boolean classpathValidationEnabled;

    @Check
    public void checkClassPath(PatternModel modelFile) {
        if (!classpathValidationEnabled) {
            return;
        }
        final JvmGenericType listType = (JvmGenericType) typeReferences.findDeclaredType(List.class, modelFile);
        if (listType == null || listType.getTypeParameters().isEmpty()) {
            error("Couldn't find a JDK 1.5 or higher on the project's classpath.", modelFile,
                    PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME, IssueCodes.JDK_NOT_ON_CLASSPATH);
        } else if (typeReferences.findDeclaredType(ViatraQueryEngine.class, modelFile) == null) {
            error("Couldn't find the mandatory library 'org.eclipse.viatra.query.runtime' on the project's classpath.",
                    modelFile, PatternLanguagePackage.Literals.PATTERN_MODEL__PACKAGE_NAME,
                    IssueCodes.IQR_NOT_ON_CLASSPATH, "org.eclipse.viatra.query.runtime");
        }
    }
    
    @Check
    public void checkClassPath(Modifiers modifier) {
        if (!classpathValidationEnabled) {
            return;
        }
        if (modifier.getExecution() == ExecutionType.SEARCH) {
            final JvmEnumerationType lsBackendType = (JvmEnumerationType) typeReferences.findDeclaredType(LocalSearchEMFBackendFactory.class, modifier);
            if (lsBackendType == null || lsBackendType.eIsProxy()) {
                error("Couldn't find the mandatory library 'org.eclipse.viatra.query.runtime.localsearch' on the project's classpath.",
                        modifier, PatternLanguagePackage.Literals.MODIFIERS__EXECUTION,
                        IssueCodes.IQR_NOT_ON_CLASSPATH, "org.eclipse.viatra.query.runtime.localsearch");
            }
        }
    }

    @Check(CheckType.NORMAL)
    public void checkClassPath(ClassType typeDecl) {
        if (!classpathValidationEnabled) {
            return;
        }
        EClassifier classifier = typeDecl.getClassname();
        String clazz = metamodelProvider.getQualifiedClassName(classifier, classifier);
        if (clazz != null && !clazz.isEmpty() && typeReferences.findDeclaredType(clazz, typeDecl) == null) {
            error(String.format("Couldn't find type %s on the project's classpath", clazz), typeDecl, null,
                    IssueCodes.TYPE_NOT_ON_CLASSPATH, classifier.getEPackage().getNsURI());
        }
    }

    @Check
    public void checkPackageImportGeneratedCode(PackageImport packageImport) {
        if (!classpathValidationEnabled) {
            return;
        }
        if (packageImport.getEPackage() != null && packageImport.getEPackage().getNsURI() != null && !metamodelProvider
                .isGeneratedCodeAvailable(packageImport.getEPackage(), packageImport.eResource().getResourceSet())) {
            warning(String.format(
                    "The generated code of the Ecore model %s cannot be found. Check the org.eclipse.emf.ecore.generated_package extension in the model project or consider setting up a generator model for the generated code to work.",
                    packageImport.getEPackage().getNsURI()),
                    PatternLanguagePackage.Literals.PACKAGE_IMPORT__EPACKAGE,
                    IssueCodes.IMPORT_WITH_GENERATEDCODE);
        }
    }
}
