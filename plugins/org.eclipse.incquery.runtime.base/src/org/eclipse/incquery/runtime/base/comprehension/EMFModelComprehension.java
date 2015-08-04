/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.comprehension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.incquery.runtime.base.api.filters.IBaseIndexResourceFilter;

/**
 * @author Bergmann Gábor
 * 
 *         Does not directly visit derived links, unless marked as a WellBehavingFeature. Derived edges are
 *         automatically interpreted correctly in these cases: - EFeatureMaps - eOpposites of containments
 * 
 * 
 */
public class EMFModelComprehension {

    private BaseIndexOptions options;
    
    /**
     * Creates a model comprehension with the specified options. The options are copied, therefore subsequent changes
     * will not affect the comprehension.
     */
    public EMFModelComprehension(BaseIndexOptions options) {
        this.options = options.copy();
    }
    
    /**
     * Should not traverse this feature directly. It is still possible that it can be represented in IQBase if
     * {@link #representable(EStructuralFeature)} is true.
     */
    public boolean untraversableDirectly(EStructuralFeature feature) {
        if((feature instanceof EReference && ((EReference)feature).isContainer())) {
            // container features are always represented through their opposite
            return true;
        }
        
        boolean suspect = onlySamplingFeature(feature);
        if(suspect) {
            // even if the feature can only be sampled, it may be used if the proper base index option is set
            suspect = options.isTraverseOnlyWellBehavingDerivedFeatures();
        }
        return suspect;
    }

    /**
     * Decides whether a feature can only be sampled as there is no guarantee that proper notifications will be
     * delivered by their implementation.
     * 
     * <p/> Such features are derived (and/or volatile) features that are not well-behaving.
     */
    public boolean onlySamplingFeature(EStructuralFeature feature) {
        boolean suspect = 
        		feature.isDerived() || 
        		feature.isVolatile();
        if (suspect) {
            // override support here
            // (e.g. if manual notifications available, or no changes expected afterwards)
            suspect = !WellbehavingDerivedFeatureRegistry.isWellbehavingFeature(feature);
            // TODO verbose flag somewhere to ease debugging (for such warnings)
            // TODO add warning about not visited subtree (containment, FeatureMap and annotation didn't define
            // otherwise)
        }
        return suspect;
    }

    /**
     * This feature can be represented in IQBase.
     */
    public boolean representable(EStructuralFeature feature) {
        if (!untraversableDirectly(feature))
            return true;

        if (feature instanceof EReference) {
            final EReference reference = (EReference) feature;
            if (reference.isContainer() && representable(reference.getEOpposite()))
                return true;
        }

        boolean isMixed = "mixed".equals(EcoreUtil.getAnnotation(feature.getEContainingClass(),
                ExtendedMetaData.ANNOTATION_URI, "kind"));
        if (isMixed)
            return true; // TODO maybe check the "name"=":mixed" or ":group" feature for representability?

        final String groupAnnotation = EcoreUtil.getAnnotation(feature, ExtendedMetaData.ANNOTATION_URI, "group");
        if (groupAnnotation != null && groupAnnotation.length() > 1 && '#' == groupAnnotation.charAt(0)) {
            final String groupFeatureName = groupAnnotation.substring(1);
            final EStructuralFeature groupFeature = feature.getEContainingClass().getEStructuralFeature(
                    groupFeatureName);
            return representable(groupFeature);
        }

        return false;
    }

    /** 
     * Resource filters not consulted here (for performance), because model roots are assumed to be pre-filtered. 
     */
    public void traverseModel(EMFVisitor visitor, Notifier source) {
        if (source == null)
            return;
        if (source instanceof EObject) {
            final EObject sourceObject = (EObject) source;
            if (sourceObject.eIsProxy()) 
            	throw new IllegalArgumentException("Proxy EObject cannot act as model roots for EMF-IncQuery: " + source);
			traverseObject(visitor, sourceObject);
        } else if (source instanceof Resource) {
            traverseResource(visitor, (Resource) source);
        } else if (source instanceof ResourceSet) {
            traverseResourceSet(visitor, (ResourceSet) source);
        }
    }

    public void traverseResourceSet(EMFVisitor visitor, ResourceSet source) {
        if (source == null)
            return;
        final List<Resource> resources = new ArrayList<Resource>(source.getResources());
        for (Resource resource : resources) {
            traverseResourceIfUnfiltered(visitor, resource);
        }
    }

    public void traverseResourceIfUnfiltered(EMFVisitor visitor, Resource resource) {
		final IBaseIndexResourceFilter resourceFilter = options.getResourceFilterConfiguration();
		if (resourceFilter != null && resourceFilter.isResourceFiltered(resource))
			return;
		final IBaseIndexObjectFilter objectFilter = options.getObjectFilterConfiguration();
		if (objectFilter != null && objectFilter.isFiltered(resource))
			return;
		
		traverseResource(visitor, resource);
	}

    public void traverseResource(EMFVisitor visitor, Resource source) {
        if (source == null)
            return;
        if (visitor.pruneSubtrees(source))
            return;
        final EList<EObject> contents = source.getContents();
        for (EObject eObject : contents) {
            traverseObjectIfUnfiltered(visitor, eObject);
        }
    }


	public void traverseObjectIfUnfiltered(EMFVisitor visitor, EObject targetObject) {
		final IBaseIndexObjectFilter objectFilter = options.getObjectFilterConfiguration();
		if (objectFilter != null && objectFilter.isFiltered(targetObject))
			return;
		
		traverseObject(visitor, targetObject);
	}

