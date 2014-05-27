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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatch;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatcher;

/**
 * A pattern-specific query specification that can instantiate BookAuthorsMatcher in a type-safe way.
 * 
 * @see BookAuthorsMatcher
 * @see BookAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BookAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<BookAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static BookAuthorsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected BookAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return BookAuthorsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.bookAuthors";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("book","author");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("book", "org.eclipse.incquery.examples.eiqlibrary.Book"),new PParameter("author", "org.eclipse.incquery.examples.eiqlibrary.Writer"));
  }
  
  @Override
  public BookAuthorsMatch newEmptyMatch() {
    return BookAuthorsMatch.newEmptyMatch();
  }
  
  @Override
  public BookAuthorsMatch newMatch(final Object... parameters) {
    return BookAuthorsMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Book) parameters[0], (org.eclipse.incquery.examples.eiqlibrary.Writer) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_book = body.getOrCreateVariableByName("book");
      PVariable var_author = body.getOrCreateVariableByName("author");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_book, "book"), 
        new ExportedParameter(body, var_author, "author")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_book, var_author, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Book", "authors"), "http:///org/incquery/examples/library/1.0/Book.authors");
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static BookAuthorsQuerySpecification INSTANCE = make();
    
    public static BookAuthorsQuerySpecification make() {
      return new BookAuthorsQuerySpecification();					
      
    }
  }
  
}
