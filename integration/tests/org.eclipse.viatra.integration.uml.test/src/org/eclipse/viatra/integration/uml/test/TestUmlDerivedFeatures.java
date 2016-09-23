/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Denes Harmath - initial API and implementation
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
import org.eclipse.viatra.integration.uml.derivedfeatures.AssociationEndTypeMatcher;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementNamespaceMatcher;
import org.eclipse.viatra.integration.uml.derivedfeatures.NamedElementQualifiedNameMatcher;
import org.eclipse.viatra.integration.uml.derivedfeatures.StateIsOrthogonalMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Denes Harmath
 *
 */
public class TestUmlDerivedFeatures {

	private static final UMLFactory FACTORY = UMLFactory.eINSTANCE;

	private static ViatraQueryEngine getEngine(Resource resource) throws ViatraQueryException {
		return ViatraQueryEngine.on(new EMFScope(resource));
	}
	
	private static Resource createResource() {
		return new ResourceSetImpl().createResource(URI.createURI("test"));
	}
	
	@Test
    public void addAffectsContainment() throws ViatraQueryException {
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
        NamedElementQualifiedNameMatcher matcher = NamedElementQualifiedNameMatcher.on(getEngine(resource));
        pkg1.getPackagedElements().add(clazz);
        assertEquals(ImmutableSet.of(clazz.getQualifiedName()), matcher.getAllValuesOfqualifiedName(clazz));
    }

	@Test
	public void associationEndType() throws ViatraQueryException {
		Resource resource = createResource();
		Association association = FACTORY.createAssociation();
		resource.getContents().add(association);
		Property memberEnd = FACTORY.createProperty();
		resource.getContents().add(memberEnd);
		association.getMemberEnds().add(memberEnd);
		Type endType = FACTORY.createClass();
		resource.getContents().add(endType);
		memberEnd.setType(endType);
		AssociationEndTypeMatcher matcher = AssociationEndTypeMatcher.on(getEngine(resource));
		assertEquals(ImmutableSet.of(endType), matcher.getAllValuesOftype());
	}

	@Test
    public void namedElementNamespace() throws ViatraQueryException {
	    Resource resource = createResource();
        Package rootPackage = FACTORY.createPackage();
        resource.getContents().add(rootPackage);
        rootPackage.setName("root");
        Package childPackage = FACTORY.createPackage();
        rootPackage.getPackagedElements().add(childPackage);
        childPackage.setName("child");
        NamedElementNamespaceMatcher matcher = NamedElementNamespaceMatcher.on(getEngine(resource));
        assertEquals(ImmutableSet.of(rootPackage), matcher.getAllValuesOftarget(childPackage));
    }

	@Test
    public void namedElementQualifiedName() throws ViatraQueryException {
	    Resource resource = createResource();
	    Package rootPackage = FACTORY.createPackage();
        resource.getContents().add(rootPackage);
        rootPackage.setName("root");
        Package childPackage = FACTORY.createPackage();
        rootPackage.getPackagedElements().add(childPackage);
        childPackage.setName("child");
        NamedElementQualifiedNameMatcher matcher = NamedElementQualifiedNameMatcher.on(getEngine(resource));
        assertEquals(ImmutableSet.of(rootPackage.getName() + NamedElement.SEPARATOR + childPackage.getName()), matcher.getAllValuesOfqualifiedName(childPackage));
    }

	@Test
	public void stateIsOrthogonal() throws ViatraQueryException {
	    Resource resource = createResource();
        State state = FACTORY.createState();
        resource.getContents().add(state);
        state.getRegions().add(FACTORY.createRegion());
        StateIsOrthogonalMatcher matcher = StateIsOrthogonalMatcher.on(getEngine(resource));
        assertEquals(ImmutableSet.of(false), matcher.getAllValuesOftarget(state));
        state.getRegions().add(FACTORY.createRegion());
        assertEquals(ImmutableSet.of(true), matcher.getAllValuesOftarget(state));
	}
	
}
