/**
 */
package org.eclipse.incquery.examples.eiqlibrary.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.eclipse.incquery.examples.eiqlibrary.EIQLibraryFactory;
import org.eclipse.incquery.examples.eiqlibrary.Writer;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Writer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are tested:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Writer#getFirstBook() <em>First Book</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.eiqlibrary.Writer#getScifiBooks() <em>Scifi Books</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class WriterTest extends TestCase {

    /**
     * The fixture for this Writer test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Writer fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(WriterTest.class);
    }

    /**
     * Constructs a new Writer test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WriterTest(String name) {
        super(name);
    }

    /**
     * Sets the fixture for this Writer test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture(Writer fixture) {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Writer test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Writer getFixture() {
        return fixture;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#setUp()
     * @generated
     */
    @Override
    protected void setUp() throws Exception {
        setFixture(EIQLibraryFactory.eINSTANCE.createWriter());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#tearDown()
     * @generated
     */
    @Override
    protected void tearDown() throws Exception {
        setFixture(null);
    }

    /**
     * Tests the '{@link org.eclipse.incquery.examples.eiqlibrary.Writer#getFirstBook() <em>First Book</em>}' feature getter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.incquery.examples.eiqlibrary.Writer#getFirstBook()
     * @generated
     */
    public void testGetFirstBook() {
        // TODO: implement this feature getter test method
        // Ensure that you remove @generated or mark it @generated NOT
        fail();
    }

    /**
     * Tests the '{@link org.eclipse.incquery.examples.eiqlibrary.Writer#getScifiBooks() <em>Scifi Books</em>}' feature getter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.incquery.examples.eiqlibrary.Writer#getScifiBooks()
     * @generated
     */
    public void testGetScifiBooks() {
        // TODO: implement this feature getter test method
        // Ensure that you remove @generated or mark it @generated NOT
        fail();
    }

} //WriterTest
