/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Design Space</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getStates <em>States</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getTransitions <em>Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getRootStates <em>Root States</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getDesignSpace()
 * @model
 * @generated
 */
public interface DesignSpace extends EObject {
    /**
     * Returns the value of the '<em><b>States</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.dse.emf.designspace.State}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>States</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>States</em>' containment reference list.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getDesignSpace_States()
     * @model containment="true"
     * @generated
     */
    EList<State> getStates();

    /**
     * Returns the value of the '<em><b>Transitions</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.dse.emf.designspace.Transition}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Transitions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Transitions</em>' containment reference list.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getDesignSpace_Transitions()
     * @model containment="true"
     * @generated
     */
    EList<Transition> getTransitions();

    /**
     * Returns the value of the '<em><b>Root States</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.dse.emf.designspace.State}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Root States</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Root States</em>' reference list.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getDesignSpace_RootStates()
     * @model
     * @generated
     */
    EList<State> getRootStates();

} // DesignSpace
