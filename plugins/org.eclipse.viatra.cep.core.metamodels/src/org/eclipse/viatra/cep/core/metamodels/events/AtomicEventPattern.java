/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Atomic Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getAtomicEventPattern()
 * @model
 * @generated
 */
public interface AtomicEventPattern extends EventPattern {
    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getAtomicEventPattern_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

} // AtomicEventPattern
