/**
 */
package targetModel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import targetModel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TargetModelFactoryImpl extends EFactoryImpl implements TargetModelFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TargetModelFactory init() {
        try {
            TargetModelFactory theTargetModelFactory = (TargetModelFactory)EPackage.Registry.INSTANCE.getEFactory(TargetModelPackage.eNS_URI);
            if (theTargetModelFactory != null) {
                return theTargetModelFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new TargetModelFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TargetModelFactoryImpl() {
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
            case TargetModelPackage.TMODEL: return createTModel();
            case TargetModelPackage.TCLASS: return createTClass();
            case TargetModelPackage.TPROPERTY: return createTProperty();
            case TargetModelPackage.TOPERATION: return createTOperation();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TModel createTModel() {
        TModelImpl tModel = new TModelImpl();
        return tModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TClass createTClass() {
        TClassImpl tClass = new TClassImpl();
        return tClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TProperty createTProperty() {
        TPropertyImpl tProperty = new TPropertyImpl();
        return tProperty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TOperation createTOperation() {
        TOperationImpl tOperation = new TOperationImpl();
        return tOperation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TargetModelPackage getTargetModelPackage() {
        return (TargetModelPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static TargetModelPackage getPackage() {
        return TargetModelPackage.eINSTANCE;
    }

} //TargetModelFactoryImpl
