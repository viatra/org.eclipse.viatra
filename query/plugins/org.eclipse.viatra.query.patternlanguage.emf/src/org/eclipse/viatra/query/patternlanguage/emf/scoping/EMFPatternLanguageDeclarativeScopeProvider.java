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
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import static org.eclipse.emf.ecore.util.EcoreUtil.getRootContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.xbase.scoping.batch.XbaseBatchScopeProvider;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * This scope provider extends the Xbase scope provider with EMF metamodel access.
 * 
 * @author Zoltan Ujhelyi
 * @noreference This class is not intended to be referenced by clients.
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageDeclarativeScopeProvider extends XbaseBatchScopeProvider {
    
    private static final EClassifier UNRESOLVED_TYPE = EcoreFactory.eINSTANCE.createEClass();
    
    @Inject
    private IQualifiedNameConverter qualifiedNameConverter;
    
    @Inject
    private IMetamodelProvider metamodelProvider;
    
    @Override
    public IScope getScope(EObject ctx, EReference ref) {
        EClassifier refEType = ref.getEType();
        if (refEType instanceof EClass) {
            EClass refType = (EClass) refEType;

            if (EcoreUtil2.isAssignableFrom(EcorePackage.Literals.EPACKAGE, refType)) {
                PackageImport importDecl = EcoreUtil2.getContainerOfType(ctx, PackageImport.class);
                if (importDecl == null) {
                    return IScope.NULLSCOPE;
                }
                return scope_EPackage(importDecl, ref);
            } else if (EcoreUtil2.isAssignableFrom(PatternLanguagePackage.Literals.PACKAGE_IMPORT, refType)) {
                return scope_PackageImport(ctx);
            } else if (EcoreUtil2.isAssignableFrom(EcorePackage.Literals.EENUM, refType)) {
                EnumValue containingValue = EcoreUtil2.getContainerOfType(ctx, EnumValue.class);
                if (containingValue != null) {
                    return scope_EEnum(containingValue);
                }
            } else if (EcoreUtil2.isAssignableFrom(EcorePackage.Literals.ECLASSIFIER, refType)) {
                ClassType containingClassDeclaration = EcoreUtil2.getContainerOfType(ctx, ClassType.class);
                if (containingClassDeclaration != null) {
                    return scope_EClassifier(containingClassDeclaration);
                } else {
                    return scope_EClassifier(ctx);
                }
            } else if (EcoreUtil2.isAssignableFrom(PatternLanguagePackage.Literals.VARIABLE, refType)) {
                PatternBody containingBody = EcoreUtil2.getContainerOfType(ctx, PatternBody.class);
                if (containingBody != null) {
                    return scope_Variable((PatternBody) containingBody);
                }
                AnnotationParameter containingAnnotationParameter = EcoreUtil2.getContainerOfType(ctx,
                        AnnotationParameter.class);
                if (containingAnnotationParameter != null) {
                    return scope_Variable(containingAnnotationParameter);
                }
            } else if (EcoreUtil2.isAssignableFrom(EcorePackage.Literals.ESTRUCTURAL_FEATURE, refType)) {
                PathExpressionConstraint constraint = EcoreUtil2.getContainerOfType(ctx, PathExpressionConstraint.class);
                
                
                final int referenceIndex = constraint.getEdgeTypes().indexOf(ctx);
                /*
                 * Limiting access by referenceIndex serves two purposes: (1) it avoids cyclic linking by defining a
                 * clear dependency between the various elements (see reference.getRefName() and
                 * constraint.getSourceType() calls), and (2) ignores possibly previously resolved future elements that
                 * can happen when content assist is reqested in the middle of the chain
                 */
                EClassifier partialType = constraint.getEdgeTypes().stream()
                    .limit(referenceIndex >= 0 ? referenceIndex : 0)
                    .filter(Objects::nonNull)
                    .map(ReferenceType::getRefname) // resolution
                    .map(EStructuralFeature::getEType)
                    .map(obj -> obj == null ? UNRESOLVED_TYPE : obj) // Replace unresolved objects with a dedicated placeholder object
                    .reduce((a, b) -> b) //find the last element fulfilling the condition
                    .orElse(constraint.getSourceType().getClassname());
                return calculateReferences(partialType);
            } else if (EcoreUtil2.isAssignableFrom(EcorePackage.Literals.EENUM_LITERAL, refType)) {
                EnumValue containingValue = EcoreUtil2.getContainerOfType(ctx, EnumValue.class);
                if (containingValue != null) {
                    return scope_EEnumLiteral(containingValue);
                }
            }
        }
        return super.getScope(ctx, ref);
    }

    private IScope scope_EPackage(PackageImport ctx, EReference ref) {
        return metamodelProvider.getAllMetamodelObjects(delegateGetScope(ctx, ref), ctx);
    }
    
    private IScope scope_PackageImport(EObject ctx) {
        EObject root = getRootContainer(ctx);
        if (root instanceof PatternModel) {
            SimpleAttributeResolver<PackageImport, String> attributeResolver = SimpleAttributeResolver.<PackageImport, String>newResolver(String.class, "alias");
            final EList<PackageImport> elements = ((PatternModel) root).getImportPackages().getPackageImport();
            return new SimpleScope(IScope.NULLSCOPE,Scopes.scopedElementsFor(elements, QualifiedName.wrapper(attributeResolver)));
        } else {
            return IScope.NULLSCOPE;
        }
    }

    private IScope scope_EClassifier(EObject ctx) {
        // The context is general as content assist might ask for different context types
        if (ctx instanceof ClassType) {
            return scope_EClassifier((ClassType)ctx);
        }
        return createUnqualifiedClassifierScope(ctx);
    }
    
    private IScope scope_EClassifier(ClassType ctx) {
        if (ctx.getMetamodel() != null && !ctx.getMetamodel().eIsProxy()) {
            return createClassifierScope(ctx.getMetamodel().getEPackage(), IScope.NULLSCOPE);
        }
        return createUnqualifiedClassifierScope(ctx);
    }

    /**
     * @since 1.6
     */
    private IScope scope_Variable(AnnotationParameter ctx) {
        Pattern pattern = EcoreUtil2.getContainerOfType(ctx, Pattern.class);
        if (pattern != null) {
            return Scopes.scopeFor(pattern.getParameters());
        }
        return IScope.NULLSCOPE;
    }
    
    private IScope scope_Variable(PatternBody ctx) {
        if (ctx != null && !ctx.eIsProxy()) {
            return Scopes.scopeFor(ctx.getVariables());
        }
        return null;
    }
    
    protected IScope createUnqualifiedClassifierScope(EObject ctx) {
        EObject root = getRootContainer(ctx);
        if (root instanceof PatternModel) {
            return createReferencedPackagesScope((PatternModel) root);
        } else {
            return IScope.NULLSCOPE;
        }
    }
    
    protected IScope createReferencedPackagesScope(PatternModel model) {
        IScope scope = IScope.NULLSCOPE;
        
        for (PackageImport decl : PatternLanguageHelper.getPackageImportsIterable(model)) {
            if (decl.getEPackage() != null) {
                scope = createClassifierScope(decl.getEPackage(), scope);
            }
        }
        return scope;
    }
    
    protected IScope createClassifierScope(EPackage ePackage, IScope outer) {
        return Scopes.scopeFor(ePackage.getEClassifiers(), outer);
    }

    private IScope scope_EEnum(EnumValue ctx) {
        PatternModel model = (PatternModel) getRootContainer(ctx);
        final Collection<EEnum> enums = Lists.newArrayList();
        for (PackageImport decl : PatternLanguageHelper.getPackageImportsIterable(model)) {
            if (decl.getEPackage() != null) {
                Iterables.addAll(enums, Iterables.filter(decl.getEPackage().getEClassifiers(), EEnum.class));
            }
        }
        return Scopes.scopeFor(enums);
    }

    private IScope scope_EEnumLiteral(EnumValue ctx) {
        return (ctx.getEnumeration() != null) 
                    ? calculateEnumLiteralScope(ctx.getEnumeration()) 
                    : PatternLanguageHelper.getPathExpressionEMFTailType(((PathExpressionConstraint) ctx.eContainer()))
                            .filter(EEnum.class::isInstance)
                            .map(EEnum.class::cast)
                            .map(this::calculateEnumLiteralScope)
                            .orElse(IScope.NULLSCOPE);
    }

    private IScope calculateEnumLiteralScope(EEnum enumeration) {
        EList<EEnumLiteral> literals = enumeration.getELiterals();
        return Scopes.scopeFor(literals, 
                literal -> qualifiedNameConverter.toQualifiedName(literal.getLiteral()), IScope.NULLSCOPE);
    }

    private IScope calculateReferences(EClassifier referredType) {
        List<EStructuralFeature> targetReferences = Collections.emptyList();
        if (Objects.equals(referredType, UNRESOLVED_TYPE)) {
            return IScope.NULLSCOPE;
        }
        if (referredType instanceof EClass) {
            targetReferences = ((EClass) referredType).getEAllStructuralFeatures();
        }
        if (targetReferences.isEmpty()) {
            return IScope.NULLSCOPE;
        }
        return Scopes.scopeFor(targetReferences);
    }
}
