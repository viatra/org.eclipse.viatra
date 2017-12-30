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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra.query.patternlanguage.emf.ResolutionException;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.util.PatternLanguageSwitch;
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
            } else if (EcoreUtil2.isAssignableFrom(EMFPatternLanguagePackage.Literals.PACKAGE_IMPORT, refType)) {
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
                PathExpressionTail tail = EcoreUtil2.getContainerOfType(ctx, PathExpressionTail.class);
                if (tail != null) {
                    return scope_EStructuralFeature(tail);
                } else {
                    PathExpressionHead head = EcoreUtil2.getContainerOfType(ctx, PathExpressionHead.class);
                    return scope_EStructuralFeature(head);
                }
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
        
        for (PackageImport decl : EMFPatternLanguageHelper.getPackageImportsIterable(model)) {
            if (decl.getEPackage() != null) {
                scope = createClassifierScope(decl.getEPackage(), scope);
            }
        }
        return scope;
    }
    
    protected IScope createClassifierScope(EPackage ePackage, IScope outer) {
        return Scopes.scopeFor(ePackage.getEClassifiers(), outer);
    }

    private IScope scope_EStructuralFeature(PathExpressionHead ctx) {
        // This is needed for content assist - in that case the ExpressionTail does not exists
        return expressionParentScopeProvider.doSwitch(ctx);
    }

    private IScope scope_EStructuralFeature(PathExpressionTail ctx) {
        return expressionParentScopeProvider.doSwitch(ctx.eContainer());
    }

    private IScope scope_EEnum(EnumValue ctx) {
        PatternModel model = (PatternModel) getRootContainer(ctx);
        final Collection<EEnum> enums = Lists.newArrayList();
        for (PackageImport decl : EMFPatternLanguageHelper.getPackageImportsIterable(model)) {
            if (decl.getEPackage() != null) {
                Iterables.addAll(enums, Iterables.filter(decl.getEPackage().getEClassifiers(), EEnum.class));
            }
        }
        return Scopes.scopeFor(enums);
    }

    private IScope scope_EEnumLiteral(EnumValue ctx) {
        EEnum type;
        try {
            type = ctx.getEnumeration();
            type = (type != null) ? type : EMFPatternLanguageScopeHelper
                    .calculateEnumerationType((PathExpressionHead) ctx.eContainer());
        } catch (ResolutionException e) {
            return IScope.NULLSCOPE;
        }
        return calculateEnumLiteralScope(type);
    }

    private IScope calculateEnumLiteralScope(EEnum enumeration) {
        EList<EEnumLiteral> literals = enumeration.getELiterals();
        return Scopes.scopeFor(literals, 
                literal -> qualifiedNameConverter.toQualifiedName(literal.getLiteral()), IScope.NULLSCOPE);
    }

    private final ParentScopeProvider expressionParentScopeProvider = new ParentScopeProvider();

    static class ParentScopeProvider extends PatternLanguageSwitch<IScope> {

        @Override
        public IScope casePathExpressionHead(PathExpressionHead object) {
            return calculateReferences(object.getType());
        }

        @Override
        public IScope casePathExpressionTail(PathExpressionTail object) {
            return calculateReferences(object.getType());
        }

        private IScope calculateReferences(Type type) {
            List<EStructuralFeature> targetReferences = Collections.emptyList();
            if (type instanceof ReferenceType) {
                EClassifier referredType = ((ReferenceType) type).getRefname().getEType();
                if (referredType instanceof EClass) {
                    targetReferences = ((EClass) referredType).getEAllStructuralFeatures();
                }
            } else if (type instanceof ClassType) {
                EClassifier classifier = ((ClassType) type).getClassname();
                if (classifier instanceof EClass) {
                    targetReferences = (((EClass) classifier).getEAllStructuralFeatures());
                }
            }
            if (targetReferences.isEmpty()) {
                return IScope.NULLSCOPE;
            }
            return Scopes.scopeFor(targetReferences);
        }
    }
}
