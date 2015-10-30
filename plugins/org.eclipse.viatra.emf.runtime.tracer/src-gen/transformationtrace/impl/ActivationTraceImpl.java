/**
 */
package transformationtrace.impl;

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

import transformationtrace.ActivationTrace;
import transformationtrace.RuleParameterTrace;
import transformationtrace.TransformationtracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Activation Trace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.impl.ActivationTraceImpl#getRuleName <em>Rule Name</em>}</li>
 *   <li>{@link transformationtrace.impl.ActivationTraceImpl#getRuleParameterTraces <em>Rule Parameter Traces</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActivationTraceImpl extends MinimalEObjectImpl.Container implements ActivationTrace {
    /**
     * The default value of the '{@link #getRuleName() <em>Rule Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRuleName()
     * @generated
     * @ordered
     */
    protected static final String RULE_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRuleName() <em>Rule Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRuleName()
     * @generated
     * @ordered
     */
    protected String ruleName = RULE_NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getRuleParameterTraces() <em>Rule Parameter Traces</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRuleParameterTraces()
     * @generated
     * @ordered
     */
    protected EList<RuleParameterTrace> ruleParameterTraces;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ActivationTraceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationtracePackage.Literals.ACTIVATION_TRACE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRuleName(String newRuleName) {
        String oldRuleName = ruleName;
        ruleName = newRuleName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationtracePackage.ACTIVATION_TRACE__RULE_NAME, oldRuleName, ruleName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<RuleParameterTrace> getRuleParameterTraces() {
        if (ruleParameterTraces == null) {
            ruleParameterTraces = new EObjectContainmentEList<RuleParameterTrace>(RuleParameterTrace.class, this, TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES);
        }
        return ruleParameterTraces;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES:
                return ((InternalEList<?>)getRuleParameterTraces()).basicRemove(otherEnd, msgs);
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
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_NAME:
                return getRuleName();
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES:
                return getRuleParameterTraces();
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
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_NAME:
                setRuleName((String)newValue);
                return;
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES:
                getRuleParameterTraces().clear();
                getRuleParameterTraces().addAll((Collection<? extends RuleParameterTrace>)newValue);
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
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_NAME:
                setRuleName(RULE_NAME_EDEFAULT);
                return;
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES:
                getRuleParameterTraces().clear();
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
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_NAME:
                return RULE_NAME_EDEFAULT == null ? ruleName != null : !RULE_NAME_EDEFAULT.equals(ruleName);
            case TransformationtracePackage.ACTIVATION_TRACE__RULE_PARAMETER_TRACES:
                return ruleParameterTraces != null && !ruleParameterTraces.isEmpty();
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
        result.append(" (ruleName: ");
        result.append(ruleName);
        result.append(')');
        return result.toString();
    }

} //ActivationTraceImpl
