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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * @since 2.0
 */
public class PatternParser extends BasePatternParser{

    
    /**
     * This function encodes the default URI scheme generation used in VIATRA 2.0.0; it is not recommended to use as it
     * can cause surprising behavior with regards to the Xtext index; only provided for backward compatibility. For most
     * users the {@link #UNUSED_ABSOLUTE_FILE_URI_PROVIDER} provides an appropriate default implementations; other users
     * can provide custom implementations with {@link PatternParserBuilder#unusedURIComputer(BiFunction)}.
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

    private final BiFunction<ResourceSet, String, URI> unusedURIComputer;


    /**
     * @since 2.1
     */
    protected PatternParser(Set<IQuerySpecification<?>> librarySpecifications, Set<URI> libraryURIs, BiFunction<ResourceSet, String, URI> unusedURIComputer) {
        super(librarySpecifications, libraryURIs);
        this.unusedURIComputer = unusedURIComputer;
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
    
    /**
     * @deprecated use {@link PatternParserBuilder}
     */
    @Deprecated
    public static class Builder extends PatternParserBuilder {}
    
    /**
     * @deprecated use {@link PatternParserBuilder}
     */
    public static Builder parser() {
        return new Builder();
    }
}
