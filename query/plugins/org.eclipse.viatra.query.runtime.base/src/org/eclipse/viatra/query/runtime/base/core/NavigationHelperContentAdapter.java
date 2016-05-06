/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.base.api.DataTypeListener;
import org.eclipse.viatra.query.runtime.base.api.FeatureListener;
import org.eclipse.viatra.query.runtime.base.api.InstanceListener;
import org.eclipse.viatra.query.runtime.base.api.LightweightEObjectObserver;
import org.eclipse.viatra.query.runtime.base.api.filters.IBaseIndexObjectFilter;
import org.eclipse.viatra.query.runtime.base.api.filters.IBaseIndexResourceFilter;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFModelComprehension;
import org.eclipse.viatra.query.runtime.base.comprehension.EMFVisitor;
import org.eclipse.viatra.query.runtime.base.exception.ViatraBaseException;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class NavigationHelperContentAdapter extends EContentAdapter {

    private final NavigationHelperImpl navigationHelper;



    // move optimization to avoid removing and re-adding entire subtrees
    protected EObject ignoreInsertionAndDeletion;
    // Set<EObject> ignoreRootInsertion = new HashSet<EObject>();
    // Set<EObject> ignoreRootDeletion = new HashSet<EObject>();

    private final EMFModelComprehension comprehension;

    private IBaseIndexObjectFilter objectFilterConfiguration;
    private IBaseIndexResourceFilter resourceFilterConfiguration;



    private EMFVisitor removalVisitor;
    private EMFVisitor insertionVisitor;

    public NavigationHelperContentAdapter(final NavigationHelperImpl navigationHelper) {
        this.navigationHelper = navigationHelper;
        final BaseIndexOptions options = this.navigationHelper.getBaseIndexOptions();
        objectFilterConfiguration = options.getObjectFilterConfiguration();
        resourceFilterConfiguration = options.getResourceFilterConfiguration();
        this.comprehension = navigationHelper.getComprehension();
        
        removalVisitor = new NavigationHelperVisitor.ChangeVisitor(navigationHelper, false);
        insertionVisitor = new NavigationHelperVisitor.ChangeVisitor(navigationHelper, true);
    }

    // key representative of the EObject class


    @Override
    public void notifyChanged(final Notification notification) {
        try {
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    NavigationHelperContentAdapter.super.notifyChanged(notification);

                    final Object oFeature = notification.getFeature();
                    final Object oNotifier = notification.getNotifier();
                    if (oNotifier instanceof EObject && oFeature instanceof EStructuralFeature) {
                        final EObject notifier = (EObject) oNotifier;
                        final EStructuralFeature feature = (EStructuralFeature) oFeature;

                        final boolean notifyLightweightObservers = handleNotification(notification, notifier, feature);

                        if (notifyLightweightObservers) {
                            navigationHelper.notifyLightweightObservers(notifier, feature, notification);
                        }
                    } else if (oNotifier instanceof Resource) {
                        if (notification.getFeatureID(Resource.class) == Resource.RESOURCE__IS_LOADED) {
                            final Resource resource = (Resource) oNotifier;
                            if (comprehension.isLoading(resource))
                                navigationHelper.resolutionDelayingResources.add(resource);
                            else
                                navigationHelper.resolutionDelayingResources.remove(resource);
                        }
                    }
                    return null;
                }
            });
        } catch (final InvocationTargetException ex) {
            navigationHelper.processingFatal(ex.getCause(), "handling the following update notification: " + notification);
        } catch (final Exception ex) {
            navigationHelper.processingFatal(ex, "handling the following update notification: " + notification);
        }

        navigationHelper.notifyBaseIndexChangeListeners();
    }

    @SuppressWarnings("deprecation")
    private boolean handleNotification(final Notification notification, final EObject notifier,
            final EStructuralFeature feature) {
        final Object oldValue = notification.getOldValue();
        final Object newValue = notification.getNewValue();
        final int positionInt = notification.getPosition();
        final Integer position = positionInt == Notification.NO_INDEX ? null : positionInt;
        final int eventType = notification.getEventType();
        boolean notifyLightweightObservers = true;
        switch (eventType) {
        case Notification.ADD:
            featureUpdate(true, notifier, feature, newValue, position);
            break;
        case Notification.ADD_MANY:
            for (final Object newElement : (Collection<?>) newValue) {
                featureUpdate(true, notifier, feature, newElement, position);
            }
            break;
        case Notification.CREATE:
            notifyLightweightObservers = false;
            break;
        case Notification.MOVE:
            // lightweight observers should be notified on MOVE
            break; // currently no support for ordering
        case Notification.REMOVE:
            featureUpdate(false, notifier, feature, oldValue, position);
            break;
        case Notification.REMOVE_MANY:
            for (final Object oldElement : (Collection<?>) oldValue) {
                featureUpdate(false, notifier, feature, oldElement, position);
            }
            break;
        case Notification.REMOVING_ADAPTER:
            notifyLightweightObservers = false;
            break;
        case Notification.RESOLVE:
            if (navigationHelper.isFeatureResolveIgnored(feature))
                break; // otherwise same as SET
            if (!feature.isMany()) { // if single-valued, can be removed from delayed resolutions
                navigationHelper.delayedProxyResolutions.remove(notifier, feature);
            }
            // fall-through
        case Notification.UNSET:
        case Notification.SET:
            featureUpdate(false, notifier, feature, oldValue, position);
            featureUpdate(true, notifier, feature, newValue, position);
            break;
        default:
            notifyLightweightObservers = false;
            break;
        }
        return notifyLightweightObservers;
    }

    private void featureUpdate(final boolean isInsertion, final EObject notifier, final EStructuralFeature feature,
            final Object value, final Integer position) {
        // this is a safe visitation, no reads will happen, thus no danger of notifications or matcher construction
        comprehension.traverseFeature(getVisitorForChange(isInsertion), notifier, feature, value, position);
    }

    @Override
    // OFFICIAL ENTRY POINT
    protected void addAdapter(final Notifier notifier) {
        if (notifier == ignoreInsertionAndDeletion) {
            return;
        }
        try {
            // cross-resource containment workaround, see Bug 483089 and Bug 483086.
            if (notifier.eAdapters().contains(this))
                return;

            if (objectFilterConfiguration != null && objectFilterConfiguration.isFiltered(notifier)) {
                return;
            }
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // the object is really traversed BEFORE the notification listener is added,
                    // so that if a proxy is resolved due to the traversal, we do not get notified about it
                    if (notifier instanceof EObject) {
                        comprehension.traverseObject(getVisitorForChange(true), (EObject) notifier);
                    } else if (notifier instanceof Resource) {
                        Resource resource = (Resource) notifier;
                        if (resourceFilterConfiguration != null
                                && resourceFilterConfiguration.isResourceFiltered(resource)) {
                            return null;
                        }
                        if (comprehension.isLoading(resource))
                            navigationHelper.resolutionDelayingResources.add(resource);
                    }
                    // subscribes to the adapter list, will receive setTarget callback that will spread addAdapter to
                    // children
                    NavigationHelperContentAdapter.super.addAdapter(notifier);
                    return null;
                }
            });
        } catch (final InvocationTargetException ex) {
            navigationHelper.processingFatal(ex.getCause(), "add the object: " + notifier);
        } catch (final Exception ex) {
            navigationHelper.processingFatal(ex, "add the object: " + notifier);
        }
    }

    @Override
    // OFFICIAL ENTRY POINT
    protected void removeAdapter(final Notifier notifier) {
        removeAdapter(notifier, true, true);
    }

    // The additional boolean options are there to save the cost of extra checks, see Bug 483089 and Bug 483086.
    void removeAdapter(final Notifier notifier, boolean additionalResourceContainerPossible,
            boolean additionalObjectContainerPossible) {
        if (notifier == ignoreInsertionAndDeletion) {
            return;
        }
        try {

            // cross-resource containment workaround, see Bug 483089 and Bug 483086.
            if (notifier instanceof InternalEObject) {
                InternalEObject internalEObject = (InternalEObject) notifier;
                if (additionalResourceContainerPossible) {
                    Resource eDirectResource = internalEObject.eDirectResource();
                    if (eDirectResource != null && eDirectResource.eAdapters().contains(this)) {
                        return;
                    }
                }
                if (additionalObjectContainerPossible) {
                    InternalEObject eInternalContainer = internalEObject.eInternalContainer();
                    if (eInternalContainer != null && eInternalContainer.eAdapters().contains(this)) {
                        return;
                    }
                }
            }

            if (objectFilterConfiguration != null && objectFilterConfiguration.isFiltered(notifier)) {
                return;
            }
            this.navigationHelper.coalesceTraversals(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (notifier instanceof EObject) {
                        final EObject eObject = (EObject) notifier;
                        comprehension.traverseObject(getVisitorForChange(false), eObject);
                        navigationHelper.delayedProxyResolutions.removeAll(eObject);
                    } else if (notifier instanceof Resource) {
                        if (resourceFilterConfiguration != null
                                && resourceFilterConfiguration.isResourceFiltered((Resource) notifier)) {
                            return null;
                        }
                        navigationHelper.resolutionDelayingResources.remove(notifier);
                    }
                    // unsubscribes from the adapter list, will receive unsetTarget callback that will spread
                    // removeAdapter to children
                    NavigationHelperContentAdapter.super.removeAdapter(notifier);
                    return null;
                }
            });
        } catch (final InvocationTargetException ex) {
            navigationHelper.processingFatal(ex.getCause(), "remove the object: " + notifier);
        } catch (final Exception ex) {
            navigationHelper.processingFatal(ex, "remove the object: " + notifier);
        }
    }


    protected EMFVisitor getVisitorForChange(final boolean isInsertion) {
        return isInsertion ? insertionVisitor : removalVisitor;
    }



    // The superclass contains a workaround for Bug 385039.
    // The workaround (checking whether the adapter is installed at 'notifier') is no longer necessary for this subclass
    // (since the check is performed by addAdapter() anyway).
    // Therefore we are overriding to improve performance.
    @Override
    protected void setTarget(final ResourceSet target) {
        basicSetTarget(target);
        final List<Resource> resources = target.getResources();
        for (int i = 0; i < resources.size(); ++i) {
            final Notifier notifier = resources.get(i);
            addAdapter(notifier);
        }
    }

    // This override mitigates the performance consequences of Bug 483089 and Bug 483086
    @Override
    protected void unsetTarget(Resource target) {
        basicUnsetTarget(target);
        List<EObject> contents = target.getContents();
        for (int i = 0, size = contents.size(); i < size; ++i) {
            Notifier notifier = contents.get(i);
            removeAdapter(notifier, false, true);
        }
    }

    // WORKAROUND (TMP) for eContents vs. derived features bug
    @Override
    protected void setTarget(final EObject target) {
        basicSetTarget(target);
        spreadToChildren(target, true);
    }

    @Override
    protected void unsetTarget(final EObject target) {
        basicUnsetTarget(target);
        spreadToChildren(target, false);
    }

    protected void spreadToChildren(final EObject target, final boolean add) {
        final EList<EReference> features = target.eClass().getEAllReferences();
        for (final EReference feature : features) {
            if (!feature.isContainment()) {
                continue;
            }
            if (!comprehension.representable(feature)) {
                continue;
            }
            if (feature.isMany()) {
                final Collection<?> values = (Collection<?>) target.eGet(feature);
                for (final Object value : values) {
                    final Notifier notifier = (Notifier) value;
                    if (add) {
                        addAdapter(notifier);
                    } else {
                        removeAdapter(notifier, true, false);
                    }
                }
            } else {
                final Object value = target.eGet(feature);
                if (value != null) {
                    final Notifier notifier = (Notifier) value;
                    if (add) {
                        addAdapter(notifier);
                    } else {
                        removeAdapter(notifier, true, false);
                    }
                }
            }
        }
    }


}
