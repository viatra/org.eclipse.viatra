/**
 */
package org.eclipse.viatra.dse.emf.designspace.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra.dse.emf.designspace.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EMFDesignSpaceFactoryImpl extends EFactoryImpl implements EMFDesignSpaceFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EMFDesignSpaceFactory init() {
        try {
            EMFDesignSpaceFactory theEMFDesignSpaceFactory = (EMFDesignSpaceFactory)EPackage.Registry.INSTANCE.getEFactory(EMFDesignSpacePackage.eNS_URI);
            if (theEMFDesignSpaceFactory != null) {
                return theEMFDesignSpaceFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new EMFDesignSpaceFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFDesignSpaceFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case EMFDesignSpacePackage.DESIGN_SPACE: return createDesignSpace();
            case EMFDesignSpacePackage.STATE: return createState();
            case EMFDesignSpacePackage.TRANSITION: return createTransition();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case EMFDesignSpacePackage.EMF_INTERNAL_TRAVERSAL_STATE:
                return createEMFInternalTraversalStateFromString(eDataType, initialValue);
            case EMFDesignSpacePackage.POJO:
                return createPOJOFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case EMFDesignSpacePackage.EMF_INTERNAL_TRAVERSAL_STATE:
                return convertEMFInternalTraversalStateToString(eDataType, instanceValue);
            case EMFDesignSpacePackage.POJO:
                return convertPOJOToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DesignSpace createDesignSpace() {
        DesignSpaceImpl designSpace = new DesignSpaceImpl();
        return designSpace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State createState() {
        StateImpl state = new StateImpl();
        return state;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Transition createTransition() {
        TransitionImpl transition = new TransitionImpl();
        return transition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFInternalTraversalState createEMFInternalTraversalStateFromString(EDataType eDataType, String initialValue) {
        EMFInternalTraversalState result = EMFInternalTraversalState.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEMFInternalTraversalStateToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object createPOJOFromString(EDataType eDataType, String initialValue) {
        return super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertPOJOToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFDesignSpacePackage getEMFDesignSpacePackage() {
        return (EMFDesignSpacePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static EMFDesignSpacePackage getPackage() {
        return EMFDesignSpacePackage.eINSTANCE;
    }

} //EMFDesignSpaceFactoryImpl
