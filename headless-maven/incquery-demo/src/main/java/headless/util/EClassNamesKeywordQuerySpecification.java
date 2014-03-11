package headless.util;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import headless.EClassNamesKeywordMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;

/**
 * A pattern-specific query specification that can instantiate EClassNamesKeywordMatcher in a type-safe way.
 * 
 * @see EClassNamesKeywordMatcher
 * @see EClassNamesKeywordMatch
 * 
 */
@SuppressWarnings("all")
public final class EClassNamesKeywordQuerySpecification extends BaseGeneratedQuerySpecification<EClassNamesKeywordMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static EClassNamesKeywordQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected EClassNamesKeywordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return EClassNamesKeywordMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "headless.eClassNamesKeyword";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("c","n");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("c", "org.eclipse.emf.ecore.EClass"),new PParameter("n", "java.lang.String"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_c = body.getOrCreateVariableByName("c");
      PVariable var_n = body.getOrCreateVariableByName("n");
      body.setExportedParameters(Arrays.asList(
        new ExportedParameter(body, var_c, "c"), 
        new ExportedParameter(body, var_n, "n")
      ));
      
      
      new TypeUnary(body, var_c, getClassifierLiteral("http://www.eclipse.org/emf/2002/Ecore", "EClass"), "http://www.eclipse.org/emf/2002/Ecore/EClass");
      new TypeBinary(body, context, var_c, var_n, getFeatureLiteral("http://www.eclipse.org/emf/2002/Ecore", "ENamedElement", "name"), "http://www.eclipse.org/emf/2002/Ecore/ENamedElement.name");
      new ExpressionEvaluation(body, new IExpressionEvaluator() {
        @Override
        public String getShortDescription() {
        	return "Expression evaluation from pattern eClassNamesKeyword";
        }
        
        @Override
        public Iterable<String> getInputParameterNames() {
        	return Arrays.asList("n");
        }
        
        @Override
        public Object evaluateExpression(IValueProvider provider) throws Exception {
        	java.lang.String n = (java.lang.String) provider.getValue("n");
        	return evaluateExpression_1_1(n);
        }
        
        },  null); 
      bodies.add(body);
    }setStatus(PQueryStatus.OK);
    return bodies;
  }
  
  private EClassNamesKeywordQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<EClassNamesKeywordQuerySpecification> {
    @Override
    public EClassNamesKeywordQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static EClassNamesKeywordQuerySpecification INSTANCE = make();
    
    public static EClassNamesKeywordQuerySpecification make() {
      try {
      	return new EClassNamesKeywordQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
  
  private boolean evaluateExpression_1_1(final String n) {
    boolean _equals = Objects.equal("A", n);
    return _equals;
  }
}
