package org.eclipse.incquery.runtime.runonce.tests.util;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.rete.construction.psystem.IValueProvider;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A xbase xexpression evaluator tailored for the org.eclipse.incquery.runtime.runonce.tests.longSciFiBooksOfAuthor pattern.
 */
@SuppressWarnings("all")
public class LongSciFiBooksOfAuthorEvaluator1_1 implements IMatchChecker {
  private ImmutableList<String> parameterNames = ImmutableList.of(
    "pages"	
    );
    ;
  
  /**
   * The raw java code generated from the xbase xexpression by xtext.
   */
  private Boolean evaluateGeneratedExpression(final Integer pages) {
    boolean _greaterThan = ((pages).intValue() > 100);
    return Boolean.valueOf(_greaterThan);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  @Deprecated
  public Boolean evaluateXExpression(final Tuple tuple, final Map<String,Integer> tupleNameMap) {
    int pagesPosition = tupleNameMap.get("pages");
    java.lang.Integer pages = (java.lang.Integer) tuple.get(pagesPosition);
    return evaluateGeneratedExpression(pages);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Boolean evaluateExpression(final IValueProvider provider) {
    java.lang.Integer pages = (java.lang.Integer) provider.getValue("pages");
    return evaluateGeneratedExpression(pages);
    
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Iterable<String> getInputParameterNames() {
    return parameterNames;
    
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public String getShortDescription() {
    return "XExpression 1_1 from Pattern longSciFiBooksOfAuthor";
    
  }
}
