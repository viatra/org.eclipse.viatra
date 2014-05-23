/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Logical Operator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming <em>Timing</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getEventPattern <em>Event Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getLogicalOperator()
 * @model abstract="true"
 * @generated
 */
public interface LogicalOperator extends ComplexEventOperator {
    /**
     * Returns the value of the '<em><b>Timing</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timing</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Timing</em>' containment reference.
     * @see #setTiming(TimingOperator)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getLogicalOperator_Timing()
     * @see org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator
     * @model opposite="operator" containment="true"
     * @generated
     */
    TimingOperator getTiming();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming <em>Timing</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Timing</em>' containment reference.
     * @see #getTiming()
     * @generated
     */
    void setTiming(TimingOperator value);

    /**
     * Returns the value of the '<em><b>Event Pattern</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Event Pattern</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Event Pattern</em>' container reference.
     * @see #setEventPattern(ComplexEventPattern)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getLogicalOperator_EventPattern()
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator
     * @model opposite="operator" required="true" transient="false"
     * @generated
     */
    ComplexEventPattern getEventPattern();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getEventPattern <em>Event Pattern</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Event Pattern</em>' container reference.
     * @see #getEventPattern()
     * @generated
     */
    void setEventPattern(ComplexEventPattern value);

} // LogicalOperator
