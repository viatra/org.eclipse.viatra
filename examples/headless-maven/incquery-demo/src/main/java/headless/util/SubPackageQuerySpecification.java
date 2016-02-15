package headless.util;

import com.google.common.collect.Sets;
import headless.SubPackageMatcher;
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

/**
 * A pattern-specific query specification that can instantiate SubPackageMatcher in a type-safe way.
 * 
 * @see SubPackageMatcher
 * @see SubPackageMatch
 * 
 */
@SuppressWarnings("all")
public final class SubPackageQuerySpecification extends BaseGeneratedQuerySpecification<SubPackageMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SubPackageQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SubPackageMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SubPackageMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "headless.subPackage";
    
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_p = body.getOrCreateVariableByName("p");
      PVariable var_sp = body.getOrCreateVariableByName("sp");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_p, "p"), 
        new ExportedParameter(body, var_sp, "sp")
      ));
      
      
      new TypeBinary(body, context, var_p, var_sp, getFeatureLiteral("http://www.eclipse.org/emf/2002/Ecore", "EPackage", "eSubpackages"), "http://www.eclipse.org/emf/2002/Ecore/EPackage.eSubpackages");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("Edge");
      annotation.addAttribute("source",new ParameterReference("p"));
      annotation.addAttribute("target",new ParameterReference("sp"));
      annotation.addAttribute("label","sub");
      addAnnotation(annotation);
    }
    setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private SubPackageQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<SubPackageQuerySpecification> {
    @Override
    public SubPackageQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SubPackageQuerySpecification INSTANCE = make();
    
    public static SubPackageQuerySpecification make() {
      try {
      	return new SubPackageQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
