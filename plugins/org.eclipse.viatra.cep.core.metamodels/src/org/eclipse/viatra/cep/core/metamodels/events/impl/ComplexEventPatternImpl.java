/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Complex Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl#getCompositionEvents <em>Composition Events</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl#getOperator <em>Operator</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl#getTimeWindow <em>Time Window</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComplexEventPatternImpl extends EventPatternImpl implements ComplexEventPattern {
    /**
     * The cached value of the '{@link #getCompositionEvents() <em>Composition Events</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCompositionEvents()
     * @generated
     * @ordered
     */
    protected EList<EventPattern> compositionEvents;

    /**
     * The cached value of the '{@link #getOperator() <em>Operator</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOperator()
     * @generated
     * @ordered
     */
    protected ComplexEventOperator operator;

    /**
     * The cached value of the '{@link #getTimeWindow() <em>Time Window</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimeWindow()
     * @generated
     * @ordered
     */
    protected TimeWindow timeWindow;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ComplexEventPatternImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.COMPLEX_EVENT_PATTERN;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<EventPattern> getCompositionEvents() {
        if (compositionEvents == null) {
            compositionEvents = new EObjectContainmentEList<EventPattern>(EventPattern.class, this, EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS);
        }
        return compositionEvents;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ComplexEventOperator getOperator() {
        return operator;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOperator(ComplexEventOperator newOperator, NotificationChain msgs) {
        ComplexEventOperator oldOperator = operator;
        operator = newOperator;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, oldOperator, newOperator);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperator(ComplexEventOperator newOperator) {
        if (newOperator != operator) {
            NotificationChain msgs = null;
            if (operator != null)
                msgs = ((InternalEObject)operator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, null, msgs);
            if (newOperator != null)
                msgs = ((InternalEObject)newOperator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, null, msgs);
            msgs = basicSetOperator(newOperator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR, newOperator, newOperator));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTimeWindow(TimeWindow newTimeWindow, NotificationChain msgs) {
        TimeWindow oldTimeWindow = timeWindow;
        timeWindow = newTimeWindow;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW, oldTimeWindow, newTimeWindow);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimeWindow(TimeWindow newTimeWindow) {
        if (newTimeWindow != timeWindow) {
            NotificationChain msgs = null;
            if (timeWindow != null)
                msgs = ((InternalEObject)timeWindow).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW, null, msgs);
            if (newTimeWindow != null)
                msgs = ((InternalEObject)newTimeWindow).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW, null, msgs);
            msgs = basicSetTimeWindow(newTimeWindow, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW, newTimeWindow, newTimeWindow));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean evaluateParameterBindings(Event event) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS:
                return ((InternalEList<?>)getCompositionEvents()).basicRemove(otherEnd, msgs);
            case EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR:
                return basicSetOperator(null, msgs);
            case EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW:
                return basicSetTimeWindow(null, msgs);
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
            case EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS:
                return getCompositionEvents();
            case EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR:
                return getOperator();
            case EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW:
                return getTimeWindow();
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
            case EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS:
                getCompositionEvents().clear();
                getCompositionEvents().addAll((Collection<? extends EventPattern>)newValue);
                return;
            case EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR:
                setOperator((ComplexEventOperator)newValue);
                return;
            case EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW:
                setTimeWindow((TimeWindow)newValue);
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
            case EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS:
                getCompositionEvents().clear();
                return;
            case EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR:
                setOperator((ComplexEventOperator)null);
                return;
            case EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW:
                setTimeWindow((TimeWindow)null);
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
            case EventsPackage.COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS:
                return compositionEvents != null && !compositionEvents.isEmpty();
            case EventsPackage.COMPLEX_EVENT_PATTERN__OPERATOR:
                return operator != null;
            case EventsPackage.COMPLEX_EVENT_PATTERN__TIME_WINDOW:
                return timeWindow != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        switch (operationID) {
            case EventsPackage.COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDINGS__EVENT:
                return evaluateParameterBindings((Event)arguments.get(0));
        }
        return super.eInvoke(operationID, arguments);
    }

} //ComplexEventPatternImpl
