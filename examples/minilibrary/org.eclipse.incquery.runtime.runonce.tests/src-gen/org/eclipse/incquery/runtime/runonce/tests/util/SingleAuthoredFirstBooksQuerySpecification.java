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
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatch;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.incquery.runtime.runonce.tests.util.BookAuthorsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate SingleAuthoredFirstBooksMatcher in a type-safe way.
 * 
 * @see SingleAuthoredFirstBooksMatcher
 * @see SingleAuthoredFirstBooksMatch
 * 
 */
@SuppressWarnings("all")
public final class SingleAuthoredFirstBooksQuerySpecification extends BaseGeneratedQuerySpecification<SingleAuthoredFirstBooksMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SingleAuthoredFirstBooksQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.singleAuthoredFirstBooks";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("library","firstBook");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("library", "org.eclipse.incquery.examples.eiqlibrary.Library"),new PParameter("firstBook", "org.eclipse.incquery.examples.eiqlibrary.Book"));
  }
  
  @Override
  public SingleAuthoredFirstBooksMatch newEmptyMatch() {
    return SingleAuthoredFirstBooksMatch.newEmptyMatch();
  }
  
  @Override
  public SingleAuthoredFirstBooksMatch newMatch(final Object... parameters) {
    return SingleAuthoredFirstBooksMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) parameters[0], (org.eclipse.incquery.examples.eiqlibrary.Book) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_library = body.getOrCreateVariableByName("library");
      PVariable var_firstBook = body.getOrCreateVariableByName("firstBook");
      PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      PVariable var__virtual_3_ = body.getOrCreateVariableByName(".virtual{3}");
      PVariable var__author = body.getOrCreateVariableByName("_author");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_library, "library"), 
        new ExportedParameter(body, var_firstBook, "firstBook")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_library, var__virtual_0_, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Library", "writers"), "http:///org/incquery/examples/library/1.0/Library.writers");
      new TypeBinary(body, CONTEXT, var__virtual_0_, var_firstBook, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Writer", "firstBook"), "http:///org/incquery/examples/library/1.0/Writer.firstBook");
      new ConstantValue(body, var__virtual_3_, 1);
      new PatternMatchCounter(body, new FlatTuple(var_firstBook, var__author), BookAuthorsQuerySpecification.instance().instance(), var__virtual_3_);
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SingleAuthoredFirstBooksQuerySpecification INSTANCE = make();
    
    public static SingleAuthoredFirstBooksQuerySpecification make() {
      return new SingleAuthoredFirstBooksQuerySpecification();					
      
    }
  }
  
}
