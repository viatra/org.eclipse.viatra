/**
 */
package targetModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see targetModel.TargetModelFactory
 * @model kind="package"
 * @generated
 */
public interface TargetModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "targetModel";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.incquerylabs.com/uml/targetModel";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "targetModel";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TargetModelPackage eINSTANCE = targetModel.impl.TargetModelPackageImpl.init();

    /**
     * The meta object id for the '{@link targetModel.impl.TModelImpl <em>TModel</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see targetModel.impl.TModelImpl
     * @see targetModel.impl.TargetModelPackageImpl#getTModel()
     * @generated
     */
    int TMODEL = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TMODEL__NAME = 0;

    /**
     * The feature id for the '<em><b>Classes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TMODEL__CLASSES = 1;

    /**
     * The number of structural features of the '<em>TModel</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TMODEL_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>TModel</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TMODEL_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link targetModel.impl.TClassImpl <em>TClass</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see targetModel.impl.TClassImpl
     * @see targetModel.impl.TargetModelPackageImpl#getTClass()
     * @generated
     */
    int TCLASS = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS__NAME = 0;

    /**
     * The feature id for the '<em><b>Super Class</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS__SUPER_CLASS = 1;

    /**
     * The feature id for the '<em><b>Super Interfaces</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS__SUPER_INTERFACES = 2;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS__PROPERTIES = 3;

    /**
     * The feature id for the '<em><b>Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS__OPERATIONS = 4;

    /**
     * The number of structural features of the '<em>TClass</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>TClass</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TCLASS_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link targetModel.impl.TPropertyImpl <em>TProperty</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see targetModel.impl.TPropertyImpl
     * @see targetModel.impl.TargetModelPackageImpl#getTProperty()
     * @generated
     */
    int TPROPERTY = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TPROPERTY__NAME = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TPROPERTY__TYPE = 1;

    /**
     * The number of structural features of the '<em>TProperty</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TPROPERTY_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>TProperty</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TPROPERTY_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link targetModel.impl.TOperationImpl <em>TOperation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see targetModel.impl.TOperationImpl
     * @see targetModel.impl.TargetModelPackageImpl#getTOperation()
     * @generated
     */
    int TOPERATION = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TOPERATION__NAME = 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TOPERATION__TYPE = 1;

    /**
     * The number of structural features of the '<em>TOperation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TOPERATION_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>TOperation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TOPERATION_OPERATION_COUNT = 0;


    /**
     * Returns the meta object for class '{@link targetModel.TModel <em>TModel</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TModel</em>'.
     * @see targetModel.TModel
     * @generated
     */
    EClass getTModel();

    /**
     * Returns the meta object for the attribute '{@link targetModel.TModel#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see targetModel.TModel#getName()
     * @see #getTModel()
     * @generated
     */
    EAttribute getTModel_Name();

    /**
     * Returns the meta object for the containment reference list '{@link targetModel.TModel#getClasses <em>Classes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Classes</em>'.
     * @see targetModel.TModel#getClasses()
     * @see #getTModel()
     * @generated
     */
    EReference getTModel_Classes();

    /**
     * Returns the meta object for class '{@link targetModel.TClass <em>TClass</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TClass</em>'.
     * @see targetModel.TClass
     * @generated
     */
    EClass getTClass();

    /**
     * Returns the meta object for the attribute '{@link targetModel.TClass#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see targetModel.TClass#getName()
     * @see #getTClass()
     * @generated
     */
    EAttribute getTClass_Name();

    /**
     * Returns the meta object for the reference '{@link targetModel.TClass#getSuperClass <em>Super Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Super Class</em>'.
     * @see targetModel.TClass#getSuperClass()
     * @see #getTClass()
     * @generated
     */
    EReference getTClass_SuperClass();

    /**
     * Returns the meta object for the reference list '{@link targetModel.TClass#getSuperInterfaces <em>Super Interfaces</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Super Interfaces</em>'.
     * @see targetModel.TClass#getSuperInterfaces()
     * @see #getTClass()
     * @generated
     */
    EReference getTClass_SuperInterfaces();

    /**
     * Returns the meta object for the containment reference list '{@link targetModel.TClass#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Properties</em>'.
     * @see targetModel.TClass#getProperties()
     * @see #getTClass()
     * @generated
     */
    EReference getTClass_Properties();

    /**
     * Returns the meta object for the containment reference list '{@link targetModel.TClass#getOperations <em>Operations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Operations</em>'.
     * @see targetModel.TClass#getOperations()
     * @see #getTClass()
     * @generated
     */
    EReference getTClass_Operations();

    /**
     * Returns the meta object for class '{@link targetModel.TProperty <em>TProperty</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TProperty</em>'.
     * @see targetModel.TProperty
     * @generated
     */
    EClass getTProperty();

    /**
     * Returns the meta object for the attribute '{@link targetModel.TProperty#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see targetModel.TProperty#getName()
     * @see #getTProperty()
     * @generated
     */
    EAttribute getTProperty_Name();

    /**
     * Returns the meta object for the reference '{@link targetModel.TProperty#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see targetModel.TProperty#getType()
     * @see #getTProperty()
     * @generated
     */
    EReference getTProperty_Type();

    /**
     * Returns the meta object for class '{@link targetModel.TOperation <em>TOperation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TOperation</em>'.
     * @see targetModel.TOperation
     * @generated
     */
    EClass getTOperation();

    /**
     * Returns the meta object for the attribute '{@link targetModel.TOperation#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see targetModel.TOperation#getName()
     * @see #getTOperation()
     * @generated
     */
    EAttribute getTOperation_Name();

    /**
     * Returns the meta object for the reference '{@link targetModel.TOperation#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see targetModel.TOperation#getType()
     * @see #getTOperation()
     * @generated
     */
    EReference getTOperation_Type();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    TargetModelFactory getTargetModelFactory();

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
         * The meta object literal for the '{@link targetModel.impl.TModelImpl <em>TModel</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see targetModel.impl.TModelImpl
         * @see targetModel.impl.TargetModelPackageImpl#getTModel()
         * @generated
         */
        EClass TMODEL = eINSTANCE.getTModel();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TMODEL__NAME = eINSTANCE.getTModel_Name();

        /**
         * The meta object literal for the '<em><b>Classes</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TMODEL__CLASSES = eINSTANCE.getTModel_Classes();

        /**
         * The meta object literal for the '{@link targetModel.impl.TClassImpl <em>TClass</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see targetModel.impl.TClassImpl
         * @see targetModel.impl.TargetModelPackageImpl#getTClass()
         * @generated
         */
        EClass TCLASS = eINSTANCE.getTClass();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TCLASS__NAME = eINSTANCE.getTClass_Name();

        /**
         * The meta object literal for the '<em><b>Super Class</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TCLASS__SUPER_CLASS = eINSTANCE.getTClass_SuperClass();

        /**
         * The meta object literal for the '<em><b>Super Interfaces</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TCLASS__SUPER_INTERFACES = eINSTANCE.getTClass_SuperInterfaces();

        /**
         * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TCLASS__PROPERTIES = eINSTANCE.getTClass_Properties();

        /**
         * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TCLASS__OPERATIONS = eINSTANCE.getTClass_Operations();

        /**
         * The meta object literal for the '{@link targetModel.impl.TPropertyImpl <em>TProperty</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see targetModel.impl.TPropertyImpl
         * @see targetModel.impl.TargetModelPackageImpl#getTProperty()
         * @generated
         */
        EClass TPROPERTY = eINSTANCE.getTProperty();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TPROPERTY__NAME = eINSTANCE.getTProperty_Name();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TPROPERTY__TYPE = eINSTANCE.getTProperty_Type();

        /**
         * The meta object literal for the '{@link targetModel.impl.TOperationImpl <em>TOperation</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see targetModel.impl.TOperationImpl
         * @see targetModel.impl.TargetModelPackageImpl#getTOperation()
         * @generated
         */
        EClass TOPERATION = eINSTANCE.getTOperation();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TOPERATION__NAME = eINSTANCE.getTOperation_Name();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TOPERATION__TYPE = eINSTANCE.getTOperation_Type();

    }

} //TargetModelPackage
