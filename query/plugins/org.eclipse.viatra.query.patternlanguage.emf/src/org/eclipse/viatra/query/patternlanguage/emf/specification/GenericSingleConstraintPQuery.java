/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.EPMToPBody;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.NameToSpecificationMap;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternBodyTransformer;
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.BasePQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameterDirection;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PVisibility;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.RewriterException;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmUnknownTypeReference;

import com.google.inject.Injector;

/**
 * This is a generic (i.e. not pattern-specific) class for the internal representation of VIATRA queries, for "interpretative" query execution. 
 * 
 * <p> End users should use {link GenericQuerySpecification} instead.
 *
 * @author Zoltan Ujhelyi
 * @noinstantiate This class is not intended to be instantiated by clients
 * @since 2.0
 */
public class GenericSingleConstraintPQuery extends BasePQuery {

    private final Pattern parentPattern;
    private final String queryName;
    private final List<PParameter> parameters;
    private final CallableRelation constraint;
    
    /**
     * Initializes a generic query representation for a given pattern. </p>
     * <p>
     * <strong>Warning</strong>: it is not recommended to directly instantiate GenericPQuery instances as
     * they will not reuse previously built specifications- use {@link SpecificationBuilder} instead.
     *
     * @param pattern
     *            the pattern for which the matcher is to be constructed.
     * @throws ViatraQueryRuntimeException
     */
    public GenericSingleConstraintPQuery(Pattern parentPattern, CallableRelation constraint, String queryName) {
        super(PVisibility.EMBEDDED);
        Preconditions.checkArgument(!(constraint instanceof PatternCall));
        this.parentPattern = parentPattern;
        this.constraint = constraint;
        this.queryName = queryName;
        this.parameters = Collections.unmodifiableList(initializeParameters(constraint));
        setBodies(doGetContainedBodies());
    }
    
    private List<PParameter> initializeParameters(CallableRelation constraint) {
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        ITypeInferrer typeInferrer = injector.getInstance(ITypeInferrer.class);
        EMFTypeSystem typeSystem = injector.getInstance(EMFTypeSystem.class);
        
        final LinkedHashMap<String, IInputKey> variableMap = PatternLanguageHelper.getParameterVariables(constraint, typeSystem, typeInferrer);
        return variableMap.entrySet().stream().map(e -> {
            String varName = e.getKey();
            
            JvmTypeReference type = typeSystem.toJvmTypeReference(e.getValue(), constraint);
            // bug 411866: JvmUnknownTypeReference.getType() can return null since Xtext 2.4
            String clazz = (type == null || type instanceof JvmUnknownTypeReference) ? "" : type.getType()
                    .getQualifiedName();
            
            return new PParameter(varName, clazz, e.getValue(), PParameterDirection.INOUT);
        }).collect(Collectors.toList());
    }
    
    public Pattern getPattern() {
        return parentPattern;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
                (obj instanceof GenericSingleConstraintPQuery &&
                 Objects.equals(getPattern(), ((GenericSingleConstraintPQuery)obj).getPattern()) &&
                 Objects.equals(  constraint, ((GenericSingleConstraintPQuery)obj).constraint));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPattern(), constraint);
    }

    @Override
    public String getFullyQualifiedName() {
        return PatternLanguageHelper.getFullyQualifiedName(getPattern()) + "$" + queryName;
    }

    @Override
    public List<PParameter> getParameters() {
        return parameters;
    }

    @Override
    protected Set<PBody> doGetContainedBodies() {
        try {
            EPMToPBody acceptor = new EPMToPBody(getPattern(), this, new NameToSpecificationMap());
            PatternBodyTransformer transformer = new PatternBodyTransformer(getPattern(), constraint, acceptor.createParameterMapping(constraint));
            PBody pBody = transformer.transform(constraint, acceptor);
            return Collections.singleton(pBody);
        } catch (RewriterException e) {
            addError(new PProblem(e, e.getShortMessage()));
            throw e;
        }
    }

}
