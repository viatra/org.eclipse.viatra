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
import java.util.function.BiFunction;

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
    
    /**
     * This function encodes the default URI scheme generation used in VIATRA 2.0.0; it is not recommended to use as it
     * can cause surprising behavior with regards to the Xtext index; only provided for backward compatibility. For most
     * users the {@link #UNUSED_ABSOLUTE_FILE_URI_PROVIDER} provides an appropriate default implementations; other users
     * can provide custom implementations with {@link Builder#unusedURIComputer(BiFunction)}.
     * 
     * @since 2.1
     */
    public static final BiFunction<ResourceSet, String, URI> UNUSED_RELATIVE_URI_PROVIDER = (rs, ext) -> {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            URI syntheticUri = URI.createURI(SYNTHETIC_URI_PREFIX + i + "." + ext);
            if (rs.getResource(syntheticUri, false) == null)
                return syntheticUri;
        }
        throw new IllegalStateException();
    };
    /**
     * This function encodes the a file URI scheme that is relative to folder described by the user.dir system property.
     * @since 2.1
     */
    public static final BiFunction<ResourceSet, String, URI> UNUSED_ABSOLUTE_FILE_URI_PROVIDER = (rs, ext) -> {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            URI syntheticUri = URI.createURI(SYNTHETIC_URI_PREFIX + i + "." + ext)
                    .resolve(URI.createFileURI(System.getProperty("user.dir")));
            if (rs.getResource(syntheticUri, false) == null)
                return syntheticUri;
        }
        throw new IllegalStateException();
    };

    public static class Builder {
        public static final String PPERROR = "The VIATRA query language parser infrastructure is not initialized, pattern parsing is not supported.";

        private Injector injector = null;
        private Set<URI> libraryURIs = new HashSet<>();
        private Set<IQuerySpecification<?>> librarySpecifications = new HashSet<>();

        private BiFunction<ResourceSet, String, URI> unusedURIComputer = UNUSED_ABSOLUTE_FILE_URI_PROVIDER;

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

        /**
         * @since 2.1
         */
        public Builder withUnusedURIComputer(BiFunction<ResourceSet, String, URI> unusedURIComputer) {
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
    private final BiFunction<ResourceSet, String, URI> unusedURIComputer;
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

    private PatternParser(Set<IQuerySpecification<?>> librarySpecifications, Set<URI> libraryURIs, BiFunction<ResourceSet, String, URI> unusedURIComputer) {
        this.librarySpecifications = new HashSet<>(librarySpecifications);
        this.libraryURIs = libraryURIs;
        this.unusedURIComputer = unusedURIComputer;
        this.reuseSpecificationBuilder = true;
    }

    /**
     * Parses a string as the contents of a VQL file and puts the results in a Resource with a previously unused URI
     */
    public PatternParsingResults parse(String text) {
        Preconditions.checkState(resourceSet != null, "Resource set was not initialized for the parser.");
        fileExtension = extensionProvider.getPrimaryFileExtension();
        return parse(text, resourceSet);
    }
    
    /**
     * Parses a string as the contents of a VQL file and puts the results in a Resource with the specified URI
     * @since 2.1
     */
    public PatternParsingResults parse(String text, URI uri) {
        Preconditions.checkState(resourceSet != null, "Resource set was not initialized for the parser.");
        Preconditions.checkState(resourceSet.getResource(uri, false) == null, "Specified URI was already used before.");
        fileExtension = extensionProvider.getPrimaryFileExtension();
        return parse(getAsStream(text), Objects.requireNonNull(uri), null, resourceSet);
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
        return parse(getAsStream(text), unusedURIComputer.apply(resourceSetToUse, fileExtension), null, resourceSetToUse);
    }

    protected PatternParsingResults parse(String text, URI uriToUse, ResourceSet resourceSetToUse) {
        return parse(getAsStream(text), uriToUse, null, resourceSetToUse);
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
