package org.eclipse.viatra.query.runtime.runonce.tests.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.runonce.tests.BookAuthorsMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.BookAuthorsMatcher;

/**
 * A pattern-specific query specification that can instantiate BookAuthorsMatcher in a type-safe way.
 * 
 * @see BookAuthorsMatcher
 * @see BookAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BookAuthorsQuerySpecification extends BaseGeneratedEMFQuerySpecification<BookAuthorsMatcher> {
  private BookAuthorsQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static BookAuthorsQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected BookAuthorsMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return BookAuthorsMatcher.on(engine);
  }
  
  @Override
  public BookAuthorsMatch newEmptyMatch() {
    return BookAuthorsMatch.newEmptyMatch();
  }
  
  @Override
  public BookAuthorsMatch newMatch(final Object... parameters) {
    return BookAuthorsMatch.newMatch((org.eclipse.viatra.examples.library.Book) parameters[0], (org.eclipse.viatra.examples.library.Writer) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link BookAuthorsQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link BookAuthorsQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static BookAuthorsQuerySpecification INSTANCE = new BookAuthorsQuerySpecification();
    
    /**
     * Statically initializes the query specification <b>after</b> the field {@link #INSTANCE} is assigned.
     * This initialization order is required to support indirect recursion.
     * 
     * <p> The static initializer is defined using a helper field to work around limitations of the code generator.
     * 
     */
    private final static Object STATIC_INITIALIZER = ensureInitialized();
    
    public static Object ensureInitialized() {
      INSTANCE.ensureInitializedInternalSneaky();
      return null;					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static BookAuthorsQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.bookAuthors";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("book","author");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("book", "org.eclipse.viatra.examples.library.Book"),new PParameter("author", "org.eclipse.viatra.examples.library.Writer"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_book = body.getOrCreateVariableByName("book");
      		PVariable var_author = body.getOrCreateVariableByName("author");
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_book, "book"),
      		   new ExportedParameter(body, var_author, "author")
      		));
      		// 	Book.authors(book, author)
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_book, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book", "authors")));
      		new Equality(body, var__virtual_0_, var_author);
      		bodies.add(body);
      	}
      	// to silence compiler error
      	if (false) throw new ViatraQueryException("Never", "happens");
      } catch (ViatraQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
