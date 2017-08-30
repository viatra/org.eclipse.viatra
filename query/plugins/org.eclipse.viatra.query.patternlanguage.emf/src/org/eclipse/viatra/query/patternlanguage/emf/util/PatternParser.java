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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidator;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.LazyStringInputStream;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @since 1.5
 */
class PatternParser {
    @Inject
    private Provider<XtextResourceSet> resourceSetProvider;

    @Inject
    private IResourceFactory resourceFactory;
    
    @Inject
    private FileExtensionProvider extensionProvider;
    
    @Inject
    private PatternSetValidator validator;
    
    private String fileExtension;

    private SpecificationBuilder builder;
    
    public PatternParsingResults parse(String text, SpecificationBuilder builder) {
        this.builder = builder;
        fileExtension = extensionProvider.getPrimaryFileExtension();
        return parse(text, createResourceSet());
    }  
    
    protected PatternParsingResults parse(InputStream in, URI uriToUse, Map<?, ?> options, ResourceSet resourceSet) {
        Resource resource = resource(in, uriToUse, options, resourceSet);
        EList<EObject> contents = resource.getContents();
        
        List<Pattern> patterns = new ArrayList<>();
        PatternSetValidationDiagnostics diagnostics = validator.validate(resource);
        for (EObject eObject : contents) {
            if(eObject instanceof PatternModel){
                for(Pattern pattern :((PatternModel) eObject).getPatterns()){
                    patterns.add(pattern);
                }
            }
        }
        return new PatternParsingResults(patterns, diagnostics, builder);
    }


    
    protected PatternParsingResults parse(String text, ResourceSet resourceSetToUse){
        return parse(getAsStream(text), computeUnusedUri(resourceSetToUse), null, resourceSetToUse);
    }

    protected PatternParsingResults parse(String text, URI uriToUse, ResourceSet resourceSetToUse){
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
    
    protected XtextResourceSet createResourceSet() {
        return resourceSetProvider.get();
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
