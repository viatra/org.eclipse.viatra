/**
 */
package tracemodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tracemodel.Trace#getDtUMLElement <em>Dt UML Element</em>}</li>
 *   <li>{@link tracemodel.Trace#getUmlElement <em>Uml Element</em>}</li>
 * </ul>
 *
 * @see tracemodel.TracemodelPackage#getTrace()
 * @model
 * @generated
 */
public interface Trace extends EObject {
    /**
     * Returns the value of the '<em><b>Dt UML Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Dt UML Element</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Dt UML Element</em>' reference.
     * @see #setDtUMLElement(EObject)
     * @see tracemodel.TracemodelPackage#getTrace_DtUMLElement()
     * @model
     * @generated
     */
    EObject getDtUMLElement();

    /**
     * Sets the value of the '{@link tracemodel.Trace#getDtUMLElement <em>Dt UML Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Dt UML Element</em>' reference.
     * @see #getDtUMLElement()
     * @generated
     */
    void setDtUMLElement(EObject value);

    /**
     * Returns the value of the '<em><b>Uml Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uml Element</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Uml Element</em>' reference.
     * @see #setUmlElement(EObject)
     * @see tracemodel.TracemodelPackage#getTrace_UmlElement()
     * @model
     * @generated
     */
    EObject getUmlElement();

    /**
     * Sets the value of the '{@link tracemodel.Trace#getUmlElement <em>Uml Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uml Element</em>' reference.
     * @see #getUmlElement()
     * @generated
     */
    void setUmlElement(EObject value);

} // Trace
