/**
 */
package targetModel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.uml2.uml.Interface;

import targetModel.TClass;
import targetModel.TOperation;
import targetModel.TProperty;
import targetModel.TargetModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>TClass</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link targetModel.impl.TClassImpl#getName <em>Name</em>}</li>
 *   <li>{@link targetModel.impl.TClassImpl#getSuperClass <em>Super Class</em>}</li>
 *   <li>{@link targetModel.impl.TClassImpl#getSuperInterfaces <em>Super Interfaces</em>}</li>
 *   <li>{@link targetModel.impl.TClassImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link targetModel.impl.TClassImpl#getOperations <em>Operations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TClassImpl extends MinimalEObjectImpl.Container implements TClass {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getSuperClass() <em>Super Class</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSuperClass()
     * @generated
     * @ordered
     */
    protected org.eclipse.uml2.uml.Class superClass;

    /**
     * The cached value of the '{@link #getSuperInterfaces() <em>Super Interfaces</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSuperInterfaces()
     * @generated
     * @ordered
     */
    protected EList<Interface> superInterfaces;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProperties()
     * @generated
     * @ordered
     */
    protected EList<TProperty> properties;

    /**
     * The cached value of the '{@link #getOperations() <em>Operations</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOperations()
     * @generated
     * @ordered
     */
    protected EList<TOperation> operations;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TClassImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TargetModelPackage.Literals.TCLASS;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TargetModelPackage.TCLASS__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public org.eclipse.uml2.uml.Class getSuperClass() {
        if (superClass != null && superClass.eIsProxy()) {
            InternalEObject oldSuperClass = (InternalEObject)superClass;
            superClass = (org.eclipse.uml2.uml.Class)eResolveProxy(oldSuperClass);
            if (superClass != oldSuperClass) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TargetModelPackage.TCLASS__SUPER_CLASS, oldSuperClass, superClass));
            }
        }
        return superClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public org.eclipse.uml2.uml.Class basicGetSuperClass() {
        return superClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSuperClass(org.eclipse.uml2.uml.Class newSuperClass) {
        org.eclipse.uml2.uml.Class oldSuperClass = superClass;
        superClass = newSuperClass;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TargetModelPackage.TCLASS__SUPER_CLASS, oldSuperClass, superClass));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Interface> getSuperInterfaces() {
        if (superInterfaces == null) {
            superInterfaces = new EObjectResolvingEList<Interface>(Interface.class, this, TargetModelPackage.TCLASS__SUPER_INTERFACES);
        }
        return superInterfaces;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<TProperty> getProperties() {
        if (properties == null) {
            properties = new EObjectContainmentEList<TProperty>(TProperty.class, this, TargetModelPackage.TCLASS__PROPERTIES);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<TOperation> getOperations() {
        if (operations == null) {
            operations = new EObjectContainmentEList<TOperation>(TOperation.class, this, TargetModelPackage.TCLASS__OPERATIONS);
        }
        return operations;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case TargetModelPackage.TCLASS__PROPERTIES:
                return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
            case TargetModelPackage.TCLASS__OPERATIONS:
                return ((InternalEList<?>)getOperations()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case TargetModelPackage.TCLASS__NAME:
                return getName();
            case TargetModelPackage.TCLASS__SUPER_CLASS:
                if (resolve) return getSuperClass();
                return basicGetSuperClass();
            case TargetModelPackage.TCLASS__SUPER_INTERFACES:
                return getSuperInterfaces();
            case TargetModelPackage.TCLASS__PROPERTIES:
                return getProperties();
            case TargetModelPackage.TCLASS__OPERATIONS:
                return getOperations();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case TargetModelPackage.TCLASS__NAME:
                setName((String)newValue);
                return;
            case TargetModelPackage.TCLASS__SUPER_CLASS:
                setSuperClass((org.eclipse.uml2.uml.Class)newValue);
                return;
            case TargetModelPackage.TCLASS__SUPER_INTERFACES:
                getSuperInterfaces().clear();
                getSuperInterfaces().addAll((Collection<? extends Interface>)newValue);
                return;
            case TargetModelPackage.TCLASS__PROPERTIES:
                getProperties().clear();
                getProperties().addAll((Collection<? extends TProperty>)newValue);
                return;
            case TargetModelPackage.TCLASS__OPERATIONS:
                getOperations().clear();
                getOperations().addAll((Collection<? extends TOperation>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case TargetModelPackage.TCLASS__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TargetModelPackage.TCLASS__SUPER_CLASS:
                setSuperClass((org.eclipse.uml2.uml.Class)null);
                return;
            case TargetModelPackage.TCLASS__SUPER_INTERFACES:
                getSuperInterfaces().clear();
                return;
            case TargetModelPackage.TCLASS__PROPERTIES:
                getProperties().clear();
                return;
            case TargetModelPackage.TCLASS__OPERATIONS:
                getOperations().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case TargetModelPackage.TCLASS__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TargetModelPackage.TCLASS__SUPER_CLASS:
                return superClass != null;
            case TargetModelPackage.TCLASS__SUPER_INTERFACES:
                return superInterfaces != null && !superInterfaces.isEmpty();
            case TargetModelPackage.TCLASS__PROPERTIES:
                return properties != null && !properties.isEmpty();
            case TargetModelPackage.TCLASS__OPERATIONS:
                return operations != null && !operations.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //TClassImpl
