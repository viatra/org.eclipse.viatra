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
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra.query.patternlanguage.emf.ResolutionException;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.util.PatternLanguageSwitch;
import org.eclipse.viatra.query.patternlanguage.scoping.MyAbstractDeclarativeScopeProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * <p>
 * An extended abstract declarative scope provider to facilitate the reusing of abstract declarative scope providers
 * together with XBase scope provider.
 * </p>
 * <p>
 * See <a
 * href="http://www.eclipse.org/forums/index.php/mv/msg/219841/699521/#msg_699521">http://www.eclipse.org/forums/index
 * .php/mv/msg/219841/699521/#msg_699521</a> for details.
 * </p>
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageDeclarativeScopeProvider extends MyAbstractDeclarativeScopeProvider {
    @Inject
    private IQualifiedNameConverter qualifiedNameConverter;
    
    @Inject
    private IMetamodelProvider metamodelProvider;

    public IScope scope_EPackage(PackageImport ctx, EReference ref) {
        return metamodelProvider.getAllMetamodelObjects(this.delegateGetScope(ctx, ref), ctx);
    }
    
    public IScope scope_PackageImport(EObject ctx, EReference ref) {
        EObject root = getRootContainer(ctx);
        if (root instanceof PatternModel) {
            SimpleAttributeResolver<PackageImport, String> attributeResolver = SimpleAttributeResolver.<PackageImport, String>newResolver(String.class, "alias");
            final EList<PackageImport> elements = ((PatternModel) root).getImportPackages().getPackageImport();
            return new SimpleScope(IScope.NULLSCOPE,Scopes.scopedElementsFor(elements, QualifiedName.wrapper(attributeResolver)));
        } else {
            return IScope.NULLSCOPE;
        }
    }

    public IScope scope_EClassifier(EObject ctx, EReference ref) {
        // The context is general as content assist might ask for different context types
        if (ctx instanceof ClassType) {
            return scope_EClassifier((ClassType)ctx, ref);
        }
        return createUnqualifiedClassifierScope(ctx);
    }
    
    public IScope scope_EClassifier(ClassType ctx, EReference ref) {
        if (ctx.getMetamodel() != null && !ctx.getMetamodel().eIsProxy()) {
            return createClassifierScope(ctx.getMetamodel().getEPackage(), IScope.NULLSCOPE);
        }
        return createUnqualifiedClassifierScope(ctx);
    }

    public IScope scope_Variable(PatternBody ctx, EReference ref) {
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

    public IScope scope_EStructuralFeature(PathExpressionHead ctx, EReference ref) {
        // This is needed for content assist - in that case the ExpressionTail does not exists
        return expressionParentScopeProvider.doSwitch(ctx);
    }

    public IScope scope_EStructuralFeature(PathExpressionTail ctx, EReference ref) {
        return expressionParentScopeProvider.doSwitch(ctx.eContainer());
    }

    public IScope scope_EEnum(EnumValue ctx, EReference ref) {
        PatternModel model = (PatternModel) getRootContainer(ctx);
        final Collection<EEnum> enums = Lists.newArrayList();
        for (PackageImport decl : EMFPatternLanguageHelper.getPackageImportsIterable(model)) {
            if (decl.getEPackage() != null) {
                Iterables.addAll(enums, Iterables.filter(decl.getEPackage().getEClassifiers(), EEnum.class));
            }
        }
        return Scopes.scopeFor(enums);
    }

    public IScope scope_EEnumLiteral(EnumValue ctx, EReference ref) {
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
        return Scopes.scopeFor(literals, new Function<EEnumLiteral, QualifiedName>() {
            @Override
            public QualifiedName apply(EEnumLiteral literal) {
                QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(literal.getLiteral());
                return qualifiedName;
            }
        }, IScope.NULLSCOPE);
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
