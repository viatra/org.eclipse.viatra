/**
 */
package tracemodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trace Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tracemodel.TraceRoot#getTrace <em>Trace</em>}</li>
 * </ul>
 *
 * @see tracemodel.TracemodelPackage#getTraceRoot()
 * @model
 * @generated
 */
public interface TraceRoot extends EObject {
    /**
     * Returns the value of the '<em><b>Trace</b></em>' containment reference list.
     * The list contents are of type {@link tracemodel.Trace}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Trace</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Trace</em>' containment reference list.
     * @see tracemodel.TracemodelPackage#getTraceRoot_Trace()
     * @model containment="true"
     * @generated
     */
    EList<Trace> getTrace();

} // TraceRoot
