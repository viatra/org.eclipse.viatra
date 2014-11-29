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

import java.util.List;

import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator;

import com.google.common.collect.Lists;

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
        ExpressionTree tree = buildTree(expression);
        while (!done(tree)) {
            decomposeComplexLeaves(tree);
        }
        return tree;
    }

    private ExpressionTree buildTree(ComplexEventExpression expression) {
        if (expression instanceof Atom) {
            return new AtomicExpressionTree((Atom) expression);
        }

        if (expressionInParenthesis(expression)) {
            return buildTree(expression.getLeft()); // FIXME multiplicity and timewindows should be handled
        }

        ExpressionTree tree = new ExpressionTree();

        ComplexEventOperator lastOperator = expression.getRight() != null ? expression.getRight().get(0).getOperator()
                : null;
        List<ComplexEventExpression> currentExpressionGroup = Lists.newArrayList();

        ComplexEventExpression head = expression.getLeft();
        int headMultiplicity = (head.getMultiplicity() != null) ? head.getMultiplicity().getValue() : 1;

        for (int i = 0; i < headMultiplicity; i++) {
            currentExpressionGroup.add(head);
        }

        for (ChainedExpression che : expression.getRight()) {
            if (!sameOperators(che.getOperator(), lastOperator) || untilIntroducedCurrying(currentExpressionGroup, che)) {
                packageCurrentPatternGroup(lastOperator, currentExpressionGroup, tree);
                currentExpressionGroup.clear();
                lastOperator = che.getOperator();
            }

            ComplexEventExpression tail = che.getExpression();
            int tailMultiplicity = 1;
            if (tail.getMultiplicity() != null) {
                tailMultiplicity = tail.getMultiplicity().getValue();
            }
            for (int i = 0; i < tailMultiplicity; i++) {
                currentExpressionGroup.add(tail);
            }
        }

        packageCurrentPatternGroup(lastOperator, currentExpressionGroup, tree);

        return tree;
    }

    private boolean expressionInParenthesis(ComplexEventExpression expression) {
        return expression.getLeft() != null && (expression.getRight() == null || expression.getRight().isEmpty());
    }

    // UNTIL is binary, thus needs to be curried one by one
    private boolean untilIntroducedCurrying(List<ComplexEventExpression> currentExpressionGroup, ChainedExpression che) {
        return (currentExpressionGroup.size() > 1) && (che.getOperator() instanceof UntilOperator);
    }

    private void packageCurrentPatternGroup(ComplexEventOperator lastOperator,
            List<ComplexEventExpression> currentExpressionGroup, ExpressionTree tree) {
        Node node = new Node(lastOperator);
        if (tree.getRoot() == null) {
            tree.setRoot(node);
        } else {
            node.addChild(tree.getRoot());
            tree.setRoot(node);
        }

        for (ComplexEventExpression complexEventExpression : currentExpressionGroup) {
            Leaf leaf = new Leaf(complexEventExpression);
            node.addChild(leaf);
            if (!(complexEventExpression instanceof Atom)) {
                tree.getComplexLeaves().add(leaf);
            }
        }
    }

    private void decomposeComplexLeaves(ExpressionTree parentTree) {
        for (Leaf leaf : parentTree.getComplexLeaves()) {
            Node parentNode = leaf.getParentNode();

            int leafPosition = parentNode.getChildren().indexOf(leaf);

            // decompose
            ExpressionTree subTree = buildTree(leaf.getExpression());

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
