package headless.util;

import com.google.common.collect.Sets;
import headless.EPackageMatcher;
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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * A pattern-specific query specification that can instantiate EPackageMatcher in a type-safe way.
 * 
 * @see EPackageMatcher
 * @see EPackageMatch
 * 
 */
@SuppressWarnings("all")
public final class EPackageQuerySpecification extends BaseGeneratedQuerySpecification<EPackageMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static EPackageQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected EPackageMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return EPackageMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "headless.ePackage";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("p");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("p", "org.eclipse.emf.ecore.EPackage"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_p = body.getOrCreateVariableByName("p");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_p, "p")
      ));
      
      new TypeUnary(body, var_p, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage"), "http://www.eclipse.org/emf/2002/Ecore/EPackage");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("Item");
      annotation.addAttribute("item",new ParameterReference("p"));
      annotation.addAttribute("label","P: $p.name$");
      addAnnotation(annotation);
    }
    {
      PAnnotation annotation = new PAnnotation("Format");
      annotation.addAttribute("textColor","#ffffff");
      annotation.addAttribute("color","#791662");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private EPackageQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<EPackageQuerySpecification> {
    @Override
    public EPackageQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static EPackageQuerySpecification INSTANCE = make();
    
    public static EPackageQuerySpecification make() {
      try {
      	return new EPackageQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
