/**
 */
package targetModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.uml2.uml.Interface;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>TClass</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link targetModel.TClass#getName <em>Name</em>}</li>
 *   <li>{@link targetModel.TClass#getSuperClass <em>Super Class</em>}</li>
 *   <li>{@link targetModel.TClass#getSuperInterfaces <em>Super Interfaces</em>}</li>
 *   <li>{@link targetModel.TClass#getProperties <em>Properties</em>}</li>
 *   <li>{@link targetModel.TClass#getOperations <em>Operations</em>}</li>
 * </ul>
 *
 * @see targetModel.TargetModelPackage#getTClass()
 * @model
 * @generated
 */
public interface TClass extends EObject {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see targetModel.TargetModelPackage#getTClass_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link targetModel.TClass#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Super Class</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Super Class</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Super Class</em>' reference.
     * @see #setSuperClass(org.eclipse.uml2.uml.Class)
     * @see targetModel.TargetModelPackage#getTClass_SuperClass()
     * @model
     * @generated
     */
    org.eclipse.uml2.uml.Class getSuperClass();

    /**
     * Sets the value of the '{@link targetModel.TClass#getSuperClass <em>Super Class</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Super Class</em>' reference.
     * @see #getSuperClass()
     * @generated
     */
    void setSuperClass(org.eclipse.uml2.uml.Class value);

    /**
     * Returns the value of the '<em><b>Super Interfaces</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.uml2.uml.Interface}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Super Interfaces</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Super Interfaces</em>' reference list.
     * @see targetModel.TargetModelPackage#getTClass_SuperInterfaces()
     * @model
     * @generated
     */
    EList<Interface> getSuperInterfaces();

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
     * The list contents are of type {@link targetModel.TProperty}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' containment reference list.
     * @see targetModel.TargetModelPackage#getTClass_Properties()
     * @model containment="true"
     * @generated
     */
    EList<TProperty> getProperties();

    /**
     * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
     * The list contents are of type {@link targetModel.TOperation}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operations</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operations</em>' containment reference list.
     * @see targetModel.TargetModelPackage#getTClass_Operations()
     * @model containment="true"
     * @generated
     */
    EList<TOperation> getOperations();

} // TClass
