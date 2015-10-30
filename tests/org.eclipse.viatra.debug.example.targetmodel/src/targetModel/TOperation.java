/**
 */
package targetModel;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.uml2.uml.Type;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>TOperation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link targetModel.TOperation#getName <em>Name</em>}</li>
 *   <li>{@link targetModel.TOperation#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see targetModel.TargetModelPackage#getTOperation()
 * @model
 * @generated
 */
public interface TOperation extends EObject {
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
     * @see targetModel.TargetModelPackage#getTOperation_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link targetModel.TOperation#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' reference.
     * @see #setType(Type)
     * @see targetModel.TargetModelPackage#getTOperation_Type()
     * @model
     * @generated
     */
    Type getType();

    /**
     * Sets the value of the '{@link targetModel.TOperation#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' reference.
     * @see #getType()
     * @generated
     */
    void setType(Type value);

} // TOperation
