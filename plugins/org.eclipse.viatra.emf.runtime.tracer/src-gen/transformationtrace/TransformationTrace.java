/**
 */
package transformationtrace;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transformation Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link transformationtrace.TransformationTrace#getActivationTraces <em>Activation Traces</em>}</li>
 * </ul>
 *
 * @see transformationtrace.TransformationtracePackage#getTransformationTrace()
 * @model
 * @generated
 */
public interface TransformationTrace extends EObject {
    /**
     * Returns the value of the '<em><b>Activation Traces</b></em>' containment reference list.
     * The list contents are of type {@link transformationtrace.ActivationTrace}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Activation Traces</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Activation Traces</em>' containment reference list.
     * @see transformationtrace.TransformationtracePackage#getTransformationTrace_ActivationTraces()
     * @model containment="true"
     * @generated
     */
    EList<ActivationTrace> getActivationTraces();

} // TransformationTrace
