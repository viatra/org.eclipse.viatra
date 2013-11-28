/**
 */
package org.eclipse.incquery.examples.eiqlibrary.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>eiqlibrary</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class EIQLibraryTests extends TestSuite {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Test suite() {
        TestSuite suite = new EIQLibraryTests("eiqlibrary Tests");
        suite.addTestSuite(LibraryTest.class);
        suite.addTestSuite(WriterTest.class);
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EIQLibraryTests(String name) {
        super(name);
    }

} //EIQLibraryTests
