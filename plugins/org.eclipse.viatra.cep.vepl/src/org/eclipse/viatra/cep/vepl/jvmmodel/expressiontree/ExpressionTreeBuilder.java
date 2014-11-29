/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.OrOperator;
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator;

import com.google.common.collect.Maps;

/**
 * Algorithm for building the {@link ExpressionTree} from the parsed Xtext model.
 * 
 * @author Istvan David
 * 
 */
public class ExpressionTreeBuilder {
    private static ExpressionTreeBuilder instance;

    public static ExpressionTreeBuilder getInstance() {
        if (instance == null) {
            instance = new ExpressionTreeBuilder();
        }
        return instance;
    }

    public ExpressionTree buildExpressionTree(ComplexEventExpression expression) {
        ExpressionTree tree = buildTree(expression, 1);
        while (!done(tree)) {
            decomposeComplexLeaves(tree);
        }
        return tree;
    }

    private int getMultiplicity(ComplexEventExpression expression) {
        if (expression.getMultiplicity() == null) {
            return 1;
        }
        return expression.getMultiplicity().getValue();
    }

    private ExpressionTree buildTree(ComplexEventExpression expression, int multiplicity) {
        if (expression instanceof Atom) {
            return new AtomicExpressionTree((Atom) expression, getMultiplicity(expression));
        }

        if (expressionInParenthesis(expression)) {
            return buildTree(expression.getLeft(), getMultiplicity(expression)); // FIXME timewindows should be handled
        }

        ExpressionTree tree = new ExpressionTree();

        ComplexEventOperator lastOperator = expression.getRight() != null ? expression.getRight().get(0).getOperator()
                : null;
        Map<ComplexEventExpression, Integer> currentExpressionGroup = Maps.newLinkedHashMap();

        ComplexEventExpression head = expression.getLeft();
        currentExpressionGroup.put(head, getMultiplicity(head));

        for (ChainedExpression che : expression.getRight()) {
            if (!sameOperators(che.getOperator(), lastOperator) || untilIntroducedCurrying(currentExpressionGroup, che)) {
                packagePatternGroup(lastOperator, 1, currentExpressionGroup, tree);
                currentExpressionGroup.clear();
                lastOperator = che.getOperator();
            }

            ComplexEventExpression tail = che.getExpression();
            currentExpressionGroup.put(tail, getMultiplicity(tail));
        }

        packagePatternGroup(lastOperator, multiplicity, currentExpressionGroup, tree);

        return tree;
    }

    private boolean expressionInParenthesis(ComplexEventExpression expression) {
        return expression.getLeft() != null && (expression.getRight() == null || expression.getRight().isEmpty());
    }

    // UNTIL is binary, thus needs to be curried one by one
    private boolean untilIntroducedCurrying(Map<ComplexEventExpression, Integer> currentExpressionGroup,
            ChainedExpression che) {
        return (currentExpressionGroup.size() > 1) && (che.getOperator() instanceof UntilOperator);
    }

    private void packagePatternGroup(ComplexEventOperator operator, int multiplicity,
            Map<ComplexEventExpression, Integer> expressionGroup, ExpressionTree tree) {
        Node node = new Node(operator, multiplicity);
        if (tree.getRoot() == null) {
            tree.setRoot(node);
        } else {
            node.addChild(tree.getRoot());
            tree.setRoot(node);
        }

        Map<ComplexEventExpression, Integer> reducedGroup = reduceExpressionGroup(operator, expressionGroup);

        for (Entry<ComplexEventExpression, Integer> entry : reducedGroup.entrySet()) {
            Leaf leaf = new Leaf(entry.getKey(), entry.getValue());
            node.addChild(leaf);
            if (!(entry.getKey() instanceof Atom)) {
                tree.getComplexLeaves().add(leaf);
            }
        }
    }

    private Map<ComplexEventExpression, Integer> reduceExpressionGroup(ComplexEventOperator operator,
            Map<ComplexEventExpression, Integer> expressionGroup) {
        Map<ComplexEventExpression, Integer> reducedGroup = Maps.newLinkedHashMap();

        for (Entry<ComplexEventExpression, Integer> entry : expressionGroup.entrySet()) {
            int multiplicity = (operator instanceof OrOperator) ? 1 : entry.getValue();
            reducedGroup.put(entry.getKey(), multiplicity);
        }

        return reducedGroup;
    }

    private void decomposeComplexLeaves(ExpressionTree parentTree) {
        for (Leaf leaf : parentTree.getComplexLeaves()) {
            Node parentNode = leaf.getParentNode();

            int leafPosition = parentNode.getChildren().indexOf(leaf);

            // decompose
            ExpressionTree subTree = buildTree(leaf.getExpression(), leaf.getMultiplicity());

            // replace
            parentNode.getChildren().set(leafPosition, subTree.getRoot());
            subTree.getRoot().setParentNode(parentNode);

            // update global complex leaf list
            for (Leaf complexLeafOfTheSubTree : subTree.getComplexLeaves()) {
                parentTree.getComplexLeaves().add(complexLeafOfTheSubTree);
            }

            // remove obsolete leaf
            parentTree.getComplexLeaves().remove(leaf);
            parentNode.getChildren().remove(leaf);
        }
    }

    private boolean sameOperators(ComplexEventOperator operator1, ComplexEventOperator operator2) {
        return operator1.getClass().equals(operator2.getClass());
    }

    private boolean done(ExpressionTree tree) {
        return tree.getComplexLeaves().isEmpty();
    }
}
