/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>EMF Internal Traversal State</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#getEMFInternalTraversalState()
 * @model
 * @generated
 */
public enum EMFInternalTraversalState implements Enumerator {
    /**
     * The '<em><b>NOT YET PROCESSED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NOT_YET_PROCESSED_VALUE
     * @generated
     * @ordered
     */
    NOT_YET_PROCESSED(0, "NOT_YET_PROCESSED", "NOT_YET_PROCESSED"),

    /**
     * The '<em><b>TRAVERSED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TRAVERSED_VALUE
     * @generated
     * @ordered
     */
    TRAVERSED(1, "TRAVERSED", "TRAVERSED"),

    /**
     * The '<em><b>CUT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CUT_VALUE
     * @generated
     * @ordered
     */
    CUT(2, "CUT", "CUT"),

    /**
     * The '<em><b>GOAL</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #GOAL_VALUE
     * @generated
     * @ordered
     */
    GOAL(3, "GOAL", "GOAL");

    /**
     * The '<em><b>NOT YET PROCESSED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NOT YET PROCESSED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NOT_YET_PROCESSED
     * @model
     * @generated
     * @ordered
     */
    public static final int NOT_YET_PROCESSED_VALUE = 0;

    /**
     * The '<em><b>TRAVERSED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TRAVERSED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TRAVERSED
     * @model
     * @generated
     * @ordered
     */
    public static final int TRAVERSED_VALUE = 1;

    /**
     * The '<em><b>CUT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CUT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CUT
     * @model
     * @generated
     * @ordered
     */
    public static final int CUT_VALUE = 2;

    /**
     * The '<em><b>GOAL</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>GOAL</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #GOAL
     * @model
     * @generated
     * @ordered
     */
    public static final int GOAL_VALUE = 3;

    /**
     * An array of all the '<em><b>EMF Internal Traversal State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final EMFInternalTraversalState[] VALUES_ARRAY =
        new EMFInternalTraversalState[] {
            NOT_YET_PROCESSED,
            TRAVERSED,
            CUT,
            GOAL,
        };

    /**
     * A public read-only list of all the '<em><b>EMF Internal Traversal State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<EMFInternalTraversalState> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>EMF Internal Traversal State</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EMFInternalTraversalState get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            EMFInternalTraversalState result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>EMF Internal Traversal State</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EMFInternalTraversalState getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            EMFInternalTraversalState result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>EMF Internal Traversal State</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EMFInternalTraversalState get(int value) {
        switch (value) {
            case NOT_YET_PROCESSED_VALUE: return NOT_YET_PROCESSED;
            case TRAVERSED_VALUE: return TRAVERSED;
            case CUT_VALUE: return CUT;
            case GOAL_VALUE: return GOAL;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EMFInternalTraversalState(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
      return value;
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
    public String getLiteral() {
      return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }
    
} //EMFInternalTraversalState
