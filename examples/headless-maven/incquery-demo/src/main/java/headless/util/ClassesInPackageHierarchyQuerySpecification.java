package headless.util;

import com.google.common.collect.Sets;
import headless.ClassesInPackageHierarchyMatcher;
import headless.util.ClassesInPackageQuerySpecification;
import headless.util.SubPackageQuerySpecification;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate ClassesInPackageHierarchyMatcher in a type-safe way.
 * 
 * @see ClassesInPackageHierarchyMatcher
 * @see ClassesInPackageHierarchyMatch
 * 
 */
@SuppressWarnings("all")
public final class ClassesInPackageHierarchyQuerySpecification extends BaseGeneratedQuerySpecification<ClassesInPackageHierarchyMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ClassesInPackageHierarchyQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ClassesInPackageHierarchyMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ClassesInPackageHierarchyMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "headless.classesInPackageHierarchy";
    
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_rootP = body.getOrCreateVariableByName("rootP");
      PVariable var_containedClass = body.getOrCreateVariableByName("containedClass");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_rootP, "rootP"), 
        new ExportedParameter(body, var_containedClass, "containedClass")
      ));
      
      new TypeUnary(body, var_rootP, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage"), "http://www.eclipse.org/emf/2002/Ecore/EPackage");
      
      new TypeUnary(body, var_containedClass, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass"), "http://www.eclipse.org/emf/2002/Ecore/EClass");
      new PositivePatternCall(body, new FlatTuple(var_rootP, var_containedClass), ClassesInPackageQuerySpecification.instance());
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_rootP = body.getOrCreateVariableByName("rootP");
      PVariable var_containedClass = body.getOrCreateVariableByName("containedClass");
      PVariable var_somePackage = body.getOrCreateVariableByName("somePackage");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_rootP, "rootP"), 
        new ExportedParameter(body, var_containedClass, "containedClass")
      ));
      
      new TypeUnary(body, var_rootP, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage"), "http://www.eclipse.org/emf/2002/Ecore/EPackage");
      
      new TypeUnary(body, var_containedClass, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass"), "http://www.eclipse.org/emf/2002/Ecore/EClass");
      new BinaryTransitiveClosure(body, new FlatTuple(var_rootP, var_somePackage), SubPackageQuerySpecification.instance().instance());
      new PositivePatternCall(body, new FlatTuple(var_somePackage, var_containedClass), ClassesInPackageQuerySpecification.instance());
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("Edge");
      annotation.addAttribute("source",new ParameterReference("rootP"));
      annotation.addAttribute("target",new ParameterReference("containedClass"));
      annotation.addAttribute("label","classIn+");
      addAnnotation(annotation);
    }
    {
      PAnnotation annotation = new PAnnotation("Format");
      annotation.addAttribute("color","#0033ff");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private ClassesInPackageHierarchyQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ClassesInPackageHierarchyQuerySpecification> {
    @Override
    public ClassesInPackageHierarchyQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ClassesInPackageHierarchyQuerySpecification INSTANCE = make();
    
    public static ClassesInPackageHierarchyQuerySpecification make() {
      try {
      	return new ClassesInPackageHierarchyQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
