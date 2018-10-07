/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus, Istvan Rath and Daniel Varro, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *    Istvan Rath - refactorings to accommodate to generic/patternspecific API differences
 *    Zoltan Ujhelyi - created a new example for various API features based on the old headless example for VIATRA 2.0
 *******************************************************************************/
package org.eclipse.viatra.documentation.example;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.documentation.example.queries.HostIpAddress;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.CyberPhysicalSystemPackage;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

/**
 * @author Abel Hegedus
 * @author Istvan Rath
 *
 */
public class ViatraQueryHeadless {

    protected Resource loadModel(String modelPath) {
        URI fileURI = URI.createFileURI(modelPath);
        return loadModel(fileURI);
    }

    protected Resource loadModel(URI fileURI) {
        // Loads the resource
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(fileURI, true);
        if (resource == null) {
            throw new IllegalArgumentException("Cannot load resource " + fileURI.toString());
        }
        return resource;
    }

    protected void prettyPrintMatches(StringBuilder results, Collection<? extends IPatternMatch> matches) {
        for (IPatternMatch match : matches) {
            results.append(match.prettyPrint() + "\n");
        }
        if (matches.isEmpty()) {
            results.append("Empty match set");
        }
        results.append("\n");
    }

    public String executeDemo(String modelPath) {
        final StringBuilder results = new StringBuilder();
        Resource resource = loadModel(modelPath);
        // get all matches of the pattern
        // initialization
        // phase 1: (managed) ViatraQueryEngine
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resource));
        // phase 2: the matcher itself
        HostIpAddress.Matcher matcher = HostIpAddress.Matcher.on(engine);
        // get all matches of the pattern
        Collection<HostIpAddress.Match> matches = matcher.getAllMatches();
        prettyPrintMatches(results, matches);
        // using a match processor
        matcher.forEachMatch(
                m -> results.append(String.format("\tHost: %s IP %s", m.getHost().getIdentifier(), m.getIp())));
        return results.toString();
    }

    public String patternParserDemo(URI modelURI) {
        // tag::patternParser[]
        final StringBuilder results = new StringBuilder();
        Resource resource = loadModel(modelURI);

        // Initializing Xtext-based resource parser (once per Java application)
        new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();

        // Parse pattern definition
        PatternParsingResults parseResults = PatternParserBuilder.instance()
                .parse("import \"http://org.eclipse.viatra/model/cps\" \n"
                        + "\n"
                        + "pattern hostIpAddress(host: HostInstance, ip : java String) {\n"
                        + "    HostInstance.nodeIp(host,ip);\n"
                        + "}");
        ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resource));

        parseResults.getQuerySpecification("hostIpAddress").ifPresent(specification -> {
            ViatraQueryMatcher<?> matcher = engine.getMatcher(specification);
            prettyPrintMatches(results, matcher.getAllMatches());
        });

        return results.toString();
        // end::patternParser[]
    }

    private static String modelParam = "-m";

    public static void main(String[] args) {
        String model = null;
        if (args == null || args.length == 0) {
            displayHelp();
            return;
        }
        int i = 0;
        while (i < args.length) {
            if (args[i].equals(modelParam)) {
                model = args[i + 1];
                i += 2;
            } else {
                i++;
            }
        }

        if (model == null) {
            System.out.println("Model parameter not set");
            displayHelp();
            return;
        }

        // Initializing EMF
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        CyberPhysicalSystemPackage.eINSTANCE.getNsURI();
        URI modelURI = URI.createFileURI(model);

        ViatraQueryHeadless app = new ViatraQueryHeadless();
        System.out.println(app.patternParserDemo(modelURI));

    }

    private static void displayHelp() {
        System.out.println("Usage:\n<call> -m <modelFilePath>");
        System.out.println("  -m    :  Required, the model to match on.");
    }
}
