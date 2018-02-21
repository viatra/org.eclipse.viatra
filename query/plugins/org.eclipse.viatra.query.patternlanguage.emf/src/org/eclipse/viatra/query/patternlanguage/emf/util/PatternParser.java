/**
 * Copyright (c) 2010-2016, Peter Lunk, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *   Zoltan Ujhelyi - updated with builder API and library support
 */
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageConfigurationConstants;
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
import com.google.inject.name.Named;

/**
 * @since 2.0
 */
public class PatternParser {

    public static final String SYNTHETIC_URI_PREFIX = "__synthetic";

    public static class Builder {
        public static final String PPERROR = "The VIATRA query language parser infrastructure is not initialized, pattern parsing is not supported.";

        private Injector injector = null;
        private Set<URI> libraryURIs = new HashSet<>();
        private Set<IQuerySpecification<?>> librarySpecifications = new HashSet<>();

        /**
         * Provide a specific injector instance to use with this parser
         */
        public Builder withInjector(Injector injector) {
            this.injector = injector;
            return this;
        }

        /**
         * Provide an URI to a VQL file that can be used as a library.
         */
        public Builder withLibrary(URI libraryURI) {
            libraryURIs.add(libraryURI);
            return this;
        }

        /**
         * Provide an URI to a VQL file that can be used as a library, together with a set of query specifications
         * already created from this library. This can be used to include the generated query specifications for the
         * given library.
         */
        public Builder withLibrary(URI libraryURI, Collection<IQuerySpecification<?>> specifications) {
            libraryURIs.add(libraryURI);
            this.librarySpecifications.addAll(specifications);
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
            PatternParser parser = new PatternParser(librarySpecifications, libraryURIs);
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

    @Inject
    private IResourceFactory resourceFactory;

    @Inject
    private FileExtensionProvider extensionProvider;

    @Inject
    private PatternSetValidator validator;

    private String fileExtension;

    private SpecificationBuilder builder;

    private ResourceSet resourceSet;

    private final Set<URI> libraryURIs;
    private final Set<IQuerySpecification<?>> librarySpecifications;
    private boolean reuseSpecificationBuilder;

    public static Builder parser() {
        return new Builder();
    }

    @Inject
    public void enableReuseSpecificationBuilder(@Named(EMFPatternLanguageConfigurationConstants.SEPARATE_PATTERN_PARSER_RUNS_KEY) boolean reuseSpecificationBuilder) {
        this.reuseSpecificationBuilder = reuseSpecificationBuilder;
    }
    
    @Inject
    public void createResourceSet(Provider<XtextResourceSet> resourceSetProvider) {
        this.resourceSet = resourceSetProvider.get();
        for (URI uri : libraryURIs) {
            resourceSet.getResource(uri, true);
        }
    }

    private PatternParser(Set<IQuerySpecification<?>> librarySpecifications, Set<URI> libraryURIs) {
        this.librarySpecifications = new HashSet<>(librarySpecifications);
        this.libraryURIs = libraryURIs;
        this.reuseSpecificationBuilder = true;
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
        return new PatternParsingResults(patterns, diagnostics, getOrCreateSpecificationBuilder());
    }

    protected PatternParsingResults parse(String text, ResourceSet resourceSetToUse) {
        return parse(getAsStream(text), computeUnusedUri(resourceSetToUse), null, resourceSetToUse);
    }

    protected PatternParsingResults parse(String text, URI uriToUse, ResourceSet resourceSetToUse) {
        return parse(getAsStream(text), uriToUse, null, resourceSetToUse);
    }

    protected URI computeUnusedUri(ResourceSet resourceSet) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            URI syntheticUri = URI.createURI(SYNTHETIC_URI_PREFIX + i + "." + fileExtension);
            if (resourceSet.getResource(syntheticUri, false) == null)
                return syntheticUri;
        }
        throw new IllegalStateException();
    }

    protected InputStream getAsStream(CharSequence text) {
        return new LazyStringInputStream(text == null ? "" : text.toString());
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

    private SpecificationBuilder getOrCreateSpecificationBuilder() {
        if (!reuseSpecificationBuilder || builder == null) {
            builder = new SpecificationBuilder(librarySpecifications);
        }
        return builder; 
    }

}
