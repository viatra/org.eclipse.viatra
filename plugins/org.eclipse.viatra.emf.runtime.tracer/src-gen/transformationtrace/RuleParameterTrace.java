/**
 */
package transformationtrace;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule Parameter Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.RuleParameterTrace#getParameterName <em>Parameter Name</em>}</li>
 *   <li>{@link transformationtrace.RuleParameterTrace#getObjectId <em>Object Id</em>}</li>
 * </ul>
 *
 * @see transformationtrace.TransformationtracePackage#getRuleParameterTrace()
 * @model
 * @generated
 */
public interface RuleParameterTrace extends EObject {
    /**
     * Returns the value of the '<em><b>Parameter Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameter Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameter Name</em>' attribute.
     * @see #setParameterName(String)
     * @see transformationtrace.TransformationtracePackage#getRuleParameterTrace_ParameterName()
     * @model required="true"
     * @generated
     */
    String getParameterName();

    /**
     * Sets the value of the '{@link transformationtrace.RuleParameterTrace#getParameterName <em>Parameter Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parameter Name</em>' attribute.
     * @see #getParameterName()
     * @generated
     */
    void setParameterName(String value);

    /**
     * Returns the value of the '<em><b>Object Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Object Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Object Id</em>' attribute.
     * @see #setObjectId(String)
     * @see transformationtrace.TransformationtracePackage#getRuleParameterTrace_ObjectId()
     * @model required="true"
     * @generated
     */
    String getObjectId();

    /**
     * Sets the value of the '{@link transformationtrace.RuleParameterTrace#getObjectId <em>Object Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Object Id</em>' attribute.
     * @see #getObjectId()
     * @generated
     */
    void setObjectId(String value);

} // RuleParameterTrace
