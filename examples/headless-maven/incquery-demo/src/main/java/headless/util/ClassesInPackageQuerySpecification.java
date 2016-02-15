package headless.util;

import com.google.common.collect.Sets;
import headless.ClassesInPackageMatcher;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * A pattern-specific query specification that can instantiate ClassesInPackageMatcher in a type-safe way.
 * 
 * @see ClassesInPackageMatcher
 * @see ClassesInPackageMatch
 * 
 */
@SuppressWarnings("all")
public final class ClassesInPackageQuerySpecification extends BaseGeneratedQuerySpecification<ClassesInPackageMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ClassesInPackageQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ClassesInPackageMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ClassesInPackageMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "headless.classesInPackage";
    
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_p = body.getOrCreateVariableByName("p");
      PVariable var_ec = body.getOrCreateVariableByName("ec");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_p, "p"), 
        new ExportedParameter(body, var_ec, "ec")
      ));
      
      
      new TypeUnary(body, var_ec, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass"), "http://www.eclipse.org/emf/2002/Ecore/EClass");
      new TypeBinary(body, context, var_p, var_ec, getFeatureLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage", "eClassifiers"), "http://www.eclipse.org/emf/2002/Ecore/EPackage.eClassifiers");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("Edge");
      annotation.addAttribute("source",new ParameterReference("p"));
      annotation.addAttribute("target",new ParameterReference("ec"));
      annotation.addAttribute("label","classIn");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private ClassesInPackageQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ClassesInPackageQuerySpecification> {
    @Override
    public ClassesInPackageQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ClassesInPackageQuerySpecification INSTANCE = make();
    
    public static ClassesInPackageQuerySpecification make() {
      try {
      	return new ClassesInPackageQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
