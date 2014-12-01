/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;

import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Automaton</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl#getStates <em>States</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl#getEventTokens <em>Event Tokens</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl#getTimedZones <em>Timed Zones</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AutomatonImpl extends MinimalEObjectImpl.Container implements Automaton {
    /**
     * The cached value of the '{@link #getStates() <em>States</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStates()
     * @generated
     * @ordered
     */
    protected EList<State> states;

    /**
     * The cached value of the '{@link #getEventPattern() <em>Event Pattern</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventPattern()
     * @generated
     * @ordered
     */
    protected EventPattern eventPattern;

    /**
     * The cached value of the '{@link #getEventTokens() <em>Event Tokens</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventTokens()
     * @generated
     * @ordered
     */
    protected EList<EventToken> eventTokens;

    /**
     * The cached value of the '{@link #getTimedZones() <em>Timed Zones</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimedZones()
     * @generated
     * @ordered
     */
    protected EList<TimedZone> timedZones;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AutomatonImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return AutomatonPackage.Literals.AUTOMATON;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<State> getStates() {
        if (states == null) {
            states = new EObjectContainmentEList<State>(State.class, this, AutomatonPackage.AUTOMATON__STATES);
        }
        return states;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventPattern getEventPattern() {
        if (eventPattern != null && eventPattern.eIsProxy()) {
            InternalEObject oldEventPattern = (InternalEObject)eventPattern;
            eventPattern = (EventPattern)eResolveProxy(oldEventPattern);
            if (eventPattern != oldEventPattern) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.AUTOMATON__EVENT_PATTERN, oldEventPattern, eventPattern));
            }
        }
        return eventPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventPattern basicGetEventPattern() {
        return eventPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetEventPattern(EventPattern newEventPattern, NotificationChain msgs) {
        EventPattern oldEventPattern = eventPattern;
        eventPattern = newEventPattern;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AutomatonPackage.AUTOMATON__EVENT_PATTERN, oldEventPattern, newEventPattern);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEventPattern(EventPattern newEventPattern) {
        if (newEventPattern != eventPattern) {
            NotificationChain msgs = null;
            if (eventPattern != null)
                msgs = ((InternalEObject)eventPattern).eInverseRemove(this, EventsPackage.EVENT_PATTERN__AUTOMATON, EventPattern.class, msgs);
            if (newEventPattern != null)
                msgs = ((InternalEObject)newEventPattern).eInverseAdd(this, EventsPackage.EVENT_PATTERN__AUTOMATON, EventPattern.class, msgs);
            msgs = basicSetEventPattern(newEventPattern, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.AUTOMATON__EVENT_PATTERN, newEventPattern, newEventPattern));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<EventToken> getEventTokens() {
        if (eventTokens == null) {
            eventTokens = new EObjectContainmentEList<EventToken>(EventToken.class, this, AutomatonPackage.AUTOMATON__EVENT_TOKENS);
        }
        return eventTokens;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<TimedZone> getTimedZones() {
        if (timedZones == null) {
            timedZones = new EObjectContainmentEList<TimedZone>(TimedZone.class, this, AutomatonPackage.AUTOMATON__TIMED_ZONES);
        }
        return timedZones;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                if (eventPattern != null)
                    msgs = ((InternalEObject)eventPattern).eInverseRemove(this, EventsPackage.EVENT_PATTERN__AUTOMATON, EventPattern.class, msgs);
                return basicSetEventPattern((EventPattern)otherEnd, msgs);
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
            case AutomatonPackage.AUTOMATON__STATES:
                return ((InternalEList<?>)getStates()).basicRemove(otherEnd, msgs);
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                return basicSetEventPattern(null, msgs);
            case AutomatonPackage.AUTOMATON__EVENT_TOKENS:
                return ((InternalEList<?>)getEventTokens()).basicRemove(otherEnd, msgs);
            case AutomatonPackage.AUTOMATON__TIMED_ZONES:
                return ((InternalEList<?>)getTimedZones()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case AutomatonPackage.AUTOMATON__STATES:
                return getStates();
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                if (resolve) return getEventPattern();
                return basicGetEventPattern();
            case AutomatonPackage.AUTOMATON__EVENT_TOKENS:
                return getEventTokens();
            case AutomatonPackage.AUTOMATON__TIMED_ZONES:
                return getTimedZones();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case AutomatonPackage.AUTOMATON__STATES:
                getStates().clear();
                getStates().addAll((Collection<? extends State>)newValue);
                return;
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                setEventPattern((EventPattern)newValue);
                return;
            case AutomatonPackage.AUTOMATON__EVENT_TOKENS:
                getEventTokens().clear();
                getEventTokens().addAll((Collection<? extends EventToken>)newValue);
                return;
            case AutomatonPackage.AUTOMATON__TIMED_ZONES:
                getTimedZones().clear();
                getTimedZones().addAll((Collection<? extends TimedZone>)newValue);
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
            case AutomatonPackage.AUTOMATON__STATES:
                getStates().clear();
                return;
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                setEventPattern((EventPattern)null);
                return;
            case AutomatonPackage.AUTOMATON__EVENT_TOKENS:
                getEventTokens().clear();
                return;
            case AutomatonPackage.AUTOMATON__TIMED_ZONES:
                getTimedZones().clear();
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
            case AutomatonPackage.AUTOMATON__STATES:
                return states != null && !states.isEmpty();
            case AutomatonPackage.AUTOMATON__EVENT_PATTERN:
                return eventPattern != null;
            case AutomatonPackage.AUTOMATON__EVENT_TOKENS:
                return eventTokens != null && !eventTokens.isEmpty();
            case AutomatonPackage.AUTOMATON__TIMED_ZONES:
                return timedZones != null && !timedZones.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //AutomatonImpl
