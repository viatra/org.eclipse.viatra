/**
 */
package org.eclipse.incquery.xcore.model.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.xcore.XMember;
import org.eclipse.emf.ecore.xcore.XModelElement;
import org.eclipse.emf.ecore.xcore.XNamedElement;
import org.eclipse.emf.ecore.xcore.XPackage;
import org.eclipse.emf.ecore.xcore.XStructuralFeature;
import org.eclipse.emf.ecore.xcore.XTypedElement;

import org.eclipse.incquery.xcore.model.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.incquery.xcore.model.XcorePackage
 * @generated
 */
public class XcoreAdapterFactory extends AdapterFactoryImpl {
	/**
     * The cached model package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected static XcorePackage modelPackage;

	/**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XcoreAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = XcorePackage.eINSTANCE;
        }
    }

	/**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
	@Override
	public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

	/**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected XcoreSwitch<Adapter> modelSwitch =
		new XcoreSwitch<Adapter>() {
            @Override
            public Adapter caseXIncQueryDerivedFeature(XIncQueryDerivedFeature object) {
                return createXIncQueryDerivedFeatureAdapter();
            }
            @Override
            public Adapter caseXIncQueryPackage(XIncQueryPackage object) {
                return createXIncQueryPackageAdapter();
            }
            @Override
            public Adapter caseXIncQueryImport(XIncQueryImport object) {
                return createXIncQueryImportAdapter();
            }
            @Override
            public Adapter caseXModelElement(XModelElement object) {
                return createXModelElementAdapter();
            }
            @Override
            public Adapter caseXNamedElement(XNamedElement object) {
                return createXNamedElementAdapter();
            }
            @Override
            public Adapter caseXTypedElement(XTypedElement object) {
                return createXTypedElementAdapter();
            }
            @Override
            public Adapter caseXMember(XMember object) {
                return createXMemberAdapter();
            }
            @Override
            public Adapter caseXStructuralFeature(XStructuralFeature object) {
                return createXStructuralFeatureAdapter();
            }
            @Override
            public Adapter caseXPackage(XPackage object) {
                return createXPackageAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

	/**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
	@Override
	public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.incquery.xcore.model.XIncQueryDerivedFeature <em>XInc Query Derived Feature</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.incquery.xcore.model.XIncQueryDerivedFeature
     * @generated
     */
	public Adapter createXIncQueryDerivedFeatureAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.incquery.xcore.model.XIncQueryPackage <em>XInc Query Package</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.incquery.xcore.model.XIncQueryPackage
     * @generated
     */
	public Adapter createXIncQueryPackageAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.incquery.xcore.model.XIncQueryImport <em>XInc Query Import</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.incquery.xcore.model.XIncQueryImport
     * @generated
     */
	public Adapter createXIncQueryImportAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XModelElement <em>XModel Element</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XModelElement
     * @generated
     */
	public Adapter createXModelElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XNamedElement <em>XNamed Element</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XNamedElement
     * @generated
     */
	public Adapter createXNamedElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XTypedElement <em>XTyped Element</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XTypedElement
     * @generated
     */
	public Adapter createXTypedElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XMember <em>XMember</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XMember
     * @generated
     */
	public Adapter createXMemberAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XStructuralFeature <em>XStructural Feature</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XStructuralFeature
     * @generated
     */
    public Adapter createXStructuralFeatureAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.ecore.xcore.XPackage <em>XPackage</em>}'.
     * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.ecore.xcore.XPackage
     * @generated
     */
	public Adapter createXPackageAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
	public Adapter createEObjectAdapter() {
        return null;
    }

} //XcoreAdapterFactory
