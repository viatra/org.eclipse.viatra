/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ListValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.NumberValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.typing.AbstractTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.typing.TypeInformation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author Zoltan Ujhelyi
 *
 */
@Singleton
public class EMFTypeInferrer extends AbstractTypeInferrer {

    @Inject
    private EMFTypeSystem typeSystem;
    @Inject
    private EMFPatternLanguageTypeRules rules;
    @Inject
    private IResourceScopeCache cache;
    @Inject
    private NumberLiterals literals;

    /**
     * This predicate selects a pattern that has at least one untyped parameter
     */
    private static final Predicate<Pattern> UNTYPED_PATTERN_PREDICATE = new Predicate<Pattern>(){

        @Override
        public boolean apply(Pattern input) {
            return input != null && Iterables.any(input.getParameters(), new Predicate<Variable>() {

                @Override
                public boolean apply(Variable variable) {
                    return variable.getType() == null;
                }
            });
        }
        
    };
    
    private static final Predicate<Pattern> TYPED_PATTERN_PREDICATE = Predicates.not(UNTYPED_PATTERN_PREDICATE);
    
    /**
     * @since 1.3
     */
    @Override
    public IInputKey getInferredType(Expression var) {
        final Pattern containingPattern = EcoreUtil2.getContainerOfType(var, Pattern.class);
        TypeInformation information = collectConstraints(containingPattern);
        return information.getType(var);
    }

    /**
     * @since 1.3
     */
    @Override
    public IInputKey getDeclaredType(Expression ex) {
        if (ex instanceof BoolValue) {
            return new JavaTransitiveInstancesKey(Boolean.class);
        } else if (ex instanceof NumberValue) {
            Class<? extends Number> javaType = literals.getJavaType(((NumberValue) ex).getValue());
            return new JavaTransitiveInstancesKey(javaType);
        } else if (ex instanceof ListValue) {
            return new JavaTransitiveInstancesKey(List.class);
        } else if (ex instanceof StringValue) {
            return new JavaTransitiveInstancesKey(String.class);
        } else if (ex instanceof EnumValue) {
            EnumValue reference = (EnumValue) ex;
            if (reference.getEnumeration() != null) {
                return typeSystem.classifierToInputKey(reference.getLiteral().getEEnum());
            }
        }
        return super.getDeclaredType(ex);
    }

    /**
     * @since 1.3
     */
    @Override
    public Set<IInputKey> getAllPossibleTypes(Expression var) {
        final Pattern containingPattern = EcoreUtil2.getContainerOfType(var, Pattern.class);
        TypeInformation information = collectConstraints(containingPattern);
        if (CorePatternLanguageHelper.isParameter(var)) {
            return ImmutableSet.of(information.getType((Variable) var));
        } else {
            return information.getAllTypes(var);
        }
    }

    private TypeInformation collectConstraints(final Pattern pattern) {
        final TypeInformation types = cache.get(this, pattern.eResource(), new Provider<TypeInformation>() {

            @Override
            public TypeInformation get() {
                return new TypeInformation(typeSystem);
            }
        });

        // XXX requiring an ordered call graph might be expensive, but it avoids inconsistent errors during type inference
        // The UNTYPED_PARAMETER_PREDICATE is used to return a reduced call graph where pattern with only declared types are (transitively) ignored.
        final Set<Pattern> patternsToCheck = CorePatternLanguageHelper.getReferencedPatternsTransitive(pattern, true, UNTYPED_PATTERN_PREDICATE);
        patternsToCheck.add(pattern);
        
        for (Pattern patternToCheck : patternsToCheck) {
            for (Pattern typedCall : Iterables.filter(CorePatternLanguageHelper.getReferencedPatterns(patternToCheck), TYPED_PATTERN_PREDICATE)) {
                // Ensure called parameter types are loaded
                rules.loadParameterVariableTypes(typedCall, types);
            }
            if (!types.isProcessed(patternToCheck)) {
                rules.inferTypes(patternToCheck, types);
                for (PatternBody body : patternToCheck.getBodies()) {
                    for (Iterator<EObject> it = body.eAllContents(); it.hasNext();) {
                        EObject obj = it.next();
                        if (obj instanceof Constraint || obj instanceof Expression) {
                            rules.inferTypes(obj, types);
                        }
                    }
                }
                types.setProcessed(patternToCheck);
            }

        }

        return types;
    }

}
