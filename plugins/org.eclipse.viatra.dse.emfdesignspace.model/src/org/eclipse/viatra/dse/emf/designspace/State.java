/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>State</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.State#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.State#getState <em>State</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.State#getOutTransitions <em>Out Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.State#getInTransitions <em>In Transitions</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.State#getThreadsafeFacade <em>Threadsafe Facade</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState()
 * @model
 * @generated
 */
public interface State extends EObject {
    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(Object)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState_Id()
     * @model dataType="org.eclipse.viatra.dse.emf.designspace.POJO"
     * @generated
     */
    Object getId();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.State#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId(Object value);

    /**
     * Returns the value of the '<em><b>State</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>State</em>' attribute.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState
     * @see #setState(EMFInternalTraversalState)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState_State()
     * @model
     * @generated
     */
    EMFInternalTraversalState getState();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.State#getState <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>State</em>' attribute.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState
     * @see #getState()
     * @generated
     */
    void setState(EMFInternalTraversalState value);

    /**
     * Returns the value of the '<em><b>Out Transitions</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.dse.emf.designspace.Transition}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom <em>Fired From</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out Transitions</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out Transitions</em>' reference list.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState_OutTransitions()
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom
     * @model opposite="firedFrom"
     * @generated
     */
    EList<Transition> getOutTransitions();

    /**
     * Returns the value of the '<em><b>In Transitions</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.viatra.dse.emf.designspace.Transition}.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn <em>Results In</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In Transitions</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In Transitions</em>' reference list.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState_InTransitions()
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn
     * @model opposite="resultsIn"
     * @generated
     */
    EList<Transition> getInTransitions();

    /**
     * Returns the value of the '<em><b>Threadsafe Facade</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Threadsafe Facade</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Threadsafe Facade</em>' attribute.
     * @see #setThreadsafeFacade(Object)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getState_ThreadsafeFacade()
     * @model dataType="org.eclipse.viatra.dse.emf.designspace.POJO" transient="true"
     * @generated
     */
    Object getThreadsafeFacade();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.State#getThreadsafeFacade <em>Threadsafe Facade</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Threadsafe Facade</em>' attribute.
     * @see #getThreadsafeFacade()
     * @generated
     */
    void setThreadsafeFacade(Object value);

} // State
