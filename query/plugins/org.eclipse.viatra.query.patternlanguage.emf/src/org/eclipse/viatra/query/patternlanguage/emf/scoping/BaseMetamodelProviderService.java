/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.EcoreGenmodelRegistry;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public abstract class BaseMetamodelProviderService implements IMetamodelProvider {

    @Inject
    protected Logger logger;
    @Inject
    protected IQualifiedNameConverter qualifiedNameConverter;
    @Inject
    private EcoreGenmodelRegistry genmodelRegistry;

    protected EcoreGenmodelRegistry getGenmodelRegistry() {
    //        if (genmodelRegistry == null)
    //            genmodelRegistry = new EcoreGenmodelRegistry(logger);
            return genmodelRegistry;
        }

    @Override
    public IScope getAllMetamodelObjects(IScope delegateScope, EObject context) {
        final Collection<String> packageURIs = getProvidedMetamodels();
        Iterable<IEObjectDescription> metamodels = Iterables.transform(packageURIs,
                new Function<String, IEObjectDescription>() {
                    @Override
                    public IEObjectDescription apply(String from) {
                        InternalEObject proxyPackage = (InternalEObject) EcoreFactory.eINSTANCE.createEPackage();
                        proxyPackage.eSetProxyURI(URI.createURI(from));
                        QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(from);
                        return EObjectDescription.create(qualifiedName, proxyPackage,
                                Collections.singletonMap("nsURI", "true"));
                    }
                });
        return new SimpleScope(IScope.NULLSCOPE, metamodels);
    }

    protected abstract Collection<String> getProvidedMetamodels();

    protected abstract String doGetQualifiedClassName(EClassifier classifier, EObject context);
    
    @Override
    public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
        if (getProvidedMetamodels().contains(ePackage.getNsURI())) {
            Iterator<EClassifier> it = Iterators.filter(ePackage.eAllContents(), EClassifier.class);
            boolean missingNameFound = false;
            while (!missingNameFound && it.hasNext()) {
                final String instanceClassName = it.next().getInstanceClassName();
                missingNameFound = Strings.isNullOrEmpty(instanceClassName);
            }
            return !missingNameFound || getGenmodelRegistry().findGenPackage(ePackage.getNsURI(), set) != null;
        } else {
            return false;
        }
    }

    @Override
    public String getQualifiedClassName(EClassifier classifier, EObject context) {
        if (!Strings.isNullOrEmpty(classifier.getInstanceClassName())) {
            return classifier.getInstanceClassName();
        } else {
            if (classifier.getEPackage() != null && getProvidedMetamodels().contains(classifier.getEPackage().getNsURI())) {
                return doGetQualifiedClassName(classifier, context);
            }
            return null;
        }
    }

}
