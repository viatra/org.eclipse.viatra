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
package org.eclipse.incquery.uml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.uml.derivedfeatures.AssociationEndTypeMatcher;
import org.eclipse.incquery.uml.derivedfeatures.NamedElementQualifiedNameMatcher;
import org.eclipse.incquery.uml.derivedfeatures.StateIsOrthogonalMatcher;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityPartition;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Denes Harmath
 *
 */
public class TestUmlDerivedFeatures {

	private static final UMLFactory FACTORY = UMLFactory.eINSTANCE;

	private static IncQueryEngine getEngine(Resource resource) throws IncQueryException {
		return IncQueryEngine.on(new EMFScope(resource));
	}
	
	private static Resource createResource() {
		return new ResourceSetImpl().createResource(URI.createURI("test"));
	}

	@Test
	public void associationEndType() throws IncQueryException {
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
    public void namedElementQualifiedName() throws IncQueryException {
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
	public void stateIsOrthogonal() throws IncQueryException {
	    Resource resource = createResource();
        State state = FACTORY.createState();
        resource.getContents().add(state);
        state.getRegions().add(FACTORY.createRegion());
        StateIsOrthogonalMatcher matcher = StateIsOrthogonalMatcher.on(getEngine(resource));
        assertEquals(ImmutableSet.of(false), matcher.getAllValuesOftarget(state));
        state.getRegions().add(FACTORY.createRegion());
        assertEquals(ImmutableSet.of(true), matcher.getAllValuesOftarget(state));
	}

	@Test
	public void listsWellbehaving() {
		Activity source = FACTORY.createActivity();
		final AtomicBoolean added = new AtomicBoolean(false);
		final AtomicBoolean removed = new AtomicBoolean(false);
		final ActivityPartition activityPartition = FACTORY.createActivityPartition();
		source.eAdapters().add(new AdapterImpl() {
			@Override
			public void notifyChanged(Notification msg) {
				super.notifyChanged(msg);
				if (msg.getFeature() == UMLPackage.Literals.ACTIVITY__GROUP) {
					if (msg.getEventType() == Notification.ADD && msg.getNewValue() == activityPartition) {
						added.set(true);
					}
					if (msg.getEventType() == Notification.REMOVE && msg.getOldValue() == activityPartition) {
						removed.set(true);
					}
				}
			}
		});
		source.getGroups().add(activityPartition);
		assertTrue(added.get());
		source.getGroups().remove(activityPartition);
		assertTrue(removed.get());
	}
	
}
