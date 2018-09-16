/*******************************************************************************
 * Copyright (c) 2010-2018, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

import com.google.inject.Injector;

/**
 * @author Peter Lunk
 * @since 2.1
 */
public class PatternParserBuilder {
    public static final String PPERROR = "The VIATRA query language parser infrastructure is not initialized, pattern parsing is not supported.";

    private Injector injector = null;
    private Set<URI> libraryURIs = new HashSet<>();
    private Set<IQuerySpecification<?>> librarySpecifications = new HashSet<>();

    private BiFunction<ResourceSet, String, URI> unusedURIComputer = PatternParser.UNUSED_ABSOLUTE_FILE_URI_PROVIDER;

    
    public static PatternParserBuilder instance(){
        return new PatternParserBuilder();
    }
    
    /**
     * Provide a specific injector instance to use with this parser
     */
    public PatternParserBuilder withInjector(Injector injector) {
        this.injector = injector;
        return this;
    }

    /**
     * Provide an URI to a VQL file that can be used as a library.
     */
    public PatternParserBuilder withLibrary(URI libraryURI) {
        libraryURIs.add(libraryURI);
        return this;
    }

    /**
     * Provide an URI to a VQL file that can be used as a library, together with a set of query specifications
     * already created from this library. This can be used to include the generated query specifications for the
     * given library.
     */
    public PatternParserBuilder withLibrary(URI libraryURI, Collection<IQuerySpecification<?>> specifications) {
        libraryURIs.add(libraryURI);
        this.librarySpecifications.addAll(specifications);
        return this;
    }

    /**
     * @since 2.1
     */
    public PatternParserBuilder withUnusedURIComputer(BiFunction<ResourceSet, String, URI> unusedURIComputer) {
        this.unusedURIComputer = Objects.requireNonNull(unusedURIComputer);
        return this;
    }
    
    private Injector getInjector() {
        if (injector == null) {
            return Objects.requireNonNull(XtextInjectorProvider.INSTANCE.getInjector(), PPERROR);
        }
        return injector;
    }

    /**
     * Initializes the pattern parser instance
     */
    public PatternParser build() {
        PatternParser parser = new PatternParser(librarySpecifications, libraryURIs, unusedURIComputer);
        getInjector().injectMembers(parser);
        return parser;
    }
    
    public AdvancedPatternParser buildAdvanced() {
        AdvancedPatternParser parser = new AdvancedPatternParser(librarySpecifications, libraryURIs);
        getInjector().injectMembers(parser);
        return parser;
    }

    /**
     * Creates a single-use pattern parser instance and collects the parsing results for the selected text, then the
     * forgets the used parser instance.
     */
    public PatternParsingResults parse(String text) {
        return build().parse(text);

    }
}