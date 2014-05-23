/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timing Operator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime <em>Time</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator <em>Operator</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTimingOperator()
 * @model abstract="true"
 * @generated
 */
public interface TimingOperator extends ComplexEventOperator {
    /**
     * Returns the value of the '<em><b>Time</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time</em>' containment reference.
     * @see #setTime(Time)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTimingOperator_Time()
     * @see org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator
     * @model opposite="operator" containment="true" required="true"
     * @generated
     */
    Time getTime();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime <em>Time</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time</em>' containment reference.
     * @see #getTime()
     * @generated
     */
    void setTime(Time value);

    /**
     * Returns the value of the '<em><b>Operator</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming <em>Timing</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operator</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operator</em>' container reference.
     * @see #setOperator(LogicalOperator)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTimingOperator_Operator()
     * @see org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming
     * @model opposite="timing" required="true" transient="false"
     * @generated
     */
    LogicalOperator getOperator();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator <em>Operator</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operator</em>' container reference.
     * @see #getOperator()
     * @generated
     */
    void setOperator(LogicalOperator value);

} // TimingOperator