	public void traverseObject(EMFVisitor visitor, EObject source) {
        if (source == null)
            return;

        if (visitor.preOrder()) visitor.visitElement(source);
        for (EStructuralFeature feature : source.eClass().getEAllStructuralFeatures()) {
            if (untraversableDirectly(feature))
                continue;
            final boolean visitorPrunes = visitor.pruneFeature(feature);
            if (visitorPrunes && !unprunableFeature(visitor, source, feature))
                continue;

            if (feature.isMany()) {
                Collection<?> targets = (Collection<?>) source.eGet(feature);
                int position = 0;
                for (Object target : targets) {
                    traverseFeatureInternal(visitor, source, feature, target, visitorPrunes, position++);
                }
            } else {
                Object target = source.eGet(feature);
                if (target != null)
                    traverseFeatureInternal(visitor, source, feature, target, visitorPrunes, null);
            }
        }
        if (!visitor.preOrder()) visitor.visitElement(source);
    }

    private boolean unprunableFeature(EMFVisitor visitor, EObject source, EStructuralFeature feature) {
        return (feature instanceof EAttribute && EcorePackage.eINSTANCE.getEFeatureMapEntry().equals(
                ((EAttribute) feature).getEAttributeType()))
                || (feature instanceof EReference && ((EReference) feature).isContainment() && (!visitor
                        .pruneSubtrees(source) || ((EReference) feature).getEOpposite() != null));
    }

    /**
     * @param position optional: known position in multivalued collection (for more efficient proxy resolution)
     */
    public void traverseFeature(EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target, Integer position) {
        if (target == null)
            return;
        if (untraversableDirectly(feature))
            return;
        traverseFeatureInternalSimple(visitor, source, feature, target, position);
    }

    /**
     * @param position optional: known position in multivalued collection (for more efficient proxy resolution)
     */
    private void traverseFeatureInternalSimple(EMFVisitor visitor, EObject source, EStructuralFeature feature,
            Object target, Integer position) {
        final boolean visitorPrunes = visitor.pruneFeature(feature);
        if (visitorPrunes && !unprunableFeature(visitor, source, feature))
            return;

        traverseFeatureInternal(visitor, source, feature, target, visitorPrunes, position);
    }

    /**
     * @pre target != null
     * @param position optional: known position in multivalued collection (for more efficient proxy resolution)
     */
    private void traverseFeatureInternal(EMFVisitor visitor, EObject source, EStructuralFeature feature,
            Object target, boolean visitorPrunes, Integer position) {
        if (feature instanceof EAttribute) {
            if (!visitorPrunes)
                visitor.visitAttribute(source, (EAttribute) feature, target);
            if (target instanceof FeatureMap.Entry) { // emulated derived edge based on FeatureMap
                Entry entry = (FeatureMap.Entry) target;
                final EStructuralFeature emulated = entry.getEStructuralFeature();
                final Object emulatedTarget = entry.getValue();

                emulateUntraversableFeature(visitor, source, emulated, emulatedTarget);
            }
        } else if (feature instanceof EReference) {
            EReference reference = (EReference) feature;
            EObject targetObject = (EObject) target;
            if (reference.isContainment()) {
                if (!visitorPrunes)
                    visitor.visitInternalContainment(source, reference, targetObject);
                if (!visitor.pruneSubtrees(source))
                    traverseObjectIfUnfiltered(visitor, targetObject);

                final EReference opposite = reference.getEOpposite();
                if (opposite != null) { // emulated derived edge based on container opposite
                    emulateUntraversableFeature(visitor, targetObject, opposite, source);
                }
            } else {
                // if (containedElements.contains(target))
                if (!visitorPrunes)
                    visitor.visitNonContainmentReference(source, reference, targetObject);
            }
            if (targetObject.eIsProxy()) {
            	if (!reference.isResolveProxies()) {
            		throw new IllegalStateException(String.format(
            				"EReference '%s' of EClass %s is set as proxy-non-resolving (i.e. it should never point to a proxy, and never lead cross-resource), " +
            						"yet EMF-IncQuery Base encountered a proxy object %s referenced from %s.",
            						reference.getName(), reference.getEContainingClass().getInstanceTypeName(),
            						targetObject, source));
            	}
            	visitor.visitProxyReference(source, reference, targetObject, position);
            }
        }

    }


    /**
     * Emulates a derived edge, if it is not visited otherwise
     * 
     * @pre target != null
     */
    private void emulateUntraversableFeature(EMFVisitor visitor, EObject source,
            final EStructuralFeature emulated, final Object target) {
        if (untraversableDirectly(emulated))
            traverseFeatureInternalSimple(visitor, source, emulated, target, null);
    }

    /**
     * Can be called to attempt to resolve a reference pointing to one or more proxies, using eGet().
     */
	public void tryResolveReference(EObject source, EReference reference) {
		final Object result = source.eGet(reference, true);
		if (reference.isMany()) {
			// no idea which element to get, have to iterate through
			for (EObject touch : (Iterable<EObject>) result);         			
		}
	}
	
    /**
     * Finds out whether the Resource is currently loading 
     */
	public boolean isLoading(Resource resource) {
		return !resource.isLoaded() || ((Resource.Internal)resource).isLoading();
	}

}