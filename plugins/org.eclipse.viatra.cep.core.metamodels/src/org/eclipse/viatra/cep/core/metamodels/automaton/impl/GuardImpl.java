/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.Guard;
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition;

import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Guard</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl#getEventType <em>Event Type</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl#getTransition <em>Transition</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GuardImpl extends MinimalEObjectImpl.Container implements Guard {
    /**
     * The cached value of the '{@link #getEventType() <em>Event Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventType()
     * @generated
     * @ordered
     */
    protected AtomicEventPattern eventType;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected GuardImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return AutomatonPackage.Literals.GUARD;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AtomicEventPattern getEventType() {
        if (eventType != null && eventType.eIsProxy()) {
            InternalEObject oldEventType = (InternalEObject)eventType;
            eventType = (AtomicEventPattern)eResolveProxy(oldEventType);
            if (eventType != oldEventType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.GUARD__EVENT_TYPE, oldEventType, eventType));
            }
        }
        return eventType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AtomicEventPattern basicGetEventType() {
        return eventType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEventType(AtomicEventPattern newEventType) {
        AtomicEventPattern oldEventType = eventType;
        eventType = newEventType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.GUARD__EVENT_TYPE, oldEventType, eventType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TypedTransition getTransition() {
        if (eContainerFeatureID() != AutomatonPackage.GUARD__TRANSITION) return null;
        return (TypedTransition)eInternalContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTransition(TypedTransition newTransition, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newTransition, AutomatonPackage.GUARD__TRANSITION, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTransition(TypedTransition newTransition) {
        if (newTransition != eInternalContainer() || (eContainerFeatureID() != AutomatonPackage.GUARD__TRANSITION && newTransition != null)) {
            if (EcoreUtil.isAncestor(this, newTransition))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newTransition != null)
                msgs = ((InternalEObject)newTransition).eInverseAdd(this, AutomatonPackage.TYPED_TRANSITION__GUARD, TypedTransition.class, msgs);
            msgs = basicSetTransition(newTransition, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.GUARD__TRANSITION, newTransition, newTransition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case AutomatonPackage.GUARD__TRANSITION:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetTransition((TypedTransition)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case AutomatonPackage.GUARD__TRANSITION:
                return basicSetTransition(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case AutomatonPackage.GUARD__TRANSITION:
                return eInternalContainer().eInverseRemove(this, AutomatonPackage.TYPED_TRANSITION__GUARD, TypedTransition.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case AutomatonPackage.GUARD__EVENT_TYPE:
                if (resolve) return getEventType();
                return basicGetEventType();
            case AutomatonPackage.GUARD__TRANSITION:
                return getTransition();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case AutomatonPackage.GUARD__EVENT_TYPE:
                setEventType((AtomicEventPattern)newValue);
                return;
            case AutomatonPackage.GUARD__TRANSITION:
                setTransition((TypedTransition)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case AutomatonPackage.GUARD__EVENT_TYPE:
                setEventType((AtomicEventPattern)null);
                return;
            case AutomatonPackage.GUARD__TRANSITION:
                setTransition((TypedTransition)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case AutomatonPackage.GUARD__EVENT_TYPE:
                return eventType != null;
            case AutomatonPackage.GUARD__TRANSITION:
                return getTransition() != null;
        }
        return super.eIsSet(featureID);
    }

} //GuardImpl
