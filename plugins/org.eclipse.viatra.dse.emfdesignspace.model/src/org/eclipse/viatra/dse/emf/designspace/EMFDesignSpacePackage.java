/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpaceFactory
 * @model kind="package"
 * @generated
 */
public interface EMFDesignSpacePackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "designspace";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://eclipse.org/viatra/emf/dse/designspace";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "emfdsp";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EMFDesignSpacePackage eINSTANCE = org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl <em>Design Space</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl
     * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getDesignSpace()
     * @generated
     */
    int DESIGN_SPACE = 0;

    /**
     * The feature id for the '<em><b>States</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DESIGN_SPACE__STATES = 0;

    /**
     * The feature id for the '<em><b>Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DESIGN_SPACE__TRANSITIONS = 1;

    /**
     * The feature id for the '<em><b>Root States</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DESIGN_SPACE__ROOT_STATES = 2;

    /**
     * The number of structural features of the '<em>Design Space</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DESIGN_SPACE_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Design Space</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DESIGN_SPACE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl <em>State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.dse.emf.designspace.impl.StateImpl
     * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getState()
     * @generated
     */
    int STATE = 1;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__ID = 0;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__STATE = 1;

    /**
     * The feature id for the '<em><b>Out Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__OUT_TRANSITIONS = 2;

    /**
     * The feature id for the '<em><b>In Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__IN_TRANSITIONS = 3;

    /**
     * The feature id for the '<em><b>Threadsafe Facade</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__THREADSAFE_FACADE = 4;

    /**
     * The number of structural features of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl <em>Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl
     * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getTransition()
     * @generated
     */
    int TRANSITION = 2;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__ID = 0;

    /**
     * The feature id for the '<em><b>Fired From</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__FIRED_FROM = 1;

    /**
     * The feature id for the '<em><b>Results In</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__RESULTS_IN = 2;

    /**
     * The feature id for the '<em><b>Rule Data</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__RULE_DATA = 3;

    /**
     * The feature id for the '<em><b>Threadsafe Facade</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__THREADSAFE_FACADE = 4;

    /**
     * The number of structural features of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState <em>EMF Internal Traversal State</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState
     * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getEMFInternalTraversalState()
     * @generated
     */
    int EMF_INTERNAL_TRAVERSAL_STATE = 3;

    /**
     * The meta object id for the '<em>POJO</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Object
     * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getPOJO()
     * @generated
     */
    int POJO = 4;


    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace <em>Design Space</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Design Space</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.DesignSpace
     * @generated
     */
    EClass getDesignSpace();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getStates <em>States</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>States</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.DesignSpace#getStates()
     * @see #getDesignSpace()
     * @generated
     */
    EReference getDesignSpace_States();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getTransitions <em>Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Transitions</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.DesignSpace#getTransitions()
     * @see #getDesignSpace()
     * @generated
     */
    EReference getDesignSpace_Transitions();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.dse.emf.designspace.DesignSpace#getRootStates <em>Root States</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Root States</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.DesignSpace#getRootStates()
     * @see #getDesignSpace()
     * @generated
     */
    EReference getDesignSpace_RootStates();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.dse.emf.designspace.State <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>State</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State
     * @generated
     */
    EClass getState();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.State#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State#getId()
     * @see #getState()
     * @generated
     */
    EAttribute getState_Id();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.State#getState <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>State</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State#getState()
     * @see #getState()
     * @generated
     */
    EAttribute getState_State();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.dse.emf.designspace.State#getOutTransitions <em>Out Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Out Transitions</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State#getOutTransitions()
     * @see #getState()
     * @generated
     */
    EReference getState_OutTransitions();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.dse.emf.designspace.State#getInTransitions <em>In Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>In Transitions</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State#getInTransitions()
     * @see #getState()
     * @generated
     */
    EReference getState_InTransitions();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.State#getThreadsafeFacade <em>Threadsafe Facade</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Threadsafe Facade</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.State#getThreadsafeFacade()
     * @see #getState()
     * @generated
     */
    EAttribute getState_ThreadsafeFacade();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.dse.emf.designspace.Transition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Transition</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition
     * @generated
     */
    EClass getTransition();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getId()
     * @see #getTransition()
     * @generated
     */
    EAttribute getTransition_Id();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom <em>Fired From</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Fired From</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getFiredFrom()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_FiredFrom();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn <em>Results In</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Results In</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getResultsIn()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_ResultsIn();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getRuleData <em>Rule Data</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Rule Data</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getRuleData()
     * @see #getTransition()
     * @generated
     */
    EAttribute getTransition_RuleData();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.dse.emf.designspace.Transition#getThreadsafeFacade <em>Threadsafe Facade</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Threadsafe Facade</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.Transition#getThreadsafeFacade()
     * @see #getTransition()
     * @generated
     */
    EAttribute getTransition_ThreadsafeFacade();

    /**
     * Returns the meta object for enum '{@link org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState <em>EMF Internal Traversal State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>EMF Internal Traversal State</em>'.
     * @see org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState
     * @generated
     */
    EEnum getEMFInternalTraversalState();

    /**
     * Returns the meta object for data type '{@link java.lang.Object <em>POJO</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>POJO</em>'.
     * @see java.lang.Object
     * @model instanceClass="java.lang.Object"
     * @generated
     */
    EDataType getPOJO();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    EMFDesignSpaceFactory getEMFDesignSpaceFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl <em>Design Space</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.dse.emf.designspace.impl.DesignSpaceImpl
         * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getDesignSpace()
         * @generated
         */
        EClass DESIGN_SPACE = eINSTANCE.getDesignSpace();

        /**
         * The meta object literal for the '<em><b>States</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DESIGN_SPACE__STATES = eINSTANCE.getDesignSpace_States();

        /**
         * The meta object literal for the '<em><b>Transitions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DESIGN_SPACE__TRANSITIONS = eINSTANCE.getDesignSpace_Transitions();

        /**
         * The meta object literal for the '<em><b>Root States</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DESIGN_SPACE__ROOT_STATES = eINSTANCE.getDesignSpace_RootStates();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.StateImpl <em>State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.dse.emf.designspace.impl.StateImpl
         * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getState()
         * @generated
         */
        EClass STATE = eINSTANCE.getState();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATE__ID = eINSTANCE.getState_Id();

        /**
         * The meta object literal for the '<em><b>State</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATE__STATE = eINSTANCE.getState_State();

        /**
         * The meta object literal for the '<em><b>Out Transitions</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__OUT_TRANSITIONS = eINSTANCE.getState_OutTransitions();

        /**
         * The meta object literal for the '<em><b>In Transitions</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__IN_TRANSITIONS = eINSTANCE.getState_InTransitions();

        /**
         * The meta object literal for the '<em><b>Threadsafe Facade</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATE__THREADSAFE_FACADE = eINSTANCE.getState_ThreadsafeFacade();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl <em>Transition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.dse.emf.designspace.impl.TransitionImpl
         * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getTransition()
         * @generated
         */
        EClass TRANSITION = eINSTANCE.getTransition();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TRANSITION__ID = eINSTANCE.getTransition_Id();

        /**
         * The meta object literal for the '<em><b>Fired From</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__FIRED_FROM = eINSTANCE.getTransition_FiredFrom();

        /**
         * The meta object literal for the '<em><b>Results In</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__RESULTS_IN = eINSTANCE.getTransition_ResultsIn();

        /**
         * The meta object literal for the '<em><b>Rule Data</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TRANSITION__RULE_DATA = eINSTANCE.getTransition_RuleData();

        /**
         * The meta object literal for the '<em><b>Threadsafe Facade</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TRANSITION__THREADSAFE_FACADE = eINSTANCE.getTransition_ThreadsafeFacade();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState <em>EMF Internal Traversal State</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState
         * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getEMFInternalTraversalState()
         * @generated
         */
        EEnum EMF_INTERNAL_TRAVERSAL_STATE = eINSTANCE.getEMFInternalTraversalState();

        /**
         * The meta object literal for the '<em>POJO</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.Object
         * @see org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpacePackageImpl#getPOJO()
         * @generated
         */
        EDataType POJO = eINSTANCE.getPOJO();

    }

} //EMFDesignSpacePackage
