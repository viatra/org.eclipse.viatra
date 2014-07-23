/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public final class EMFHelper {

    private static final Logger logger = Logger.getLogger(EMFHelper.class);

    private EMFHelper() {
    }

    public static TransactionalEditingDomain wrapModelInDummyDomain(EObject root) {
        // TODO maybe there is already a ted on the eobject
        TransactionalEditingDomain ted = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
        Resource createResource = ted.getResourceSet().createResource(URI.createFileURI("DUMMY"));
        ted.getCommandStack().execute(new AddCommand(ted, createResource.getContents(), root));
        return ted;
    }

    public static void serializeModel(EObject root, String name, String ext) {

        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(ext, new XMIResourceFactoryImpl());

        // Obtain a new resource set
        ResourceSet resSet = new ResourceSetImpl();

        URI uri = URI.createURI(name + "." + ext);
        // Create a resource
        Resource resource = resSet.createResource(uri);

        // Get the first model element and cast it to the right type, in my
        // example everything is hierarchical included in this first node
        Copier copier = new Copier();
        EObject result = copier.copy(root);

        copier.copyReferences();

        resource.getContents().add(result);

        // Now save the content.
        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static EObject clone(EObject root) {
        Copier copier = new Copier();
        EObject result = copier.copy(root);

        copier.copyReferences();
        return result;

    }

    public static List<EModelElement> getClassesAndReferences(Collection<EPackage> metaModelPackages) {
        List<EModelElement> result = new ArrayList<EModelElement>();
        for (EPackage ePackage : metaModelPackages) {
            // Add all classes and references from the package
            for (EClassifier eClassifier : ePackage.getEClassifiers()) {
                if (eClassifier instanceof EClass) {
                    EClass eClass = ((EClass) eClassifier);
                    addToListIfNotContains(result, eClass);
                    for (EClass c : eClass.getEAllSuperTypes()) {
                        addToListIfNotContains(result, c);
                    }
                    for (EReference eReference : eClass.getEAllReferences()) {
                        addToListIfNotContains(result, eReference);
                    }
                }
            }
        }
        return result;
    }

    private static <T, E extends T> void addToListIfNotContains(List<T> list, E element) {
        if (!list.contains(element)) {
            list.add(element);
        }
    }
}
