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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageConfigurationConstants;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidator;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.LazyStringInputStream;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Base class for various pattern parser implementations.
 * 
 * @author Peter Lunk
 * @since 2.1
 *
 */
public abstract class BasePatternParser {
    public static final String SYNTHETIC_URI_PREFIX = "__synthetic";
    
    
    @Inject
    protected IResourceDescription.Manager manager;

    @Inject
    protected IResourceFactory resourceFactory;

    @Inject
    protected FileExtensionProvider extensionProvider;

    @Inject
    protected PatternSetValidator validator;

    protected String fileExtension;

    protected SpecificationBuilder builder;

    protected ResourceSet resourceSet;

    protected final Set<URI> libraryURIs;
    protected final Set<IQuerySpecification<?>> librarySpecifications;

    private boolean reuseSpecificationBuilder;
    
    protected BasePatternParser(Set<IQuerySpecification<?>> librarySpecifications, Set<URI> libraryURIs) {
        this.librarySpecifications = new HashSet<>(librarySpecifications);
        this.libraryURIs = libraryURIs;
        this.reuseSpecificationBuilder = true;
    }
    
    protected SpecificationBuilder getOrCreateSpecificationBuilder() {
        if (!reuseSpecificationBuilder  || builder == null) {
            builder = new SpecificationBuilder(librarySpecifications);
        }
        return builder; 
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
    
}
