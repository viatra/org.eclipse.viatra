/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transition</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.Transition#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom <em>Fired From</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn <em>Results In</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.Transition#getRuleData <em>Rule Data</em>}</li>
 *   <li>{@link org.eclipse.viatra.dse.emf.designspace.Transition#getThreadsafeFacade <em>Threadsafe Facade</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition()
 * @model
 * @generated
 */
public interface Transition extends EObject {
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
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition_Id()
     * @model dataType="org.eclipse.viatra.dse.emf.designspace.POJO"
     * @generated
     */
    Object getId();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId(Object value);

    /**
     * Returns the value of the '<em><b>Fired From</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.dse.emf.designspace.State#getOutTransitions <em>Out Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Fired From</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Fired From</em>' reference.
     * @see #setFiredFrom(State)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition_FiredFrom()
     * @see org.eclipse.viatra.dse.emf.designspace.State#getOutTransitions
     * @model opposite="outTransitions" required="true"
     * @generated
     */
    State getFiredFrom();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom <em>Fired From</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Fired From</em>' reference.
     * @see #getFiredFrom()
     * @generated
     */
    void setFiredFrom(State value);

    /**
     * Returns the value of the '<em><b>Results In</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.viatra.dse.emf.designspace.State#getInTransitions <em>In Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Results In</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Results In</em>' reference.
     * @see #setResultsIn(State)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition_ResultsIn()
     * @see org.eclipse.viatra.dse.emf.designspace.State#getInTransitions
     * @model opposite="inTransitions"
     * @generated
     */
    State getResultsIn();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn <em>Results In</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Results In</em>' reference.
     * @see #getResultsIn()
     * @generated
     */
    void setResultsIn(State value);

    /**
     * Returns the value of the '<em><b>Rule Data</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule Data</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule Data</em>' attribute.
     * @see #setRuleData(Object)
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition_RuleData()
     * @model dataType="org.eclipse.viatra.dse.emf.designspace.POJO" transient="true"
     * @generated
     */
    Object getRuleData();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getRuleData <em>Rule Data</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rule Data</em>' attribute.
     * @see #getRuleData()
     * @generated
     */
    void setRuleData(Object value);

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
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getTransition_ThreadsafeFacade()
     * @model dataType="org.eclipse.viatra.dse.emf.designspace.POJO" transient="true"
     * @generated
     */
    Object getThreadsafeFacade();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getThreadsafeFacade <em>Threadsafe Facade</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Threadsafe Facade</em>' attribute.
     * @see #getThreadsafeFacade()
     * @generated
     */
    void setThreadsafeFacade(Object value);

} // Transition
