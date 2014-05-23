/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Time</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getLength <em>Length</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator <em>Operator</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTime()
 * @model
 * @generated
 */
public interface Time extends EObject {
    /**
     * Returns the value of the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Length</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Length</em>' attribute.
     * @see #setLength(long)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTime_Length()
     * @model required="true"
     * @generated
     */
    long getLength();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getLength <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Length</em>' attribute.
     * @see #getLength()
     * @generated
     */
    void setLength(long value);

    /**
     * Returns the value of the '<em><b>Operator</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operator</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operator</em>' container reference.
     * @see #setOperator(TimingOperator)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTime_Operator()
     * @see org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime
     * @model opposite="time" required="true" transient="false"
     * @generated
     */
    TimingOperator getOperator();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator <em>Operator</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operator</em>' container reference.
     * @see #getOperator()
     * @generated
     */
    void setOperator(TimingOperator value);

} // Time
