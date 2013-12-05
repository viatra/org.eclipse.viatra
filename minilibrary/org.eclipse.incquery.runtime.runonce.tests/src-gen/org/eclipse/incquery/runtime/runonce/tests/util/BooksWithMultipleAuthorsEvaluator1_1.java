package org.eclipse.incquery.runtime.runonce.tests.util;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.rete.construction.psystem.IValueProvider;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A xbase xexpression evaluator tailored for the org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors pattern.
 */
@SuppressWarnings("all")
public class BooksWithMultipleAuthorsEvaluator1_1 implements IMatchChecker {
  private ImmutableList<String> parameterNames = ImmutableList.of(
    "numberOfBooks"	
    );
    ;
  
  /**
   * The raw java code generated from the xbase xexpression by xtext.
   */
  private Boolean evaluateGeneratedExpression(final Integer numberOfBooks) {
    boolean _greaterThan = ((numberOfBooks).intValue() > 1);
    return Boolean.valueOf(_greaterThan);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  @Deprecated
  public Boolean evaluateXExpression(final Tuple tuple, final Map<String,Integer> tupleNameMap) {
    int numberOfBooksPosition = tupleNameMap.get("numberOfBooks");
    java.lang.Integer numberOfBooks = (java.lang.Integer) tuple.get(numberOfBooksPosition);
    return evaluateGeneratedExpression(numberOfBooks);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Boolean evaluateExpression(final IValueProvider provider) {
    java.lang.Integer numberOfBooks = (java.lang.Integer) provider.getValue("numberOfBooks");
    return evaluateGeneratedExpression(numberOfBooks);
    
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
    return "XExpression 1_1 from Pattern booksWithMultipleAuthors";
    
  }
}
