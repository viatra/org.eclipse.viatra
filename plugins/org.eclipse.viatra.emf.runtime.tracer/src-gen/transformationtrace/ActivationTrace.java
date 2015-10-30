/**
 */
package transformationtrace;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Activation Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.ActivationTrace#getRuleName <em>Rule Name</em>}</li>
 *   <li>{@link transformationtrace.ActivationTrace#getRuleParameterTraces <em>Rule Parameter Traces</em>}</li>
 * </ul>
 *
 * @see transformationtrace.TransformationtracePackage#getActivationTrace()
 * @model
 * @generated
 */
public interface ActivationTrace extends EObject {
    /**
     * Returns the value of the '<em><b>Rule Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule Name</em>' attribute.
     * @see #setRuleName(String)
     * @see transformationtrace.TransformationtracePackage#getActivationTrace_RuleName()
     * @model required="true"
     * @generated
     */
    String getRuleName();

    /**
     * Sets the value of the '{@link transformationtrace.ActivationTrace#getRuleName <em>Rule Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rule Name</em>' attribute.
     * @see #getRuleName()
     * @generated
     */
    void setRuleName(String value);

    /**
     * Returns the value of the '<em><b>Rule Parameter Traces</b></em>' containment reference list.
     * The list contents are of type {@link transformationtrace.RuleParameterTrace}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule Parameter Traces</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule Parameter Traces</em>' containment reference list.
     * @see transformationtrace.TransformationtracePackage#getActivationTrace_RuleParameterTraces()
     * @model containment="true"
     * @generated
     */
    EList<RuleParameterTrace> getRuleParameterTraces();

} // ActivationTrace
