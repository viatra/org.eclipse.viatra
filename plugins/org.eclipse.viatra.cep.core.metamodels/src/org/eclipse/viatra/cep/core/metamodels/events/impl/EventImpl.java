/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl#isIsProcessed <em>Is Processed</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventImpl extends MinimalEObjectImpl.Container implements Event {
    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected static final String TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected String type = TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimestamp()
     * @generated
     * @ordered
     */
    protected static final long TIMESTAMP_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimestamp()
     * @generated
     * @ordered
     */
    protected long timestamp = TIMESTAMP_EDEFAULT;

    /**
     * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSource()
     * @generated
     * @ordered
     */
    protected EventSource source;

    /**
     * The default value of the '{@link #isIsProcessed() <em>Is Processed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsProcessed()
     * @generated
     * @ordered
     */
    protected static final boolean IS_PROCESSED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIsProcessed() <em>Is Processed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIsProcessed()
     * @generated
     * @ordered
     */
    protected boolean isProcessed = IS_PROCESSED_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EventImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return EventsPackage.Literals.EVENT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(String newType) {
        String oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimestamp(long newTimestamp) {
        long oldTimestamp = timestamp;
        timestamp = newTimestamp;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT__TIMESTAMP, oldTimestamp, timestamp));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventSource getSource() {
        if (source != null && source.eIsProxy()) {
            InternalEObject oldSource = (InternalEObject)source;
            source = (EventSource)eResolveProxy(oldSource);
            if (source != oldSource) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, EventsPackage.EVENT__SOURCE, oldSource, source));
            }
        }
        return source;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventSource basicGetSource() {
        return source;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSource(EventSource newSource) {
        EventSource oldSource = source;
        source = newSource;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT__SOURCE, oldSource, source));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIsProcessed() {
        return isProcessed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIsProcessed(boolean newIsProcessed) {
        boolean oldIsProcessed = isProcessed;
        isProcessed = newIsProcessed;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, EventsPackage.EVENT__IS_PROCESSED, oldIsProcessed, isProcessed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case EventsPackage.EVENT__TYPE:
                return getType();
            case EventsPackage.EVENT__TIMESTAMP:
                return getTimestamp();
            case EventsPackage.EVENT__SOURCE:
                if (resolve) return getSource();
                return basicGetSource();
            case EventsPackage.EVENT__IS_PROCESSED:
                return isIsProcessed();
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
            case EventsPackage.EVENT__TYPE:
                setType((String)newValue);
                return;
            case EventsPackage.EVENT__TIMESTAMP:
                setTimestamp((Long)newValue);
                return;
            case EventsPackage.EVENT__SOURCE:
                setSource((EventSource)newValue);
                return;
            case EventsPackage.EVENT__IS_PROCESSED:
                setIsProcessed((Boolean)newValue);
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
            case EventsPackage.EVENT__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case EventsPackage.EVENT__TIMESTAMP:
                setTimestamp(TIMESTAMP_EDEFAULT);
                return;
            case EventsPackage.EVENT__SOURCE:
                setSource((EventSource)null);
                return;
            case EventsPackage.EVENT__IS_PROCESSED:
                setIsProcessed(IS_PROCESSED_EDEFAULT);
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
            case EventsPackage.EVENT__TYPE:
                return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case EventsPackage.EVENT__TIMESTAMP:
                return timestamp != TIMESTAMP_EDEFAULT;
            case EventsPackage.EVENT__SOURCE:
                return source != null;
            case EventsPackage.EVENT__IS_PROCESSED:
                return isProcessed != IS_PROCESSED_EDEFAULT;
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
        result.append(" (type: ");
        result.append(type);
        result.append(", timestamp: ");
        result.append(timestamp);
        result.append(", isProcessed: ");
        result.append(isProcessed);
        result.append(')');
        return result.toString();
    }

} //EventImpl
