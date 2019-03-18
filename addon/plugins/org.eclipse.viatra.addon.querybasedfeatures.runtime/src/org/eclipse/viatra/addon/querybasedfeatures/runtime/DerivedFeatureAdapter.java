/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFVisitor;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * Adapter that turns derived features that recalculate their values on each getter call
 * based on a well-defined set of navigation from the source into well-behaving derived features
 * by automatically calling the getters and sending notifications.
 * 
 * @author Abel Hegedus
 */
public class DerivedFeatureAdapter extends AdapterImpl {
    private final InternalEObject source;
    private final EStructuralFeature derivedFeature;
    private final DerivedFeatureEMFVisitor visitor = new DerivedFeatureEMFVisitor();

    /**
     * Only used for single reference!
     */
    private Object currentValue;
    private Object oldValue;
    private EClassifier type;
    private EMFModelComprehension comprehension;
    private Logger logger = ViatraQueryLoggingUtil.getLogger(DerivedFeatureAdapter.class);

    private final List<EStructuralFeature> localFeatures = new ArrayList<>();
    private final List<DependentFeaturePath> featurePaths = new ArrayList<>();

    /**
     * Convenience constructor for a local and navigated dependency
     */
    public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature,
            EStructuralFeature navigationFeature, EStructuralFeature dependantFeature, EStructuralFeature localFeature) {
        this(source, derivedFeature);
        addNavigatedDependencyInternal(navigationFeature, dependantFeature);
        addLocalDependencyInternal(localFeature);
    }

    /**
     * Convenience constructor for a navigated dependency
     */
    public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature,
            EStructuralFeature navigationFeature, EStructuralFeature dependantFeature) {
        this(source, derivedFeature);
        addNavigatedDependencyInternal(navigationFeature, dependantFeature);
    }

    /**
     * Convenience constructor for a local dependency
     */
    public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature, EStructuralFeature localFeature) {
        this(source, derivedFeature);
        addLocalDependencyInternal(localFeature);
    }

    public DerivedFeatureAdapter(EObject source, EStructuralFeature derivedFeature) {
        super();
        comprehension = new EMFModelComprehension(new BaseIndexOptions());
        this.source = (InternalEObject) source;
        this.derivedFeature = derivedFeature;
        source.eAdapters().add(this);
    }

    public void addNavigatedDependency(EStructuralFeature navigationFeature, EStructuralFeature dependantFeature) {
        if (navigationFeature == null || dependantFeature == null) {
            return;
        }
        if (!source.eClass().getEAllStructuralFeatures().contains(navigationFeature)) {
            return;
        }
        if (!(navigationFeature.getEType() instanceof EClass)
                || !dependantFeature.getEContainingClass().isSuperTypeOf((EClass) navigationFeature.getEType())) {
            return;
        }
        addNavigatedDependencyInternal(navigationFeature, dependantFeature);
    }

    public void addLocalDependency(EStructuralFeature localFeature) {
        if (localFeature == null) {
            return;
        }
        if (!source.eClass().getEAllStructuralFeatures().contains(localFeature)) {
            return;
        }
        addLocalDependencyInternal(localFeature);
    }

    private void addNavigatedDependencyInternal(EStructuralFeature navigationFeature,
            EStructuralFeature dependantFeature) {
        featurePaths.add(new DependentFeaturePath(navigationFeature, dependantFeature));
    }

    private void addLocalDependencyInternal(EStructuralFeature localFeature) {
        localFeatures.add(localFeature);
    }

    @Override
    public void notifyChanged(Notification notification) {
        logger.trace("[Source: " + derivedFeature.getName() + "] New notification: " + notification);
        for (DependentFeaturePath path : featurePaths) {
            if (Objects.equals(notification.getFeature(), path.getNavigationFeature())) {
                logger.trace("Handling notification.");
                switch (notification.getEventType()) {
                case Notification.SET:
                    EObject newValue = (EObject) notification.getNewValue();
                    EObject tempOldValue = (EObject) notification.getOldValue();
                    if (tempOldValue != null) {
                        tempOldValue.eAdapters().remove(path.getDependantAdapter());
                    } else {
                        logger.debug("[DerivedFeatureAdapter] oldValue is not set");
                    }
                    if (newValue != null) {
                        newValue.eAdapters().add(path.getDependantAdapter());
                    } else {
                        logger.debug("[DerivedFeatureAdapter] new value is not set");
                    }
                    break;
                case Notification.ADD:
                    EObject added = (EObject) notification.getNewValue();
                    added.eAdapters().add(path.getDependantAdapter());
                    break;
                case Notification.ADD_MANY:
                    EObject newValueCollection = (EObject) notification.getNewValue();
                    for (Object newElement : (Collection<?>) newValueCollection) {
                        ((Notifier) newElement).eAdapters().add(path.getDependantAdapter());
                    }
                    break;
                case Notification.REMOVE:
                    EObject removed = (EObject) notification.getOldValue();
                    removed.eAdapters().remove(path.getDependantAdapter());
                    break;
                case Notification.REMOVE_MANY:
                    EObject oldValueCollection = (EObject) notification.getOldValue();
                    for (Object oldElement : (Collection<?>) oldValueCollection) {
                        ((Notifier) oldElement).eAdapters().remove(path.getDependantAdapter());
                    }
                    break;
                case Notification.UNSET:
                    EObject unset = (EObject) notification.getOldValue();
                    unset.eAdapters().remove(path.getDependantAdapter());
                    break;
                case Notification.CREATE:
                case Notification.MOVE: // currently no support for ordering
                case Notification.RESOLVE: // TODO is it safe to ignore all of them?
                case Notification.REMOVING_ADAPTER:
                    break;
                default:
                    logger.debug(
                            "[DerivedFeatureAdapter] Unhandled notification: " + notification.getEventType());
                    return; // No notification
                }
                refreshDerivedFeature();
            }
        }
        if (localFeatures.contains(notification.getFeature())) {
            logger.trace("Handling notification.");
            refreshDerivedFeature();
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshDerivedFeature() {
        logger.trace("[Notify: " + derivedFeature.getName() + "] Derived refresh.");
        try {
            if (source.eNotificationRequired()) {
                if (type == null) {
                    type = derivedFeature.getEType();
                }
                if (derivedFeature.isMany()) {
                    if (currentValue != null) {
                        oldValue = new HashSet<>((Collection<EObject>) currentValue);
                    } else {
                        oldValue = new HashSet<>();
                    }
                    currentValue = new HashSet<>();
                    Collection<? extends Object> targets = (Collection<? extends Object>) source.eGet(derivedFeature);
                    int position = 0;
                    for (Object target : targets) {
                        comprehension.traverseFeature(visitor, source, derivedFeature, target, position++);
                    }
                    if (currentValue instanceof Collection<?> && oldValue instanceof Collection<?>) {
                        ((Collection<?>) oldValue).removeAll((Collection<?>) currentValue);
                        if (!((Collection<?>) oldValue).isEmpty()) {
                            sendRemoveManyNotification(source, derivedFeature, oldValue);
                        }
                    }
                } else {
                    oldValue = currentValue;
                    Object target = source.eGet(derivedFeature);
                    comprehension.traverseFeature(visitor, source, derivedFeature, target, null);
                    if (!Objects.equals(oldValue, target)) {
                        comprehension.traverseFeature(visitor, source, derivedFeature, target, null);
                        currentValue = target;
                    }             
                }
            }
        } catch (Exception ex) {
            logger.error(
                    "The derived feature adapter encountered an error in processing the EMF model. "
                            + "This happened while maintaining the derived feature " + derivedFeature.getName()
                            + " of object " + source, ex);
        }
    }

    private class DependentFeaturePath {
        private EStructuralFeature dependantFeature = null;
        private EStructuralFeature navigationFeature = null;

        private final AdapterImpl dependantAdapter = new AdapterImpl() {

            @Override
            public void notifyChanged(Notification msg) {
                logger.trace("[Dependant: " + derivedFeature.getName() + "] New notification: " + msg);
                if (Objects.equals(msg.getFeature(), dependantFeature)) {
                    refreshDerivedFeature();
                }
            }
        };

        public DependentFeaturePath(EStructuralFeature navigationFeature, EStructuralFeature dependantFeature) {
            this.dependantFeature = dependantFeature;
            this.navigationFeature = navigationFeature;
        }

        public AdapterImpl getDependantAdapter() {
            return dependantAdapter;
        }

        public EStructuralFeature getNavigationFeature() {
            return navigationFeature;
        }
    }

    protected class DerivedFeatureEMFVisitor extends EMFVisitor {

        public DerivedFeatureEMFVisitor() {
            super(true /* preOrder */);
        }

        @Override
        public void visitAttribute(EObject source, EAttribute feature, Object target) {
            logger.trace("Attribute refresh.");
            // send set notification
            sendSetNotification(source, feature, currentValue, target);
            storeSingleValue(feature, target);
        }

        @Override
        public void visitElement(EObject source) {
            return;
        }

        @Override
        public void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
            logger.trace("Non-containment reference refresh.");
            sendNotificationForEReference(source, feature, target);
        }

        @Override
        public void visitInternalContainment(EObject source, EReference feature, EObject target) {
            logger.trace("Containment reference refresh.");
            sendNotificationForEReference(source, feature, target);
        }

        @Override
        public boolean pruneSubtrees(EObject source) {
            return true;
        }
    }

    /**
     * @param source
     * @param feature
     * @param target
     */
    private void sendSetNotification(EObject source, EStructuralFeature feature, Object oldTarget, Object target) {
        source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.SET, feature, oldTarget, target));
    }

    /**
     * @param source
     * @param feature
     * @param target
     */
    private void sendAddNotification(EObject source, EStructuralFeature feature, Object target) {
        source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.ADD, feature, null, target));
    }

    /**
     * @param source
     * @param feature
     * @param target
     */
    private void sendRemoveManyNotification(EObject source, EStructuralFeature feature, Object oldTarget) {
        source.eNotify(new ENotificationImpl((InternalEObject) source, Notification.REMOVE_MANY, feature, oldTarget,
                null));
    }

    @SuppressWarnings("unchecked")
    private void sendNotificationForEReference(EObject source, EReference feature, EObject target) {
        if (feature.isMany() && oldValue instanceof Collection<?> && currentValue instanceof Collection<?>) {
            // send ADD notification
            if (!((Collection<?>) oldValue).contains(target)) {
                sendAddNotification(source, feature, target);
                // add to currentValue
            }
            ((Collection<EObject>) currentValue).add(target);
        } else {
            if (!Objects.equals(oldValue, target)) {
                sendSetNotification(source, feature, currentValue, target);
            }
        }
    }

    /**
     * @param feature
     * @param target
     */
    private void storeSingleValue(EAttribute feature, Object target) {
        // store current value
        if (feature.isChangeable()) {
            if (target instanceof EObject) {
                currentValue = EcoreUtil.copy((EObject) target);
            }
        } else {
            currentValue = target;
        }
    }
}
