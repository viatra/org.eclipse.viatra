package org.eclipse.viatra.query.application.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.ClassesInPackageMatch;
import org.eclipse.viatra.query.application.queries.ClassesInPackageMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
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
 * A pattern-specific query specification that can instantiate ClassesInPackageMatcher in a type-safe way.
 * 
 * @see ClassesInPackageMatcher
 * @see ClassesInPackageMatch
 * 
 */
@SuppressWarnings("all")
public final class ClassesInPackageQuerySpecification extends BaseGeneratedEMFQuerySpecification<ClassesInPackageMatcher> {
  private ClassesInPackageQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static ClassesInPackageQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ClassesInPackageMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return ClassesInPackageMatcher.on(engine);
  }
  
  @Override
  public ClassesInPackageMatch newEmptyMatch() {
    return ClassesInPackageMatch.newEmptyMatch();
  }
  
  @Override
  public ClassesInPackageMatch newMatch(final Object... parameters) {
    return ClassesInPackageMatch.newMatch((org.eclipse.emf.ecore.EPackage) parameters[0], (org.eclipse.emf.ecore.EClass) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link ClassesInPackageQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link ClassesInPackageQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static ClassesInPackageQuerySpecification INSTANCE = new ClassesInPackageQuerySpecification();
    
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
    private final static ClassesInPackageQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.application.queries.classesInPackage";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("p","ec");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("p", "org.eclipse.emf.ecore.EPackage"),new PParameter("ec", "org.eclipse.emf.ecore.EClass"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_p = body.getOrCreateVariableByName("p");
      		PVariable var_ec = body.getOrCreateVariableByName("ec");
      		new TypeConstraint(body, new FlatTuple(var_p), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		new TypeConstraint(body, new FlatTuple(var_ec), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_p, "p"),
      		   new ExportedParameter(body, var_ec, "ec")
      		));
      		//  EPackage.eClassifiers(p,ec)
      		new TypeConstraint(body, new FlatTuple(var_p), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_p, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage", "eClassifiers")));
      		new Equality(body, var__virtual_0_, var_ec);
      		bodies.add(body);
      	}
      	                {
      		PAnnotation annotation = new PAnnotation("Edge");
      		annotation.addAttribute("source", new ParameterReference("p"));
      		annotation.addAttribute("label", "classIn");
      		annotation.addAttribute("target", new ParameterReference("ec"));
      		addAnnotation(annotation);
      	}
      	// to silence compiler error
      	if (false) throw new ViatraQueryException("Never", "happens");
      } catch (ViatraQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
