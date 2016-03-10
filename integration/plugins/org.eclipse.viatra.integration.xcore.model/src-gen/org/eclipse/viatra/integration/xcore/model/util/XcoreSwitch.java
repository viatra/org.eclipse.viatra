/**
 */
package org.eclipse.viatra.integration.xcore.model.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XModelElement;
import org.eclipse.emf.ecore.xcore.XNamedElement;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.XStructuralFeature;
import org.eclipse.emf.ecore.xcore.XTypedElement;

import org.eclipse.viatra.integration.xcore.model.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.integration.xcore.model.XcorePackage
 * @generated
 */
public class XcoreSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static XcorePackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XcoreSwitch() {
        if (modelPackage == null) {
            modelPackage = XcorePackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case XcorePackage.XVIATRA_QUERY_DERIVED_FEATURE: {
                XViatraQueryDerivedFeature xViatraQueryDerivedFeature = (XViatraQueryDerivedFeature)theEObject;
                T result = caseXViatraQueryDerivedFeature(xViatraQueryDerivedFeature);
                if (result == null) result = caseXStructuralFeature(xViatraQueryDerivedFeature);
                if (result == null) result = caseXMember(xViatraQueryDerivedFeature);
                if (result == null) result = caseXTypedElement(xViatraQueryDerivedFeature);
                if (result == null) result = caseXNamedElement(xViatraQueryDerivedFeature);
                if (result == null) result = caseXModelElement(xViatraQueryDerivedFeature);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XcorePackage.XVIATRA_QUERY_PACKAGE: {
                XViatraQueryPackage xViatraQueryPackage = (XViatraQueryPackage)theEObject;
                T result = caseXViatraQueryPackage(xViatraQueryPackage);
                if (result == null) result = caseXPackage(xViatraQueryPackage);
                if (result == null) result = caseXNamedElement(xViatraQueryPackage);
                if (result == null) result = caseXModelElement(xViatraQueryPackage);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case XcorePackage.XVIATRA_QUERY_IMPORT: {
                XViatraQueryImport xViatraQueryImport = (XViatraQueryImport)theEObject;
                T result = caseXViatraQueryImport(xViatraQueryImport);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XViatra Query Derived Feature</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XViatra Query Derived Feature</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXViatraQueryDerivedFeature(XViatraQueryDerivedFeature object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XViatra Query Package</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XViatra Query Package</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXViatraQueryPackage(XViatraQueryPackage object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XViatra Query Import</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XViatra Query Import</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXViatraQueryImport(XViatraQueryImport object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XModel Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XModel Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXModelElement(XModelElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XNamed Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XNamed Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXNamedElement(XNamedElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XTyped Element</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XTyped Element</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXTypedElement(XTypedElement object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XMember</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XMember</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXMember(XMember object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XStructural Feature</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XStructural Feature</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXStructuralFeature(XStructuralFeature object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>XPackage</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>XPackage</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseXPackage(XPackage object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //XcoreSwitch
