/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.runonce.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.BookCategory;
import org.eclipse.incquery.examples.eiqlibrary.EIQLibraryFactory;
import org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.examples.eiqlibrary.Writer;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.impl.RunOnceQueryEngine;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.junit.Test;

/**
 * Test cases that run different kind of derived features in run-once engine.
 * 
 * @author Abel Hegedus
 *
 */
public class RunOnceTest {
    
    private ResourceSet rs;
    
    /**
     * Prepares a resource set and loads the example model.
     * 
     * @return the library that is the root of the example model 
     */
    private Library prepareModel() {
        String modelPath = "/org.eclipse.incquery.runtime.runonce.tests/model/test.eiqlibrary";
        rs = new ResourceSetImpl();
        URI modelUri = URI.createPlatformPluginURI(modelPath, true);
        Resource resource = rs.getResource(modelUri, true);
        return (Library) resource.getContents().get(0);
    }

    /**
     * Test whether a run-once engine returns matches for regular queries.
     */
    @Test
    public void testRegularQuery() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<BooksWithMultipleAuthorsMatch> allMatches = engine.getAllMatches(BooksWithMultipleAuthorsMatcher.querySpecification());
            assertTrue(allMatches.size() == 2);
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    /**
     * Shows an example of using generic queries in a run-once engine.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGenericQuery() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            IQuerySpecification<IncQueryMatcher<IPatternMatch>> specification = (IQuerySpecification<IncQueryMatcher<IPatternMatch>>) QuerySpecificationRegistry.getOrCreateQuerySpecification(BooksWithMultipleAuthorsMatcher.querySpecification().getPattern());
            Collection<IPatternMatch> allMatches = engine.getAllMatches(specification);
            assertTrue(allMatches.size() == 2);
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }

    @Test
    public void testSimpleAttribute() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<SumOfPagesInLibraryMatch> allMatches = engine.getAllMatches(SumOfPagesInLibraryMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SumOfPagesInLibraryMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getSumOfPages() == 222);
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    @Test
    public void testSingleReference() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<SingleAuthoredFirstBooksMatch> allMatches = engine.getAllMatches(SingleAuthoredFirstBooksMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SingleAuthoredFirstBooksMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getFirstBook().getTitle().equals("Other SciFi"));
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    @Test
    public void testManyReference() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<LongSciFiBooksOfAuthorMatch> allMatches = engine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
            assertTrue(match.getAuthor().getName().equals("Third Author"));
            assertTrue(match.getBook().getTitle().equals("Other SciFi"));
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    /**
     * This test uses a derived feature that returns different values on subsequent requests.
     * We could use this to test how the engine responds in such cases (e.g. during disposal).
     */
    @Test
    public void testNonDeterministicAttribute() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<RequestCountOfLibraryMatch> allMatches = engine.getAllMatches(RequestCountOfLibraryMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            RequestCountOfLibraryMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getReqCount() == 2);
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    /**
     * Similar to {@link #testNonDeterministicAttribute()} but with a many reference.
     */
    @Test
    public void testNonDeterministicFeature() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<SomeBooksWithTwoAuthorsMatch> allMatches = engine.getAllMatches(SomeBooksWithTwoAuthorsMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SomeBooksWithTwoAuthorsMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getBook().getTitle().equals("Twin life"));

