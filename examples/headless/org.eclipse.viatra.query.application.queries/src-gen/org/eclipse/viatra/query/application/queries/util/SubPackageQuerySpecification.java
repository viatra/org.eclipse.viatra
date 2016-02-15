package org.eclipse.viatra.query.application.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.SubPackageMatch;
import org.eclipse.viatra.query.application.queries.SubPackageMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate SubPackageMatcher in a type-safe way.
 * 
 * @see SubPackageMatcher
 * @see SubPackageMatch
 * 
 */
@SuppressWarnings("all")
public final class SubPackageQuerySpecification extends BaseGeneratedEMFQuerySpecification<SubPackageMatcher> {
  private SubPackageQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SubPackageQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected SubPackageMatcher instantiate(final ViatraQueryEngine engine) throws IncQueryException {
    return SubPackageMatcher.on(engine);
  }
  
  @Override
  public SubPackageMatch newEmptyMatch() {
    return SubPackageMatch.newEmptyMatch();
  }
  
  @Override
  public SubPackageMatch newMatch(final Object... parameters) {
    return SubPackageMatch.newMatch((org.eclipse.emf.ecore.EPackage) parameters[0], (org.eclipse.emf.ecore.EPackage) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link SubPackageQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link SubPackageQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static SubPackageQuerySpecification INSTANCE = new SubPackageQuerySpecification();
    
    /**
     * Statically initializes the query specification <b>after</b> the field {@link #INSTANCE} is assigned.
     * This initialization order is required to support indirect recursion.
     * 
     * <p> The static initializer is defined using a helper field to work around limitations of the code generator.
     * 
     */
    private final static Object STATIC_INITIALIZER = ensureInitialized();
    
    public static Object ensureInitialized() {
      INSTANCE.ensureInitializedInternalSneaky();
      return null;					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static SubPackageQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.application.queries.subPackage";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("p","sp");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("p", "org.eclipse.emf.ecore.EPackage"),new PParameter("sp", "org.eclipse.emf.ecore.EPackage"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_p = body.getOrCreateVariableByName("p");
      		PVariable var_sp = body.getOrCreateVariableByName("sp");
      		new TypeConstraint(body, new FlatTuple(var_p), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		new TypeConstraint(body, new FlatTuple(var_sp), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_p, "p"),
      		   new ExportedParameter(body, var_sp, "sp")
      		));
      		//  EPackage.eSubpackages(p,sp)
      		new TypeConstraint(body, new FlatTuple(var_p), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_p, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage", "eSubpackages")));
      		new Equality(body, var__virtual_0_, var_sp);
      		bodies.add(body);
      	}
      	                {
      		PAnnotation annotation = new PAnnotation("Edge");
      		annotation.addAttribute("source", new ParameterReference("p"));
      		annotation.addAttribute("label", "sub");
      		annotation.addAttribute("target", new ParameterReference("sp"));
      		addAnnotation(annotation);
      	}
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
