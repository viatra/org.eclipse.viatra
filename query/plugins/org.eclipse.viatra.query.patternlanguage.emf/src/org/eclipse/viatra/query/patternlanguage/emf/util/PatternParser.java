/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidator;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.LazyStringInputStream;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * @since 2.0
 */
public class PatternParser {

    public static class Builder {
        public static final String PPERROR = "The VIATRA query language parser infrastructure is not initialized, pattern parsing is not supported.";

        private Injector injector = null;
        private Set<URI> libraryURIs = new HashSet<>();
        private Set<IQuerySpecification<?>> specifications = new HashSet<>();

        public Builder withInjector(Injector injector) {
            this.injector = injector;
            return this;
        }

        public Builder withLibrary(URI libraryURI) {
            libraryURIs.add(libraryURI);
            return this;
        }

        public Builder withLibrary(URI libraryURI, Collection<IQuerySpecification<?>> specifications) {
            libraryURIs.add(libraryURI);
            this.specifications.addAll(specifications);
            return this;
        }

        private Injector getInjector() {
            if (injector == null) {
                Injector defaultInjector = XtextInjectorProvider.INSTANCE.getInjector();
                Preconditions.checkState(defaultInjector != null, PPERROR);
                return defaultInjector;
            }
            return injector;
        }

        private SpecificationBuilder getSpecificationBuilder() {
            return new SpecificationBuilder(specifications);
        }

        public PatternParser build() {
            PatternParser parser = new PatternParser(getSpecificationBuilder(), libraryURIs);
            getInjector().injectMembers(parser);
            return parser;
        }

        public PatternParsingResults parse(String text) {
            return build().parse(text);

        }
    }

    @Inject
    private IResourceFactory resourceFactory;

    @Inject
    private FileExtensionProvider extensionProvider;

    @Inject
    private PatternSetValidator validator;

    private String fileExtension;

    private final SpecificationBuilder builder;

    private ResourceSet resourceSet;

    private final Set<URI> libraryURIs;

    public static Builder parser() {
        return new Builder();
    }

    private PatternParser(SpecificationBuilder builder, Set<URI> libraryURIs) {
        this.builder = builder;
        this.libraryURIs = libraryURIs;
    }

    public PatternParsingResults parse(String text) {
        Preconditions.checkState(resourceSet != null, "Resource set was not initialized for the parser.");
        fileExtension = extensionProvider.getPrimaryFileExtension();
        return parse(text, resourceSet);
    }

    protected PatternParsingResults parse(InputStream in, URI uriToUse, Map<?, ?> options, ResourceSet resourceSet) {
        Resource resource = resource(in, uriToUse, options, resourceSet);
        EList<EObject> contents = resource.getContents();

        List<Pattern> patterns = new ArrayList<>();
        PatternSetValidationDiagnostics diagnostics = validator.validate(resource);
        for (EObject eObject : contents) {
            if (eObject instanceof PatternModel) {
                for (Pattern pattern : ((PatternModel) eObject).getPatterns()) {
                    patterns.add(pattern);
                }
            }
        }
        return new PatternParsingResults(patterns, diagnostics, builder);
    }

    protected PatternParsingResults parse(String text, ResourceSet resourceSetToUse) {
        return parse(getAsStream(text), computeUnusedUri(resourceSetToUse), null, resourceSetToUse);
    }

    protected PatternParsingResults parse(String text, URI uriToUse, ResourceSet resourceSetToUse) {
        return parse(getAsStream(text), uriToUse, null, resourceSetToUse);
    }

    protected URI computeUnusedUri(ResourceSet resourceSet) {
        String name = "__synthetic";
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            URI syntheticUri = URI.createURI(name + i + "." + fileExtension);
            if (resourceSet.getResource(syntheticUri, false) == null)
                return syntheticUri;
        }
        throw new IllegalStateException();
    }

    protected InputStream getAsStream(CharSequence text) {
        return new LazyStringInputStream(text == null ? "" : text.toString());
    }

    @Inject
    public void createResourceSet(Provider<XtextResourceSet> resourceSetProvider) {
        this.resourceSet = resourceSetProvider.get();
        for (URI uri : libraryURIs) {
            resourceSet.getResource(uri, true);
        }
    }

    protected Resource resource(InputStream in, URI uriToUse, Map<?, ?> options, ResourceSet resourceSet) {
        Resource resource = resourceFactory.createResource(uriToUse);
        resourceSet.getResources().add(resource);
        try {
            resource.load(in, options);
            return resource;
        } catch (IOException e) {
            throw new WrappedException(e);
        }
    }

}
