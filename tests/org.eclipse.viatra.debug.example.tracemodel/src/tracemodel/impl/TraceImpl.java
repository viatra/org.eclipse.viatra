/**
 */
package tracemodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import tracemodel.Trace;
import tracemodel.TracemodelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tracemodel.impl.TraceImpl#getDtUMLElement <em>Dt UML Element</em>}</li>
 *   <li>{@link tracemodel.impl.TraceImpl#getUmlElement <em>Uml Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TraceImpl extends MinimalEObjectImpl.Container implements Trace {
    /**
     * The cached value of the '{@link #getDtUMLElement() <em>Dt UML Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDtUMLElement()
     * @generated
     * @ordered
     */
    protected EObject dtUMLElement;

    /**
     * The cached value of the '{@link #getUmlElement() <em>Uml Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUmlElement()
     * @generated
     * @ordered
     */
    protected EObject umlElement;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TraceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TracemodelPackage.Literals.TRACE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getDtUMLElement() {
        if (dtUMLElement != null && dtUMLElement.eIsProxy()) {
            InternalEObject oldDtUMLElement = (InternalEObject)dtUMLElement;
            dtUMLElement = eResolveProxy(oldDtUMLElement);
            if (dtUMLElement != oldDtUMLElement) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TracemodelPackage.TRACE__DT_UML_ELEMENT, oldDtUMLElement, dtUMLElement));
            }
        }
        return dtUMLElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetDtUMLElement() {
        return dtUMLElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDtUMLElement(EObject newDtUMLElement) {
        EObject oldDtUMLElement = dtUMLElement;
        dtUMLElement = newDtUMLElement;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TracemodelPackage.TRACE__DT_UML_ELEMENT, oldDtUMLElement, dtUMLElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getUmlElement() {
        if (umlElement != null && umlElement.eIsProxy()) {
            InternalEObject oldUmlElement = (InternalEObject)umlElement;
            umlElement = eResolveProxy(oldUmlElement);
            if (umlElement != oldUmlElement) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TracemodelPackage.TRACE__UML_ELEMENT, oldUmlElement, umlElement));
            }
        }
        return umlElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetUmlElement() {
        return umlElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUmlElement(EObject newUmlElement) {
        EObject oldUmlElement = umlElement;
        umlElement = newUmlElement;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TracemodelPackage.TRACE__UML_ELEMENT, oldUmlElement, umlElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case TracemodelPackage.TRACE__DT_UML_ELEMENT:
                if (resolve) return getDtUMLElement();
                return basicGetDtUMLElement();
            case TracemodelPackage.TRACE__UML_ELEMENT:
                if (resolve) return getUmlElement();
                return basicGetUmlElement();
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
            case TracemodelPackage.TRACE__DT_UML_ELEMENT:
                setDtUMLElement((EObject)newValue);
                return;
            case TracemodelPackage.TRACE__UML_ELEMENT:
                setUmlElement((EObject)newValue);
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
            case TracemodelPackage.TRACE__DT_UML_ELEMENT:
                setDtUMLElement((EObject)null);
                return;
            case TracemodelPackage.TRACE__UML_ELEMENT:
                setUmlElement((EObject)null);
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
            case TracemodelPackage.TRACE__DT_UML_ELEMENT:
                return dtUMLElement != null;
            case TracemodelPackage.TRACE__UML_ELEMENT:
                return umlElement != null;
        }
        return super.eIsSet(featureID);
    }

} //TraceImpl
