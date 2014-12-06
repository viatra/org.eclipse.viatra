/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timewindow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.core.metamodels.events.Timewindow#getTime <em>Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTimewindow()
 * @model
 * @generated
 */
public interface Timewindow extends EObject {
    /**
     * Returns the value of the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time</em>' attribute.
     * @see #setTime(long)
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#getTimewindow_Time()
     * @model required="true"
     * @generated
     */
    long getTime();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.cep.core.metamodels.events.Timewindow#getTime <em>Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time</em>' attribute.
     * @see #getTime()
     * @generated
     */
    void setTime(long value);

} // Timewindow
