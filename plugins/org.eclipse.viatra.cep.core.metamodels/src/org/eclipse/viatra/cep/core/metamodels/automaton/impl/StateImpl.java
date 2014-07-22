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

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone;
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition;

import org.eclipse.viatra.cep.core.metamodels.events.Event;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>State</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getInTransitions <em>In Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getOutTransitions <em>Out Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getEventTokens <em>Event Tokens</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getLastProcessedEvent <em>Last Processed Event</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getInStateOf <em>In State Of</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl#getOutStateOf <em>Out State Of</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StateImpl extends MinimalEObjectImpl.Container implements State {
    /**
     * The cached value of the '{@link #getInTransitions() <em>In Transitions</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInTransitions()
     * @generated
     * @ordered
     */
    protected EList<Transition> inTransitions;

    /**
     * The cached value of the '{@link #getOutTransitions() <em>Out Transitions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutTransitions()
     * @generated
     * @ordered
     */
    protected EList<Transition> outTransitions;

    /**
     * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected static final String LABEL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected String label = LABEL_EDEFAULT;

    /**
     * The cached value of the '{@link #getEventTokens() <em>Event Tokens</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEventTokens()
     * @generated
     * @ordered
     */
    protected EList<EventToken> eventTokens;

    /**
     * The cached value of the '{@link #getLastProcessedEvent() <em>Last Processed Event</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastProcessedEvent()
     * @generated
     * @ordered
     */
    protected Event lastProcessedEvent;

    /**
     * The cached value of the '{@link #getInStateOf() <em>In State Of</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInStateOf()
     * @generated
     * @ordered
     */
    protected TimedZone inStateOf;

    /**
     * The cached value of the '{@link #getOutStateOf() <em>Out State Of</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutStateOf()
     * @generated
     * @ordered
     */
    protected TimedZone outStateOf;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StateImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return AutomatonPackage.Literals.STATE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Transition> getInTransitions() {
        if (inTransitions == null) {
            inTransitions = new EObjectWithInverseResolvingEList<Transition>(Transition.class, this, AutomatonPackage.STATE__IN_TRANSITIONS, AutomatonPackage.TRANSITION__POST_STATE);
        }
        return inTransitions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Transition> getOutTransitions() {
        if (outTransitions == null) {
            outTransitions = new EObjectContainmentWithInverseEList<Transition>(Transition.class, this, AutomatonPackage.STATE__OUT_TRANSITIONS, AutomatonPackage.TRANSITION__PRE_STATE);
        }
        return outTransitions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLabel() {
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLabel(String newLabel) {
        String oldLabel = label;
        label = newLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__LABEL, oldLabel, label));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<EventToken> getEventTokens() {
        if (eventTokens == null) {
            eventTokens = new EObjectWithInverseResolvingEList<EventToken>(EventToken.class, this, AutomatonPackage.STATE__EVENT_TOKENS, AutomatonPackage.EVENT_TOKEN__CURRENT_STATE);
        }
        return eventTokens;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Event getLastProcessedEvent() {
        if (lastProcessedEvent != null && lastProcessedEvent.eIsProxy()) {
            InternalEObject oldLastProcessedEvent = (InternalEObject)lastProcessedEvent;
            lastProcessedEvent = (Event)eResolveProxy(oldLastProcessedEvent);
            if (lastProcessedEvent != oldLastProcessedEvent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.STATE__LAST_PROCESSED_EVENT, oldLastProcessedEvent, lastProcessedEvent));
            }
        }
        return lastProcessedEvent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Event basicGetLastProcessedEvent() {
        return lastProcessedEvent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastProcessedEvent(Event newLastProcessedEvent) {
        Event oldLastProcessedEvent = lastProcessedEvent;
        lastProcessedEvent = newLastProcessedEvent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__LAST_PROCESSED_EVENT, oldLastProcessedEvent, lastProcessedEvent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimedZone getInStateOf() {
        if (inStateOf != null && inStateOf.eIsProxy()) {
            InternalEObject oldInStateOf = (InternalEObject)inStateOf;
            inStateOf = (TimedZone)eResolveProxy(oldInStateOf);
            if (inStateOf != oldInStateOf) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.STATE__IN_STATE_OF, oldInStateOf, inStateOf));
            }
        }
        return inStateOf;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimedZone basicGetInStateOf() {
        return inStateOf;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetInStateOf(TimedZone newInStateOf, NotificationChain msgs) {
        TimedZone oldInStateOf = inStateOf;
        inStateOf = newInStateOf;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__IN_STATE_OF, oldInStateOf, newInStateOf);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInStateOf(TimedZone newInStateOf) {
        if (newInStateOf != inStateOf) {
            NotificationChain msgs = null;
            if (inStateOf != null)
                msgs = ((InternalEObject)inStateOf).eInverseRemove(this, AutomatonPackage.TIMED_ZONE__IN_STATE, TimedZone.class, msgs);
            if (newInStateOf != null)
                msgs = ((InternalEObject)newInStateOf).eInverseAdd(this, AutomatonPackage.TIMED_ZONE__IN_STATE, TimedZone.class, msgs);
            msgs = basicSetInStateOf(newInStateOf, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__IN_STATE_OF, newInStateOf, newInStateOf));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimedZone getOutStateOf() {
        if (outStateOf != null && outStateOf.eIsProxy()) {
            InternalEObject oldOutStateOf = (InternalEObject)outStateOf;
            outStateOf = (TimedZone)eResolveProxy(oldOutStateOf);
            if (outStateOf != oldOutStateOf) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, AutomatonPackage.STATE__OUT_STATE_OF, oldOutStateOf, outStateOf));
            }
        }
        return outStateOf;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimedZone basicGetOutStateOf() {
        return outStateOf;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOutStateOf(TimedZone newOutStateOf, NotificationChain msgs) {
        TimedZone oldOutStateOf = outStateOf;
        outStateOf = newOutStateOf;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__OUT_STATE_OF, oldOutStateOf, newOutStateOf);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutStateOf(TimedZone newOutStateOf) {
        if (newOutStateOf != outStateOf) {
            NotificationChain msgs = null;
            if (outStateOf != null)
                msgs = ((InternalEObject)outStateOf).eInverseRemove(this, AutomatonPackage.TIMED_ZONE__OUT_STATE, TimedZone.class, msgs);
            if (newOutStateOf != null)
                msgs = ((InternalEObject)newOutStateOf).eInverseAdd(this, AutomatonPackage.TIMED_ZONE__OUT_STATE, TimedZone.class, msgs);
            msgs = basicSetOutStateOf(newOutStateOf, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, AutomatonPackage.STATE__OUT_STATE_OF, newOutStateOf, newOutStateOf));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getInTransitions()).basicAdd(otherEnd, msgs);
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutTransitions()).basicAdd(otherEnd, msgs);
            case AutomatonPackage.STATE__EVENT_TOKENS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getEventTokens()).basicAdd(otherEnd, msgs);
            case AutomatonPackage.STATE__IN_STATE_OF:
                if (inStateOf != null)
                    msgs = ((InternalEObject)inStateOf).eInverseRemove(this, AutomatonPackage.TIMED_ZONE__IN_STATE, TimedZone.class, msgs);
                return basicSetInStateOf((TimedZone)otherEnd, msgs);
            case AutomatonPackage.STATE__OUT_STATE_OF:
                if (outStateOf != null)
                    msgs = ((InternalEObject)outStateOf).eInverseRemove(this, AutomatonPackage.TIMED_ZONE__OUT_STATE, TimedZone.class, msgs);
                return basicSetOutStateOf((TimedZone)otherEnd, msgs);
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
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                return ((InternalEList<?>)getInTransitions()).basicRemove(otherEnd, msgs);
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                return ((InternalEList<?>)getOutTransitions()).basicRemove(otherEnd, msgs);
            case AutomatonPackage.STATE__EVENT_TOKENS:
                return ((InternalEList<?>)getEventTokens()).basicRemove(otherEnd, msgs);
            case AutomatonPackage.STATE__IN_STATE_OF:
                return basicSetInStateOf(null, msgs);
            case AutomatonPackage.STATE__OUT_STATE_OF:
                return basicSetOutStateOf(null, msgs);
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
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                return getInTransitions();
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                return getOutTransitions();
            case AutomatonPackage.STATE__LABEL:
                return getLabel();
            case AutomatonPackage.STATE__EVENT_TOKENS:
                return getEventTokens();
            case AutomatonPackage.STATE__LAST_PROCESSED_EVENT:
                if (resolve) return getLastProcessedEvent();
                return basicGetLastProcessedEvent();
            case AutomatonPackage.STATE__IN_STATE_OF:
                if (resolve) return getInStateOf();
                return basicGetInStateOf();
            case AutomatonPackage.STATE__OUT_STATE_OF:
                if (resolve) return getOutStateOf();
                return basicGetOutStateOf();
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
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                getInTransitions().clear();
                getInTransitions().addAll((Collection<? extends Transition>)newValue);
                return;
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                getOutTransitions().clear();
                getOutTransitions().addAll((Collection<? extends Transition>)newValue);
                return;
            case AutomatonPackage.STATE__LABEL:
                setLabel((String)newValue);
                return;
            case AutomatonPackage.STATE__EVENT_TOKENS:
                getEventTokens().clear();
                getEventTokens().addAll((Collection<? extends EventToken>)newValue);
                return;
            case AutomatonPackage.STATE__LAST_PROCESSED_EVENT:
                setLastProcessedEvent((Event)newValue);
                return;
            case AutomatonPackage.STATE__IN_STATE_OF:
                setInStateOf((TimedZone)newValue);
                return;
            case AutomatonPackage.STATE__OUT_STATE_OF:
                setOutStateOf((TimedZone)newValue);
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
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                getInTransitions().clear();
                return;
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                getOutTransitions().clear();
                return;
            case AutomatonPackage.STATE__LABEL:
                setLabel(LABEL_EDEFAULT);
                return;
            case AutomatonPackage.STATE__EVENT_TOKENS:
                getEventTokens().clear();
                return;
            case AutomatonPackage.STATE__LAST_PROCESSED_EVENT:
                setLastProcessedEvent((Event)null);
                return;
            case AutomatonPackage.STATE__IN_STATE_OF:
                setInStateOf((TimedZone)null);
                return;
            case AutomatonPackage.STATE__OUT_STATE_OF:
                setOutStateOf((TimedZone)null);
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
            case AutomatonPackage.STATE__IN_TRANSITIONS:
                return inTransitions != null && !inTransitions.isEmpty();
            case AutomatonPackage.STATE__OUT_TRANSITIONS:
                return outTransitions != null && !outTransitions.isEmpty();
            case AutomatonPackage.STATE__LABEL:
                return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
            case AutomatonPackage.STATE__EVENT_TOKENS:
                return eventTokens != null && !eventTokens.isEmpty();
            case AutomatonPackage.STATE__LAST_PROCESSED_EVENT:
                return lastProcessedEvent != null;
            case AutomatonPackage.STATE__IN_STATE_OF:
                return inStateOf != null;
            case AutomatonPackage.STATE__OUT_STATE_OF:
                return outStateOf != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (label: ");
        result.append(label);
        result.append(')');
        return result.toString();
    }

} //StateImpl
