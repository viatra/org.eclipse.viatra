/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ScopeBasedSelectable;
import org.eclipse.xtext.xbase.scoping.XImportSectionNamespaceScopeProvider;
import org.eclipse.xtext.xtype.XImportSection;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageImportNamespaceProvider extends XImportSectionNamespaceScopeProvider {

    private static final QualifiedName VIATRA_AGGREGATORS = QualifiedName.create("org","eclipse","viatra","query","runtime","matchers","aggregators");
    
    @Inject
    IQualifiedNameConverter nameConverter;
    
    /**
     * @since 2.4
     */
    @Override
    protected IScope getResourceScope(IScope globalScope, Resource res, EReference reference) {
        IScope result = globalScope;
        ISelectable globalScopeSelectable = new ScopeBasedSelectable(result);
        
        // implicit imports (i.e. java.lang.*)
        List<ImportNormalizer> normalizers = getImplicitImports(isIgnoreCase(reference));
        
        // Also add an import normalizer for the current package declaration for pattern references
        if (reference == PatternLanguagePackage.Literals.PATTERN_CALL__PATTERN_REF) {
            res.getContents().stream()
                .filter(PatternModel.class::isInstance)
                .map(PatternModel.class::cast)
                .map(PatternModel::getPackageName)
                .filter(Objects::nonNull)
                .map(nameConverter::toQualifiedName)
                .findFirst()
                .map(packageName -> doCreateImportNormalizer(packageName, true, false))
                .ifPresent(normalizers::add);
        }
        
        if (!normalizers.isEmpty()) {
            result = createImportScope(result, normalizers, globalScopeSelectable, reference.getEReferenceType(), isIgnoreCase(reference));
        }
        
        return result;
    }
    
    /**
     * @since 1.4
     */
    @Override
    protected List<ImportNormalizer> getImplicitImports(boolean ignoreCase) {
        return Lists.<ImportNormalizer>newArrayList(
                doCreateImportNormalizer(JAVA_LANG, true, false),
                doCreateImportNormalizer(XBASE_LIB, true, false),
                doCreateImportNormalizer(VIATRA_AGGREGATORS, true, false));
    }
    
    @Override
    protected IScope internalGetScope(IScope parent, IScope globalScope, EObject context, EReference reference) {
        if (context instanceof PatternImport) {
            return globalScope;
        }
        return super.internalGetScope(parent, globalScope, context, reference);
    }

    @Override
    protected List<ImportNormalizer> getImportedNamespaceResolvers(XImportSection importSection, boolean ignoreCase) {
        List<ImportNormalizer> parentNormalizers = super.getImportedNamespaceResolvers(importSection, ignoreCase);
        List<PatternImport> patternImportDeclarations;
        if (importSection instanceof VQLImportSection) {
            patternImportDeclarations = ((VQLImportSection) importSection).getPatternImport();
        } else {
            patternImportDeclarations = Lists.newArrayList();
        }
        List<ImportNormalizer> result = Lists.newArrayListWithExpectedSize(patternImportDeclarations.size()
                + parentNormalizers.size());
        for (PatternImport imp : patternImportDeclarations) {
            ImportNormalizer resolver = createImportedNamespaceResolver(
                    PatternLanguageHelper.getFullyQualifiedName(imp.getPattern()), ignoreCase);
            if (resolver != null) {
                result.add(resolver);
            }
        }
        result.addAll(parentNormalizers);
        return result;
    }
}
