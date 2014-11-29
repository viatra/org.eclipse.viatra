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

import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.OrOperator;
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
        ExpressionTree tree = buildTree(expression, getMultiplicity(expression), getTimeWindow(expression));
        while (!done(tree)) {
            decomposeComplexLeaves(tree);
        }
        return tree;
    }

    private TimeWindow getTimeWindow(ComplexEventExpression expression) {
        if (expression.getTimewindow() != null) {
            TimeWindow timeWindow = EventsFactory.eINSTANCE.createTimeWindow();
            timeWindow.setTime(expression.getTimewindow().getLength());
            return timeWindow;
        }
        return null;
    }

    private int getMultiplicity(ComplexEventExpression expression) {
        if (expression.getMultiplicity() == null) {
            return 1;
        }
        return expression.getMultiplicity().getValue();
    }

    private ExpressionTree buildTree(ComplexEventExpression expression, int multiplicity, TimeWindow timeWindow) {
        if (expression instanceof Atom) {
            return new AtomicExpressionTree((Atom) expression, getMultiplicity(expression), getTimeWindow(expression));
        }

        if (expressionInParenthesis(expression)) {
            return buildTree(expression.getLeft(), getMultiplicity(expression.getLeft()),
                    getTimeWindow(expression.getLeft()));
        }

        ExpressionTree tree = new ExpressionTree();

        ComplexEventOperator lastOperator = expression.getRight() != null ? expression.getRight().get(0).getOperator()
                : null;
        List<ExpressionGroupElement> currentExpressionGroup = Lists.newArrayList();

        ComplexEventExpression head = expression.getLeft();
        currentExpressionGroup.add(new ExpressionGroupElement(head, getMultiplicity(head), getTimeWindow(head)));

        for (ChainedExpression che : expression.getRight()) {
            if (!sameOperators(che.getOperator(), lastOperator) || untilIntroducedCurrying(currentExpressionGroup, che)) {
                packagePatternGroup(lastOperator, 1, timeWindow, currentExpressionGroup, tree);
                currentExpressionGroup.clear();
                lastOperator = che.getOperator();
            }

            ComplexEventExpression tail = che.getExpression();
            currentExpressionGroup.add(new ExpressionGroupElement(tail, getMultiplicity(tail), getTimeWindow(tail)));
        }

        packagePatternGroup(lastOperator, multiplicity, timeWindow, currentExpressionGroup, tree);

        return tree;
    }

    private boolean expressionInParenthesis(ComplexEventExpression expression) {
        return expression.getLeft() != null && (expression.getRight() == null || expression.getRight().isEmpty());
    }

    // UNTIL is binary, thus needs to be curried one by one
    private boolean untilIntroducedCurrying(List<ExpressionGroupElement> currentExpressionGroup, ChainedExpression che) {
        return (currentExpressionGroup.size() > 1) && (che.getOperator() instanceof UntilOperator);
    }

    private void packagePatternGroup(ComplexEventOperator operator, int multiplicity, TimeWindow timewindow,
            List<ExpressionGroupElement> expressionGroup, ExpressionTree tree) {
        Node node = new Node(operator, multiplicity, timewindow);
        if (tree.getRoot() == null) {
            tree.setRoot(node);
        } else {
            node.addChild(tree.getRoot());
            tree.setRoot(node);
        }

        List<ExpressionGroupElement> reducedGroup = reduceExpressionGroup(operator, expressionGroup);

        for (ExpressionGroupElement groupElement : reducedGroup) {
            Leaf leaf = new Leaf(groupElement.getComplexEventExpression(), groupElement.getMultiplicity(),
                    groupElement.getTimeWindow());
            node.addChild(leaf);
            if (!(groupElement.getComplexEventExpression() instanceof Atom)) {
                tree.getComplexLeaves().add(leaf);
            }
        }
    }

    private List<ExpressionGroupElement> reduceExpressionGroup(ComplexEventOperator operator,
            List<ExpressionGroupElement> expressionGroup) {
        List<ExpressionGroupElement> reducedGroup = Lists.newArrayList();

        for (ExpressionGroupElement groupElement : expressionGroup) {
            int multiplicity = (operator instanceof OrOperator) ? 1 : groupElement.getMultiplicity();
            groupElement.setMultiplicity(multiplicity);
            reducedGroup.add(groupElement);
        }

        return reducedGroup;
    }

    private void decomposeComplexLeaves(ExpressionTree parentTree) {
        for (Leaf leaf : parentTree.getComplexLeaves()) {
            Node parentNode = leaf.getParentNode();

            int leafPosition = parentNode.getChildren().indexOf(leaf);

            // decompose
            ExpressionTree subTree = buildTree(leaf.getExpression(), leaf.getMultiplicity(), leaf.getTimeWindow());

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
