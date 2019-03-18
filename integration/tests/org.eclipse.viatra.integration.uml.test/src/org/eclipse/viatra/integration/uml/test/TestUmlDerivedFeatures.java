/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.uml.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.viatra.integration.uml.derivedfeatures.AssociationEndType;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementNamespace;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementQualifiedName;
import org.eclipse.viatra.integration.uml.derivedfeatures.StateIsOrthogonal;
import org.eclipse.viatra.query.runtime.api.GenericPatternMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Denes Harmath
 *
 */
public class TestUmlDerivedFeatures {

    private static final UMLFactory FACTORY = UMLFactory.eINSTANCE;

    private static ViatraQueryEngine getEngine(Resource resource) {
        return ViatraQueryEngine.on(new EMFScope(resource));
    }
    
    private static Resource createResource() {
        return new ResourceSetImpl().createResource(URI.createURI("test"));
    }
    
    @Test
    public void addAffectsContainment() {
        Resource resource = createResource();
        Package pkg = FACTORY.createPackage();
        pkg.setName("pkg");
        Package pkg1 = FACTORY.createPackage();
        pkg1.setName("pkg1");
        Class clazz = FACTORY.createClass();
        clazz.setName("cl");
        resource.getContents().add(pkg);
        pkg.getPackagedElements().add(pkg1);
        pkg.getPackagedElements().add(clazz);
        NamedElementQualifiedName specification = NamedElementQualifiedName.instance();
        GenericPatternMatcher matcher = specification.getMatcher(getEngine(resource));
        pkg1.getPackagedElements().add(clazz);
        assertEquals(ImmutableSet.of(clazz.getQualifiedName()), matcher.getAllValues("qualifiedName",
                specification.newMatch(clazz, null)));
    }

    @Test
    public void associationEndType() {
        Resource resource = createResource();
        Association association = FACTORY.createAssociation();
        resource.getContents().add(association);
        Property memberEnd = FACTORY.createProperty();
        resource.getContents().add(memberEnd);
        association.getMemberEnds().add(memberEnd);
        Type endType = FACTORY.createClass();
        resource.getContents().add(endType);
        memberEnd.setType(endType);
        GenericPatternMatcher matcher = AssociationEndType.instance().getMatcher(getEngine(resource));
        assertEquals(ImmutableSet.of(endType), matcher.getAllValues("type"));
    }

    @Test
    public void namedElementNamespace() {
        Resource resource = createResource();
        Package rootPackage = FACTORY.createPackage();
        resource.getContents().add(rootPackage);
        rootPackage.setName("root");
        Package childPackage = FACTORY.createPackage();
        rootPackage.getPackagedElements().add(childPackage);
        childPackage.setName("child");
        NamedElementNamespace specification = NamedElementNamespace.instance();
        GenericPatternMatcher matcher = specification.getMatcher(getEngine(resource));
        assertEquals(ImmutableSet.of(rootPackage), matcher.getAllValues("target", 
                specification.newMatch(childPackage, null)));
    }

    @Test
    public void namedElementQualifiedName() {
        Resource resource = createResource();
        Package rootPackage = FACTORY.createPackage();
        resource.getContents().add(rootPackage);
        rootPackage.setName("root");
        Package childPackage = FACTORY.createPackage();
        rootPackage.getPackagedElements().add(childPackage);
        childPackage.setName("child");
        NamedElementQualifiedName specification = NamedElementQualifiedName.instance();
        GenericPatternMatcher matcher = specification.getMatcher(getEngine(resource));
        assertEquals(ImmutableSet.of(rootPackage.getName() + NamedElement.SEPARATOR + childPackage.getName()),
                matcher.getAllValues("qualifiedName", specification.newMatch(childPackage, null)));
    }

    @Test
    public void stateIsOrthogonal() {
        Resource resource = createResource();
        State state = FACTORY.createState();
        resource.getContents().add(state);
        state.getRegions().add(FACTORY.createRegion());
        StateIsOrthogonal specification = StateIsOrthogonal.instance();
        GenericPatternMatcher matcher = specification.getMatcher(getEngine(resource));
        assertEquals(ImmutableSet.of(false), matcher.getAllValues("target",
                specification.newMatch(state, null)));
        state.getRegions().add(FACTORY.createRegion());
        assertEquals(ImmutableSet.of(true), matcher.getAllValues("target",
                specification.newMatch(state, null)));
    }
    
}
