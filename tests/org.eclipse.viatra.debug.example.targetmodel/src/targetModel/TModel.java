/**
 */
package targetModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>TModel</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link targetModel.TModel#getName <em>Name</em>}</li>
 *   <li>{@link targetModel.TModel#getClasses <em>Classes</em>}</li>
 * </ul>
 *
 * @see targetModel.TargetModelPackage#getTModel()
 * @model
 * @generated
 */
public interface TModel extends EObject {
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
     * @see targetModel.TargetModelPackage#getTModel_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link targetModel.TModel#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Classes</b></em>' containment reference list.
     * The list contents are of type {@link targetModel.TClass}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Classes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Classes</em>' containment reference list.
     * @see targetModel.TargetModelPackage#getTModel_Classes()
     * @model containment="true"
     * @generated
     */
    EList<TClass> getClasses();

} // TModel
