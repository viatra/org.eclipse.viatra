package org.eclipse.incquery.runtime.runonce.tests.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatch;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.util.BookAuthorsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate SomeBooksWithTwoAuthorsMatcher in a type-safe way.
 * 
 * @see SomeBooksWithTwoAuthorsMatcher
 * @see SomeBooksWithTwoAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class SomeBooksWithTwoAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<SomeBooksWithTwoAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SomeBooksWithTwoAuthorsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected SomeBooksWithTwoAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SomeBooksWithTwoAuthorsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.someBooksWithTwoAuthors";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("library","book");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("library", "org.eclipse.incquery.examples.eiqlibrary.Library"),new PParameter("book", "org.eclipse.incquery.examples.eiqlibrary.Book"));
  }
  
  @Override
  public SomeBooksWithTwoAuthorsMatch newEmptyMatch() {
    return SomeBooksWithTwoAuthorsMatch.newEmptyMatch();
  }
  
  @Override
  public SomeBooksWithTwoAuthorsMatch newMatch(final Object... parameters) {
    return SomeBooksWithTwoAuthorsMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) parameters[0], (org.eclipse.incquery.examples.eiqlibrary.Book) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_library = body.getOrCreateVariableByName("library");
      PVariable var_book = body.getOrCreateVariableByName("book");
      PVariable var__c = body.getOrCreateVariableByName("_c");
      PVariable var__virtual_3_ = body.getOrCreateVariableByName(".virtual{3}");
      PVariable var__author = body.getOrCreateVariableByName("_author");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_library, "library"), 
        new ExportedParameter(body, var_book, "book")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_library, var__c, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Library", "requestCount"), "http:///org/incquery/examples/library/1.0/Library.requestCount");
      new TypeBinary(body, CONTEXT, var_library, var_book, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Library", "someBooks"), "http:///org/incquery/examples/library/1.0/Library.someBooks");
      new ConstantValue(body, var__virtual_3_, 2);
      new PatternMatchCounter(body, new FlatTuple(var_book, var__author), BookAuthorsQuerySpecification.instance().instance(), var__virtual_3_);
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SomeBooksWithTwoAuthorsQuerySpecification INSTANCE = make();
    
    public static SomeBooksWithTwoAuthorsQuerySpecification make() {
      return new SomeBooksWithTwoAuthorsQuerySpecification();					
      
    }
  }
  
}
