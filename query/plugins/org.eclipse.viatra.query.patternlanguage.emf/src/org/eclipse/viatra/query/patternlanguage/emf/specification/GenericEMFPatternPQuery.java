/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternBodyTransformer;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Parameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.InitializablePQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.BasePQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameterDirection;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.RewriterException;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmUnknownTypeReference;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * This is a generic (i.e. not pattern-specific) class for the internal representation of VIATRA queries, for "interpretative" query execution. 
 * 
 * <p> End users should use {link GenericQuerySpecification} instead.
 *
 * @author Bergmann Gábor
 * @noinstantiate This class is not intended to be instantiated by clients
 */
public class GenericEMFPatternPQuery extends BasePQuery implements InitializablePQuery {

    private Pattern pattern;
    private ImmutableList<PParameter> parameters;
	
    /**
     * Initializes a generic query representation for a given pattern. </p>
     * <p>
     * <strong>Warning</strong>: it is not recommended to directly instantiate GenericPQuery instances as
     * they will not reuse previously built specifications- use {@link SpecificationBuilder} instead.
     *
     * @param pattern
     *            the pattern for which the matcher is to be constructed.
     * @throws QueryInitializationException
     */
    public GenericEMFPatternPQuery(Pattern pattern) throws QueryInitializationException {
        this(pattern, false);
    }

    /**
     * Initializes a generic query specification for a given pattern.
     *
     * @param pattern
     *            the pattern for which matchers are to be constructed.
     * @param delayedInitialization
     *            true if the query is not created automatically - in this case before use the
     *            {@link #initializeBodies(Set) } method
     *
     * @throws QueryInitializationException
     */
    public GenericEMFPatternPQuery(Pattern pattern, boolean delayedInitialization) throws QueryInitializationException {
        super();
        this.pattern = pattern;
        if (delayedInitialization) {
            setStatus(PQueryStatus.UNINITIALIZED);
        } else {
            setBodies(doGetContainedBodies());
        }
    }
    
    public Pattern getPattern() {
        return pattern;
    }
    
    @Override
    public boolean equals(Object obj) {
    	return (obj == this) ||
    			(obj instanceof GenericEMFPatternPQuery &&
    					pattern.equals(((GenericEMFPatternPQuery)obj).pattern));
    }

    @Override
    public int hashCode() {
    	return pattern.hashCode();
    }

    @Override
    public String getFullyQualifiedName() {
        return CorePatternLanguageHelper.getFullyQualifiedName(getPattern());
    }

    @Override
    public List<PParameter> getParameters() {
        if (parameters == null) {
            parameters = ImmutableList.copyOf(Iterables.transform(pattern.getParameters(), new Function<Variable, PParameter>() {
    
                @Override
                public PParameter apply(Variable var) {
                    if (var == null) {
                        return new PParameter("", "");
                    } else {
                        PParameterDirection direction = PParameterDirection.INOUT;
                        if (var instanceof Parameter){
                            switch(((Parameter) var).getDirection()){
                            case IN:
                                direction = PParameterDirection.IN;
                                break;
                            case OUT:
                                direction = PParameterDirection.OUT;
                                break;
                            case INOUT:
                            default:
                                break;
                            
                            }
                        }
                        ITypeInferrer typeProvider = XtextInjectorProvider.INSTANCE.getInjector().getInstance(ITypeInferrer.class);
                        JvmTypeReference ref = typeProvider.getJvmType(var, var);
                        // bug 411866: JvmUnknownTypeReference.getType() returns null in Xtext 2.4
                        String clazz = (ref == null || ref instanceof JvmUnknownTypeReference) ? "" : ref.getType()
                                .getQualifiedName();
                        
                        IInputKey unaryDeclaredType = null; 
                        Type type = var.getType();
    					if (type instanceof ClassType) 
                        	unaryDeclaredType = PatternBodyTransformer.classifierToInputKey(((ClassType) type).getClassname());
                        
    					return new PParameter(var.getName(), clazz, unaryDeclaredType, direction);
                    }
                }
    
            }));
        }
        return parameters;
    }

    @Override
    public PDisjunction getDisjunctBodies() {
        Preconditions.checkState(!isMutable(), "Query %s is not initialized.", getFullyQualifiedName());
        Preconditions.checkState(!getStatus().equals(PQueryStatus.ERROR), "Query %s contains errors.", getFullyQualifiedName());
        return super.getDisjunctBodies();
    }
    
    @Override
    public void addAnnotation(PAnnotation annotation) {
        // Making the upper-level construct visible
        super.addAnnotation(annotation);
    }

    @Override
    protected Set<PBody> doGetContainedBodies() throws QueryInitializationException {
        SpecificationBuilder specificationBuilder = new SpecificationBuilder();
        try {
			return specificationBuilder.getBodies(pattern, this);
		} catch (RewriterException e) {
            addError(new PProblem(e, e.getShortMessage()));
            throw e;
		}
    }
	
    /**
     * Sets up the bodies stored inside this query specification. Only available for uninitialized specifications.
     * @param bodies a non-empty set of {@link PBody} instances
     */
    @Override
    public void initializeBodies(Set<PBody> bodies) throws QueryInitializationException {
        Preconditions.checkState(isMutable(), "The bodies can only be set for uninitialized queries.");
        if (bodies.isEmpty()) {
            addError(new PProblem("No bodies specified for query"));
        } else {
            setBodies(bodies);
        }
    }
    
    @Override
	public final void setStatus(PQueryStatus newStatus) {
        Preconditions.checkState(isMutable(), "The status of the specification can only be set for uninitialized queries.");
        super.setStatus(newStatus);
    }
	@Override
	public void addError(PProblem problem) {
        Preconditions.checkState(
                isMutable() || getStatus().equals(PQueryStatus.ERROR), 
        		"Errors can only be added to unitialized or erroneous queries.");
        super.addError(problem);
	}


}
