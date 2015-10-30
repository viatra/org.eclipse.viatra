/**
 */
package transformationtrace.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import transformationtrace.RuleParameterTrace;
import transformationtrace.TransformationtracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rule Parameter Trace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.impl.RuleParameterTraceImpl#getParameterName <em>Parameter Name</em>}</li>
 *   <li>{@link transformationtrace.impl.RuleParameterTraceImpl#getObjectId <em>Object Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RuleParameterTraceImpl extends MinimalEObjectImpl.Container implements RuleParameterTrace {
    /**
     * The default value of the '{@link #getParameterName() <em>Parameter Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameterName()
     * @generated
     * @ordered
     */
    protected static final String PARAMETER_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getParameterName() <em>Parameter Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParameterName()
     * @generated
     * @ordered
     */
    protected String parameterName = PARAMETER_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getObjectId() <em>Object Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getObjectId()
     * @generated
     * @ordered
     */
    protected static final String OBJECT_ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getObjectId() <em>Object Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getObjectId()
     * @generated
     * @ordered
     */
    protected String objectId = OBJECT_ID_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RuleParameterTraceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationtracePackage.Literals.RULE_PARAMETER_TRACE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParameterName(String newParameterName) {
        String oldParameterName = parameterName;
        parameterName = newParameterName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationtracePackage.RULE_PARAMETER_TRACE__PARAMETER_NAME, oldParameterName, parameterName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setObjectId(String newObjectId) {
        String oldObjectId = objectId;
        objectId = newObjectId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationtracePackage.RULE_PARAMETER_TRACE__OBJECT_ID, oldObjectId, objectId));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case TransformationtracePackage.RULE_PARAMETER_TRACE__PARAMETER_NAME:
                return getParameterName();
            case TransformationtracePackage.RULE_PARAMETER_TRACE__OBJECT_ID:
                return getObjectId();
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
            case TransformationtracePackage.RULE_PARAMETER_TRACE__PARAMETER_NAME:
                setParameterName((String)newValue);
                return;
            case TransformationtracePackage.RULE_PARAMETER_TRACE__OBJECT_ID:
                setObjectId((String)newValue);
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
            case TransformationtracePackage.RULE_PARAMETER_TRACE__PARAMETER_NAME:
                setParameterName(PARAMETER_NAME_EDEFAULT);
                return;
            case TransformationtracePackage.RULE_PARAMETER_TRACE__OBJECT_ID:
                setObjectId(OBJECT_ID_EDEFAULT);
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
            case TransformationtracePackage.RULE_PARAMETER_TRACE__PARAMETER_NAME:
                return PARAMETER_NAME_EDEFAULT == null ? parameterName != null : !PARAMETER_NAME_EDEFAULT.equals(parameterName);
            case TransformationtracePackage.RULE_PARAMETER_TRACE__OBJECT_ID:
                return OBJECT_ID_EDEFAULT == null ? objectId != null : !OBJECT_ID_EDEFAULT.equals(objectId);
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
        result.append(" (parameterName: ");
        result.append(parameterName);
        result.append(", objectId: ");
        result.append(objectId);
        result.append(')');
        return result.toString();
    }

} //RuleParameterTraceImpl