            allMatches = engine.getAllMatches(SomeBooksWithTwoAuthorsMatcher.querySpecification());
            assertTrue(allMatches.isEmpty());
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    /**
     * The test shows that using an incremental engine with not well-behaving derived features will return 
     * incorrect values if the model changes.
     */
    @Test
    public void testModelModification() {
        // the results of incremental engine will not be correct
        Library library = prepareModel();
        
        try {
            AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(rs);
            // this is to allow the normal engine to traverse feature
            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            LongSciFiBooksOfAuthorMatcher matcher = engine.getMatcher(LongSciFiBooksOfAuthorMatcher.querySpecification());
            Collection<LongSciFiBooksOfAuthorMatch> allMatches = matcher.getAllMatches();
            
            RunOnceQueryEngine roengine = new RunOnceQueryEngine(rs);
            Collection<LongSciFiBooksOfAuthorMatch> allROMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            
            assertTrue(allMatches.size() == allROMatches.size());
            LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
            LongSciFiBooksOfAuthorMatch romatch = allROMatches.iterator().next();
            assertTrue(match.getAuthor() == romatch.getAuthor());
            Book longBook = romatch.getBook();
            assertTrue(match.getBook() == longBook);

            Book b = EIQLibraryFactory.eINSTANCE.createBook();
            b.setTitle("Long book");
            b.setPages(120);
            b.getCategory().add(BookCategory.SCI_FI);
            b.getAuthors().add(library.getWriters().get(0));
            library.getBooks().add(b);
            
            allROMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            allMatches = matcher.getAllMatches();
            assertTrue(allMatches.size() != allROMatches.size());
            assertTrue(allROMatches.size() == 2);
            
            Set<Book> longScifiBooks = new HashSet<Book>();
            for (LongSciFiBooksOfAuthorMatch m : allROMatches) {
                longScifiBooks.add(m.getBook());
            }
            assertTrue(longScifiBooks.contains(b));
            assertTrue(longScifiBooks.contains(longBook));
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }

    @Test
    public void testSamplingModelModification() {
     // the results of incremental engine will be correct after resampling
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine roengine = new RunOnceQueryEngine(rs);
            AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(rs,roengine.getBaseIndexOptions());
            // TODO remove this once such EClasses are automatically identified
            //engine.getBaseIndex().registerEClasses(Sets.newHashSet(EIQLibraryPackage.eINSTANCE.getBook(), EIQLibraryPackage.eINSTANCE.getWriter()));
            runModelModification(library, roengine, engine);
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }
    
    @Test
    public void testSamplingModelModificationInWildCardMode() {
        // the results of incremental engine will be correct after resampling
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine roengine = new RunOnceQueryEngine(rs);
            BaseIndexOptions baseIndexOptions = roengine.getBaseIndexOptions();
            baseIndexOptions.setWildcardMode(true); // ensure all types are indexed
            AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(rs,baseIndexOptions);
            runModelModification(library, roengine, engine);
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    }

    private void runModelModification(Library library, RunOnceQueryEngine roengine, AdvancedIncQueryEngine engine)
            throws IncQueryException {
        LongSciFiBooksOfAuthorMatcher matcher = engine.getMatcher(LongSciFiBooksOfAuthorMatcher.querySpecification());
        Collection<LongSciFiBooksOfAuthorMatch> allMatches = matcher.getAllMatches();
        
        Collection<LongSciFiBooksOfAuthorMatch> allROMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
        
        assertTrue(allMatches.size() == allROMatches.size());
        LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
        LongSciFiBooksOfAuthorMatch romatch = allROMatches.iterator().next();
        assertTrue(match.getAuthor() == romatch.getAuthor());
        Book longBook = romatch.getBook();
        assertTrue(match.getBook() == longBook);

        Book b = EIQLibraryFactory.eINSTANCE.createBook();
        b.setTitle("Long book");
        b.setPages(120);
        b.getCategory().add(BookCategory.SCI_FI);
        b.getAuthors().add(library.getWriters().get(0));
        library.getBooks().add(b);
        
        allROMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
        allMatches = matcher.getAllMatches();
        assertTrue(allMatches.size() != allROMatches.size());

        // manually resample values
        engine.getBaseIndex().resampleDerivedFeatures();
        
        allMatches = matcher.getAllMatches();
        assertTrue(allMatches.size() == allROMatches.size());
        assertTrue(allROMatches.size() == 2);
        
        Set<Book> longScifiBooks = new HashSet<Book>();
        for (LongSciFiBooksOfAuthorMatch m : allROMatches) {
            longScifiBooks.add(m.getBook());
        }
        assertTrue(longScifiBooks.contains(b));
        assertTrue(longScifiBooks.contains(longBook));
    }
    
    @Test
    public void testSamplingModelModificationChangeVisitor() {
     // the results of incremental engine will be correct after resampling
        String address = "test";
        Library library = createLibrary(address);
        String title = "test";
        Book book = createBook(library, title, 150, BookCategory.SCI_FI);
        
        try {
            RunOnceQueryEngine roengine = new RunOnceQueryEngine(library);
            AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(library,roengine.getBaseIndexOptions());

            LongSciFiBooksOfAuthorMatcher matcher = engine.getMatcher(LongSciFiBooksOfAuthorMatcher.querySpecification());
            Collection<LongSciFiBooksOfAuthorMatch> allMatches = matcher.getAllMatches();
            assertTrue(allMatches.isEmpty());
            
            Writer writer = createWriter(library, "test");
            book.getAuthors().add(writer);
            
            engine.getBaseIndex().resampleDerivedFeatures();
            
            allMatches = matcher.getAllMatches();
            assertTrue(!allMatches.isEmpty());
            LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
            assertTrue(match.getAuthor().equals(writer));
            assertTrue(match.getBook().equals(book));
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
    
    }
    
    @Test
    public void testAutomaticSamplingModelModification() {
        // the results of run-once engine will be correct after automatic resampling
        String address = "test";
        Library library = createLibrary(address);
        String title = "test";
        Book book = createBook(library, title, 150, BookCategory.SCI_FI);
        
        try {
            RunOnceQueryEngine roengine = new RunOnceQueryEngine(library);
            roengine.setAutomaticResampling(true);
            Collection<LongSciFiBooksOfAuthorMatch> allMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            assertTrue(allMatches.isEmpty());
            
            Writer writer = createWriter(library, "test");
            book.getAuthors().add(writer);
            
            allMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            assertTrue(!allMatches.isEmpty());
            LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
            assertTrue(match.getAuthor().equals(writer));
            assertTrue(match.getBook().equals(book));
            
            roengine.setAutomaticResampling(false);
            
            Writer writer2 = createWriter(library, "test2");
            allMatches = roengine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            assertTrue(!allMatches.isEmpty());
            
        } catch (IncQueryException e) {
            e.printStackTrace();
            fail(e.getShortMessage());
        }
        
    }

    private Writer createWriter(Library library, String name) {
        Writer writer = EIQLibraryFactory.eINSTANCE.createWriter();
        writer.setName(name);
        library.getWriters().add(writer);
        return writer;
    }

    private Book createBook(Library library, String title, int pages, BookCategory cat) {
        Book book = EIQLibraryFactory.eINSTANCE.createBook();
        book.setTitle(title);
        book.setPages(pages);
        book.getCategory().add(cat);
        library.getBooks().add(book);
        return book;
    }

    private Library createLibrary(String address) {
        Library library = EIQLibraryFactory.eINSTANCE.createLibrary();
        library.setAddress(address);
        return library;
    }
    
}
