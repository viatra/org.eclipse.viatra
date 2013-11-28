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
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.RunOnceQueryEngine;
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.junit.Test;

/**
 * @author Abel Hegedus
 *
 */
public class RunOnceTest {
    
    private ResourceSet rs;

    @Test
    public void testRegularQuery() {
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
            Collection<BooksWithMultipleAuthorsMatch> allMatches = engine.getAllMatches(BooksWithMultipleAuthorsMatcher.querySpecification());
            assertTrue(allMatches.size() == 2);
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }

    private Library prepareModel() {
        String modelPath = "/org.eclipse.incquery.runtime.runonce.tests/model/test.eiqlibrary";
        rs = new ResourceSetImpl();
        URI modelUri = URI.createPlatformPluginURI(modelPath, true);
        Resource resource = rs.getResource(modelUri, true);
        return (Library) resource.getContents().get(0);
    }

    @Test
    public void testSimpleAttribute() {
        // TODO this should work only after WellBehavingFeatureRegistry is filled
        // TODO after adding base options, must work without touching WellBehavingRegistry

        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
//            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            Collection<SumOfPagesInLibraryMatch> allMatches = engine.getAllMatches(SumOfPagesInLibraryMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SumOfPagesInLibraryMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getSumOfPages() == 222);
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSingleReference() {
        // TODO this should work only after WellBehavingFeatureRegistry is filled
        // TODO after adding base options, must work without touching WellBehavingRegistry
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
//            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            Collection<SingleAuthoredFirstBooksMatch> allMatches = engine.getAllMatches(SingleAuthoredFirstBooksMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SingleAuthoredFirstBooksMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getFirstBook().getTitle().equals("Other SciFi"));
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testManyReference() {
        // TODO this should work only after WellBehavingFeatureRegistry is filled
        // TODO after adding base options, must work without touching WellBehavingRegistry
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
//            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            Collection<LongSciFiBooksOfAuthorMatch> allMatches = engine.getAllMatches(LongSciFiBooksOfAuthorMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            LongSciFiBooksOfAuthorMatch match = allMatches.iterator().next();
            assertTrue(match.getAuthor().getName().equals("Third Author"));
            assertTrue(match.getBook().getTitle().equals("Other SciFi"));
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testNonDeterministicAttribute() {
        // TODO this should fail when dispose traverses the model again
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
//            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            Collection<RequestCountOfLibraryMatch> allMatches = engine.getAllMatches(RequestCountOfLibraryMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            RequestCountOfLibraryMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getReqCount() == 2);
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testNonDeterministicFeature() {
        // TODO this should fail when dispose traverses the model again
        Library library = prepareModel();
        
        try {
            RunOnceQueryEngine engine = new RunOnceQueryEngine(rs);
//            WellbehavingDerivedFeatureRegistry.registerWellbehavingDerivedPackage(EIQLibraryPackage.eINSTANCE);
            Collection<SomeBooksWithTwoAuthorsMatch> allMatches = engine.getAllMatches(SomeBooksWithTwoAuthorsMatcher.querySpecification());
            assertTrue(allMatches.size() == 1);
            SomeBooksWithTwoAuthorsMatch match = allMatches.iterator().next();
            assertTrue(match.getLibrary().equals(library));
            assertTrue(match.getBook().getTitle().equals("Twin life"));

            allMatches = engine.getAllMatches(SomeBooksWithTwoAuthorsMatcher.querySpecification());
            assertTrue(allMatches.isEmpty());
            
        } catch (IncQueryException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testModelModification() {
        // TODO the results of incremental engine will not be correct
        
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
        }
    }

    @Test
    public void testSamplingModelModification() {
        // TODO the results of incremental engine will be correct if sampling is invoked by the client after changes
        
        fail("Not yet implemented");
    }
    
    
    
}
