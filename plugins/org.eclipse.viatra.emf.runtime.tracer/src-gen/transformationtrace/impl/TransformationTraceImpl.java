/**
 */
package transformationtrace.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import transformationtrace.ActivationTrace;
import transformationtrace.TransformationTrace;
import transformationtrace.TransformationtracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transformation Trace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.impl.TransformationTraceImpl#getActivationTraces <em>Activation Traces</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransformationTraceImpl extends MinimalEObjectImpl.Container implements TransformationTrace {
    /**
     * The cached value of the '{@link #getActivationTraces() <em>Activation Traces</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getActivationTraces()
     * @generated
     * @ordered
     */
    protected EList<ActivationTrace> activationTraces;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TransformationTraceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationtracePackage.Literals.TRANSFORMATION_TRACE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ActivationTrace> getActivationTraces() {
        if (activationTraces == null) {
            activationTraces = new EObjectContainmentEList<ActivationTrace>(ActivationTrace.class, this, TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES);
        }
        return activationTraces;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES:
                return ((InternalEList<?>)getActivationTraces()).basicRemove(otherEnd, msgs);
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
            case TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES:
                return getActivationTraces();
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
            case TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES:
                getActivationTraces().clear();
                getActivationTraces().addAll((Collection<? extends ActivationTrace>)newValue);
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
            case TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES:
                getActivationTraces().clear();
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
            case TransformationtracePackage.TRANSFORMATION_TRACE__ACTIVATION_TRACES:
                return activationTraces != null && !activationTraces.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //TransformationTraceImpl
