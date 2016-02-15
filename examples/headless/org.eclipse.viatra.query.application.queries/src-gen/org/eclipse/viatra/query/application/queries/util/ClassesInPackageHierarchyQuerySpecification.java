package org.eclipse.viatra.query.application.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.ClassesInPackageHierarchyMatch;
import org.eclipse.viatra.query.application.queries.ClassesInPackageHierarchyMatcher;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageQuerySpecification;
import org.eclipse.viatra.query.application.queries.util.SubPackageQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate ClassesInPackageHierarchyMatcher in a type-safe way.
 * 
 * @see ClassesInPackageHierarchyMatcher
 * @see ClassesInPackageHierarchyMatch
 * 
 */
@SuppressWarnings("all")
public final class ClassesInPackageHierarchyQuerySpecification extends BaseGeneratedEMFQuerySpecification<ClassesInPackageHierarchyMatcher> {
  private ClassesInPackageHierarchyQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ClassesInPackageHierarchyQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ClassesInPackageHierarchyMatcher instantiate(final ViatraQueryEngine engine) throws IncQueryException {
    return ClassesInPackageHierarchyMatcher.on(engine);
  }
  
  @Override
  public ClassesInPackageHierarchyMatch newEmptyMatch() {
    return ClassesInPackageHierarchyMatch.newEmptyMatch();
  }
  
  @Override
  public ClassesInPackageHierarchyMatch newMatch(final Object... parameters) {
    return ClassesInPackageHierarchyMatch.newMatch((org.eclipse.emf.ecore.EPackage) parameters[0], (org.eclipse.emf.ecore.EClass) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link ClassesInPackageHierarchyQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link ClassesInPackageHierarchyQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static ClassesInPackageHierarchyQuerySpecification INSTANCE = new ClassesInPackageHierarchyQuerySpecification();
    
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
    private final static ClassesInPackageHierarchyQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.application.queries.classesInPackageHierarchy";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("rootP","containedClass");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("rootP", "org.eclipse.emf.ecore.EPackage"),new PParameter("containedClass", "org.eclipse.emf.ecore.EClass"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_rootP = body.getOrCreateVariableByName("rootP");
      		PVariable var_containedClass = body.getOrCreateVariableByName("containedClass");
      		new TypeConstraint(body, new FlatTuple(var_rootP), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		new TypeConstraint(body, new FlatTuple(var_containedClass), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_rootP, "rootP"),
      		   new ExportedParameter(body, var_containedClass, "containedClass")
      		));
      		// 	find classesInPackage(rootP,containedClass)
      		new PositivePatternCall(body, new FlatTuple(var_rootP, var_containedClass), ClassesInPackageQuerySpecification.instance().getInternalQueryRepresentation());
      		bodies.add(body);
      	}
      	{
      		PBody body = new PBody(this);
      		PVariable var_rootP = body.getOrCreateVariableByName("rootP");
      		PVariable var_containedClass = body.getOrCreateVariableByName("containedClass");
      		PVariable var_somePackage = body.getOrCreateVariableByName("somePackage");
      		new TypeConstraint(body, new FlatTuple(var_rootP), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage")));
      		new TypeConstraint(body, new FlatTuple(var_containedClass), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_rootP, "rootP"),
      		   new ExportedParameter(body, var_containedClass, "containedClass")
      		));
      		// 	find subPackage+(rootP,somePackage)
      		new BinaryTransitiveClosure(body, new FlatTuple(var_rootP, var_somePackage), SubPackageQuerySpecification.instance().getInternalQueryRepresentation());
      		// 	find classesInPackage(somePackage,containedClass)
      		new PositivePatternCall(body, new FlatTuple(var_somePackage, var_containedClass), ClassesInPackageQuerySpecification.instance().getInternalQueryRepresentation());
      		bodies.add(body);
      	}
      	                {
      		PAnnotation annotation = new PAnnotation("Edge");
      		annotation.addAttribute("source", new ParameterReference("rootP"));
      		annotation.addAttribute("label", "classIn+");
      		annotation.addAttribute("target", new ParameterReference("containedClass"));
      		addAnnotation(annotation);
      	}
      	                {
      		PAnnotation annotation = new PAnnotation("Format");
      		annotation.addAttribute("color", "#0033ff");
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
